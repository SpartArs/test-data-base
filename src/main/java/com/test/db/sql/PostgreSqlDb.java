package com.test.db.sql;

import com.test.db.util.Constants;
import com.test.db.util.PropertiesUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PostgreSqlDb extends SqlDb {

    private static final String POSTGRES_PROPERTIES_FILE = "src/main/resources/postgres.db.properties";

    public PostgreSqlDb() {
        try {
            FileInputStream fileInputStream = new FileInputStream(POSTGRES_PROPERTIES_FILE);
            Properties property = PropertiesUtil.getProperties(fileInputStream);
            property.load(fileInputStream);
            Class.forName(property.getProperty(Constants.DB_DRIVER));
            this.host = property.getProperty(Constants.URL);
            this.login = property.getProperty(Constants.LOGIN);
            this.password = property.getProperty(Constants.PASSWORD);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
