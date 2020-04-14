package com.test.db.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {

    public static Properties getProperties(FileInputStream fileInputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(fileInputStream);

        return properties;
    }
}
