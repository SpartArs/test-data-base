package com.test.db;

import com.test.db.sql.PostgreSqlDb;

public class StartPostgreSqlTest {

    public static void main(String[] args) throws InterruptedException {
        ThreadProducerConsumer startPostgreSql = new ThreadProducerConsumer(new PostgreSqlDb());
        startPostgreSql.startPcThread();
//        startPostgreSql.deleteAll();
    }
}
