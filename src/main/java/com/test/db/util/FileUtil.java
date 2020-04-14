package com.test.db.util;

import com.test.db.DatabaseType;
import com.test.db.model.DatabaseOperationDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;

@Slf4j
public class FileUtil {

    private static final String XLSX_FILE_NAME = "result.xlsx";

    public static void writeToXlsx(DatabaseOperationDto dbOoperationDto,
                                   int fileRowNumber,
                                   DatabaseType databaseType,
                                   boolean isInsert) throws IOException {
        XSSFWorkbook workbook;
        XSSFSheet sheet;
//
//        int[] currentCells;
//        String sheetName;
//        switch (databaseType) {
//            case MONGO:
//                sheetName = databaseType.getName();
//                currentCells = mongoCells;
//                break;
//            case MYSQL:
//                sheetName = databaseType.getName();
//                currentCells = mysqlCells;
//                break;
//            case POSTGRES:
//                sheetName = databaseType.getName();
//                currentCells = postgresCells;
//                break;
//            default:
//                currentCells = new int[0];
//                break;
//        }

        File file = new File(XLSX_FILE_NAME);
        if (!file.exists()) {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet(databaseType.getName());
            createTitleRow(workbook, sheet);
        } else {
            FileInputStream inputStream = new FileInputStream(file);
            workbook = new XSSFWorkbook(inputStream);
            sheet = workbook.getSheet(databaseType.getName());
            if (sheet == null) {
                sheet = workbook.createSheet(databaseType.getName());
                createTitleRow(workbook, sheet);
            }
        }

        Row row = sheet.getRow(fileRowNumber);
        if (row == null) {
            row = sheet.createRow(fileRowNumber);
        }

        Cell cell;

        if (isInsert) {
            cell = row.createCell(0, CellType.NUMERIC);
            cell.setCellValue(dbOoperationDto.getTotalCount());

            cell = row.createCell(1, CellType.NUMERIC);
            cell.setCellValue(dbOoperationDto.getInsertCount());

            cell = row.createCell(2, CellType.NUMERIC);
            cell.setCellValue(dbOoperationDto.getInsertTime());

        } else {
            cell = row.createCell(4, CellType.NUMERIC);
            cell.setCellValue(dbOoperationDto.getTotalCount());

            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue(dbOoperationDto.getSelectCount());

            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellValue(dbOoperationDto.getSelectTime());
        }

        FileOutputStream outFile = new FileOutputStream(file);

        workbook.write(outFile);
        workbook.close();
    }

    private static XSSFCellStyle createStyleForTitle(XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private static void createTitleRow(XSSFWorkbook workbook, XSSFSheet sheet) {
        XSSFCellStyle style = createStyleForTitle(workbook);

        Row row = sheet.createRow(0);
        Cell cell;

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Total count");
        cell.setCellStyle(style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Insert count");
        cell.setCellStyle(style);

        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Insert time");
        cell.setCellStyle(style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Total count");
        cell.setCellStyle(style);

        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Select count");
        cell.setCellStyle(style);

        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Select time");
        cell.setCellStyle(style);
//
//        cell = row.createCell(mysqlCells[0], CellType.STRING);
//        cell.setCellValue("MySqlTotalCount");
//        cell.setCellStyle(style);
//
//        cell = row.createCell(mysqlCells[1], CellType.STRING);
//        cell.setCellValue("MySqlInsertCount");
//        cell.setCellStyle(style);
//
//        cell = row.createCell(mysqlCells[2], CellType.STRING);
//        cell.setCellValue("MySqlInsertTime");
//        cell.setCellStyle(style);
//
//        cell = row.createCell(mysqlCells[3], CellType.STRING);
//        cell.setCellValue("MySqlSelectCount");
//        cell.setCellStyle(style);
//
//        cell = row.createCell(mysqlCells[4], CellType.STRING);
//        cell.setCellValue("MySqlSelectTime");
//        cell.setCellStyle(style);
//
//        cell = row.createCell(postgresCells[0], CellType.STRING);
//        cell.setCellValue("PostgreTotalCount");
//        cell.setCellStyle(style);
//
//        cell = row.createCell(postgresCells[1], CellType.STRING);
//        cell.setCellValue("PostgreInsertCount");
//        cell.setCellStyle(style);
//
//        cell = row.createCell(postgresCells[2], CellType.STRING);
//        cell.setCellValue("PostgreInsertTime");
//        cell.setCellStyle(style);
//
//        cell = row.createCell(postgresCells[3], CellType.STRING);
//        cell.setCellValue("PostgreSelectCount");
//        cell.setCellStyle(style);
//
//        cell = row.createCell(postgresCells[4], CellType.STRING);
//        cell.setCellValue("PostgreSelectTime");
//        cell.setCellStyle(style);
    }

}