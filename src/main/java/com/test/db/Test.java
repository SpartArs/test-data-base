package com.test.db;

import com.test.db.model.Historical;
import com.test.db.mongo.MongoDb;
import com.test.db.sql.MySqlDb;
import com.test.db.sql.PostgreSqlDb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        MongoDb mongoDb = new MongoDb();
        mongoDb.selectHistoric();
//        mongoDb.deleteAll();
//        insert(mongoDb);
//        MySqlDb mySqlDb = new MySqlDb();
//        List<Historical> historicals = mongoDb.selectAll();
//        System.out.println(historicals.size());
//        for (int i = 1900900; i < 2000000; i++) {
//            System.out.println(historicals.get(i));
//        }
//        historicals.forEach(historical -> {
//            System.out.println(historical.getSampleId());
//        });

//        List<Historical> historicals1 = mongoDb.selectHistoric();
//        System.out.println(historicals1.size());

//        MySqlDb mySqlDb = new MySqlDb();
//        mySqlDb.deleteAll();
//        insert(mySqlDb);
//        List<Historical> historicals = mySqlDb.selectAll();
//        System.out.println(historicals.size());
//
//        mySqlDb.selectHistoric();

//        PostgreSqlDb postgreSqlDb = new PostgreSqlDb();
//        insert(postgreSqlDb);
//        postgreSqlDb.deleteAll();
//        List<Historical> historicals = postgreSqlDb.selectAll();
//        System.out.println(historicals.size());
//        postgreSqlDb.selectHistoric();


    }

    private static void insert(DatabaseManager databaseManager) {
        List<Historical> historicalList = createItems();

        for (int i = 1; i <= 20; i++) {
            for (int j = 0; j < 100000; j++) {
                Calendar instance = Calendar.getInstance();
                instance.setTime(new Date());
                instance.add(Calendar.DATE, i);
                if (!historicalList.isEmpty()) {
                    Historical historical = historicalList.get(j);
                    historical.setStepId(i);
                    historical.setStepBegin(new Date());
                    historical.setStepEnd(instance.getTime());
                    historicalList.add(Historical.builder()
                            .laboratoryId(1)
                            .sampleId(historicalList.get(j).getSampleId())
                            .stepId(i)
                            .stepBegin(new Date())
                            .stepEnd(instance.getTime())
                            .build());
                }
            }
        }

        databaseManager.insert(historicalList);
    }

    private static List<Historical> createItems() {
        List<Historical> historicalList = new ArrayList<>();
        int id = 1000000;

        for (int i = 1; i <= 100000; i++) {
            historicalList.add(Historical.builder()
                    .laboratoryId(1)
                    .sampleId(id)
                    .build());

            id++;
        }

        return historicalList;
    }
}
