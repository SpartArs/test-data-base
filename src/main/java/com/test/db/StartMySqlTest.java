package com.test.db;

import com.test.db.sql.MySqlDb;

public class StartMySqlTest {

    public static void main(String[] args) throws InterruptedException {
            ThreadProducerConsumer startMySql = new ThreadProducerConsumer(new MySqlDb());
            startMySql.startPcThread();
//            startMySql.deleteAll();
    }
}
