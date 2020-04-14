package com.test.db;

import com.test.db.model.Historical;

import java.util.Date;
import java.util.List;

public interface DatabaseManager {
    String CSV_FILE_NAME = "result.csv";

    void insert(List<Historical> forInsertList);

    List<Historical> select(Date date);

    List<Historical> selectAll();

    void selectHistoric();

    void deleteAll();
}
