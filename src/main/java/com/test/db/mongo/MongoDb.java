package com.test.db.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.test.db.DatabaseManager;
import com.test.db.DatabaseType;
import com.test.db.model.DatabaseOperationDto;
import com.test.db.model.Historical;
import com.test.db.util.Constants;
import com.test.db.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
public class MongoDb implements DatabaseManager {

    private static final String URL = "mongodb://127.0.0.1:27017/connect?compressors=disabled&gssapiServiceName=mongodb";
    private static final String DB_NAME = "historical";
    private MongoCollection<Document> collection;
    private static int totalCount = 0;
    private static int fileInsertRowNumber;
    private static int fileSelectRowNumber;

    public MongoDb() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        this.collection = database.getCollection(DB_NAME);
    }

    @Override
    public void insert(List<Historical> forInsertList) {
        List<Document> documents = new ArrayList<>();
        forInsertList.forEach(historical -> {
            Document document = buildDocumentByHistorical(historical);
            documents.add(document);
        });

        long start = System.nanoTime();
        collection.insertMany(documents);
        long end = System.nanoTime();

        double resultTime = (end - start)/1000000000d;
        log.info("INSERT TIME: " + String.format("%.6f", resultTime) + " seconds");

        writeToFile(forInsertList.size(), resultTime, 0, 0, true);
    }

    @Override
    public void selectHistoric() {
        long start = System.nanoTime();
        AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(
                Aggregates.group("$sampleId", Accumulators.max(Constants.STEP_END, "$stepEnd"))));
        long end = System.nanoTime();
        double resultTime = (end - start) / 1000000000d;
        MongoCursor<Document> iterator = aggregate.iterator();

        int count = 0;
        while (iterator.hasNext()) {
            count++;
            iterator.next();
        }

        log.info("----------------------------------------------");
        log.info("SELECT COUNT:  " + count);
        log.info("SELECT TIME: " + String.format("%.6f", resultTime) + " seconds");
        log.info("----------------------------------------------");

        writeToFile(0, 0, count, resultTime, false);
    }

    public void insertOne(Historical historical) {
        Document document = buildDocumentByHistorical(historical);
        collection.insertOne(document);
    }

    public List<Historical> select(Date date) {
        List<Historical> result = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();
        query.put(Constants.STEP_END, new BasicDBObject("$gt", date));

        long start = System.nanoTime();
        FindIterable<Document> documents = collection.find(query).sort(new BasicDBObject(Constants.STEP_END, -1));
        long end = System.nanoTime();
        for (Document document : documents) {
            result.add(buildHistoricalByDocument(document));
        }

        double resultTime = (end - start)/1000000000d;
        log.info("----------------------------------------------");
        log.info("SELECT COUNT:  " + result.size());
        log.info("SELECT TIME: " +  String.format("%.10f", resultTime) + " seconds");
        log.info("----------------------------------------------");

        writeToFile(0, 0, result.size(), resultTime, false);

        return result;
    }

    public List<Historical> selectAll() {
        List<Historical> result = new ArrayList<>();
        for (Document document : collection.find()) {
            result.add(buildHistoricalByDocument(document));
        }

        return result;
    }

    public void deleteAll() {
        collection.drop();
    }

    private Document buildDocumentByHistorical(Historical historical) {
        Document document = new Document(Constants.LABORATORY_ID, historical.getLaboratoryId());
        document.append(Constants.SAMPLE_ID, historical.getSampleId());
        document.append(Constants.STEP_ID, historical.getStepId());
        document.append(Constants.STEP_BEGIN, historical.getStepBegin());
        document.append(Constants.STEP_END, historical.getStepEnd());

        return document;
    }

    private Historical buildHistoricalByDocument(Document document) {
        return Historical.builder()
                .laboratoryId(document.getLong(Constants.LABORATORY_ID))
                .sampleId(document.getLong(Constants.SAMPLE_ID))
                .stepId(document.getLong(Constants.STEP_ID))
                .stepBegin(document.getDate(Constants.STEP_BEGIN))
                .stepEnd(document.getDate(Constants.STEP_END))
                .build();
    }

    private void writeToFile(int insertCount, double insertTime, int selectCount, double selectTime,boolean isInsert) {
        int rowNumber = isInsert ? ++fileInsertRowNumber : ++fileSelectRowNumber;

        DatabaseOperationDto dbOperationDto = DatabaseOperationDto.builder()
                .totalCount(selectAll().size())
                .insertCount(insertCount)
                .insertTime(new BigDecimal(insertTime).setScale(6, RoundingMode.HALF_UP).doubleValue())
                .selectCount(selectCount)
                .selectTime(new BigDecimal(selectTime).setScale(6, RoundingMode.HALF_UP).doubleValue())
                .build();

        try {
            FileUtil.writeToXlsx(dbOperationDto, rowNumber, DatabaseType.MONGO, isInsert);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }


}
