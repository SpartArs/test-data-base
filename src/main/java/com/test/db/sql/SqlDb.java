package com.test.db.sql;

import com.test.db.DatabaseManager;
import com.test.db.DatabaseType;
import com.test.db.model.DatabaseOperationDto;
import com.test.db.model.Historical;
import com.test.db.util.Constants;
import com.test.db.util.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Slf4j
public abstract class SqlDb implements DatabaseManager {

    private static int COUNT = 0;
    private static int totalCount = 0;
    private static int fileInsertRowNumber;
    private static int fileSelectRowNumber;

    private static DatabaseType databaseType;

    protected static String SQL_SELECT_ALL = "SELECT * from historical";
    protected static String SQL_SELECT_HISTORIC = "SELECT sampleId, stepEnd FROM historical\n" +
            "WHERE stepEnd = (SELECT MAX(stepEnd) FROM historical);";

    protected static String SQL_SELECT_BY_DATE = "SELECT * from historical WHERE stepEnd > ?";
    protected static String SQL_INSERT = "INSERT INTO historical " +
            "(laboratoryId, sampleId, stepId, stepBegin, stepEnd)" +
            " VALUES (?, ?, ?, ?, ?)";

    protected static String SQL_DELETE_ALL = "DELETE from historical";

    protected String host;
    protected String login;
    protected String password;

    public SqlDb() {
        if (this instanceof MySqlDb) {
            databaseType = DatabaseType.MYSQL;
        } else if (this instanceof PostgreSqlDb) {
            databaseType = DatabaseType.POSTGRES;
        }
    }

    @Override
    public void insert(List<Historical> historicals) {
        try (Connection connection = DriverManager.getConnection(host, login, password);
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {

            historicals.forEach(historical -> {
                try {
                    connection.setAutoCommit(false);
                    preparedStatement.setLong(1, historical.getLaboratoryId());
                    preparedStatement.setLong(2, historical.getSampleId());
                    preparedStatement.setLong(3, historical.getStepId());
                    preparedStatement.setDate(4, new java.sql.Date(historical.getStepBegin().getTime()));
                    preparedStatement.setDate(5, new java.sql.Date(historical.getStepEnd().getTime()));
                    preparedStatement.addBatch();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            preparedStatement.executeBatch();
            long start = System.nanoTime();
            connection.commit();
            long end = System.nanoTime();
            double resultTime = (end - start) / 1000000000d;

            writeToFile(historicals.size(), resultTime, 0, 0, true);

            log.info("INSERT TIME: " + String.format("%.6f", resultTime) + " seconds");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public void selectHistoric() {
        try (Connection connection = DriverManager.getConnection(host, login, password);
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_HISTORIC)) {

            long start = System.nanoTime();
            ResultSet resultSet = preparedStatement.executeQuery();
            long end = System.nanoTime();
            double resultTime = (end - start) / 1000000000d;

            int count = 0;
            while (resultSet.next()) {
                count++;
            }

            log.info("----------------------------------------------");
            log.info("SELECT COUNT:  " + count);
            log.info("SELECT TIME: " + String.format("%.6f", resultTime) + " seconds");
            log.info("----------------------------------------------");

            writeToFile(0, 0, count, resultTime, false);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Historical> select(Date date) {
        List<Historical> result = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(host, login, password);
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_BY_DATE)) {

            preparedStatement.setDate(1, new java.sql.Date(date.getTime()));
            long start = System.nanoTime();
            ResultSet resultSet = preparedStatement.executeQuery();
            long end = System.nanoTime();
            double resultTime = (end - start) / 1000000000d;

            while (resultSet.next()) {
                Historical historical = buildHistorical(resultSet);
                result.add(historical);
            }

            writeToFile(0, 0, result.size(), resultTime, false);

            log.info("----------------------------------------------");
            log.info("SELECT COUNT:  " + result.size());
            log.info("SELECT TIME: " + String.format("%.3f", resultTime) + " seconds");
            log.info("----------------------------------------------");

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    @Override
    public List<Historical> selectAll() {
        List<Historical> result = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(host, login, password);
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ALL)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Historical historical = buildHistorical(resultSet);
                result.add(historical);
            }

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    @Override
    public void deleteAll() {
        try (Connection connection = DriverManager.getConnection(host, login, password);
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_ALL)) {

            preparedStatement.execute();

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeToFile(int insertCount, double insertTime, int selectCount, double selectTime, boolean isInsert) {
        DatabaseOperationDto dbOperationDto = DatabaseOperationDto.builder()
                .totalCount(selectAll().size())
                .insertCount(insertCount)
                .insertTime(new BigDecimal(insertTime).setScale(6, RoundingMode.HALF_UP).doubleValue())
                .selectCount(selectCount)
                .selectTime(new BigDecimal(selectTime).setScale(6, RoundingMode.HALF_UP).doubleValue())
                .build();

        int rowNumber = isInsert ? ++fileInsertRowNumber : ++fileSelectRowNumber;

        try {
            FileUtil.writeToXlsx(dbOperationDto, rowNumber, databaseType, isInsert);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Historical buildHistorical(ResultSet resultSet) throws SQLException {
        return Historical.builder()
                .laboratoryId(resultSet.getLong(Constants.LABORATORY_ID))
                .sampleId(resultSet.getLong(Constants.SAMPLE_ID))
                .stepId(resultSet.getLong(Constants.STEP_ID))
                .stepBegin(resultSet.getDate(Constants.STEP_BEGIN))
                .stepEnd(resultSet.getDate(Constants.STEP_END))
                .build();
    }

}
