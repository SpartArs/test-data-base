package com.test.db;

import lombok.Data;

public enum DatabaseType {
    MONGO("MongoDB"),
    MYSQL("MySQL"),
    POSTGRES("PostgreSQL");

    private String name;

    DatabaseType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
