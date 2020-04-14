package com.test.db;

import com.test.db.model.Historical;
import com.test.db.mongo.MongoDb;

import java.util.List;


public class StartMongoDbTest {

    public static void main(String[] args) throws InterruptedException {
        ThreadProducerConsumer startMongo = new ThreadProducerConsumer(new MongoDb());
        startMongo.startPcThread();
//        List<Historical> historicalList = startMongo.selectAll();
//        System.out.println(historicalList.size());
//        startMongo.selectHistoric();
//        startMongo.deleteAll();
    }
}