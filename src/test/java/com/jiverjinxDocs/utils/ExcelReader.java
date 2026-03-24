package com.jiverjinxDocs.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    public static class TestRow {
        public final String testId;      // String to support H1, H2 and numeric IDs
        public final String section;
        public final String linkName;
        public final String expectedUrl;

        public TestRow(String testId, String section, String linkName, String expectedUrl) {
            this.testId      = testId;
            this.section     = section;
            this.linkName    = linkName;
            this.expectedUrl = expectedUrl;
        }

        // Convenience: is this a home page button row?
        public boolean isHomePageButton() {
            return testId.startsWith("H");
        }
    }

    public static List<TestRow> readTestData() {
        String path = System.getProperty("user.dir")
                    + "/src/test/resources/TestData_2.xlsx";
        List<TestRow> rows = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(path);
             Workbook wb = new XSSFWorkbook(fis)) {

            Sheet sheet = wb.getSheetAt(0);
            String currentSection = "";

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // skip header

                Cell idCell = row.getCell(0);
                if (idCell == null) continue;

                // Section header rows — string in col A, rest empty
                if (idCell.getCellType() == CellType.STRING) {
                    String val = idCell.getStringCellValue().trim();
                    // If col C (LinkName) is empty it's a section header, not a data row
                    Cell nameCell = row.getCell(2);
                    if (nameCell == null || nameCell.getCellType() == CellType.BLANK
                            || getCellString(nameCell).isEmpty()) {
                        currentSection = val;
                        continue;
                    }
                    // Otherwise it's a string TestID data row (H1, H2...)
                    String linkName = getCellString(row.getCell(2));
                    String url      = getCellString(row.getCell(3));
                    if (!linkName.isEmpty() && !url.isEmpty()) {
                        rows.add(new TestRow(val, currentSection, linkName, url));
                    }
                    continue;
                }

                // Numeric TestID data rows
                if (idCell.getCellType() == CellType.NUMERIC) {
                    String testId  = String.valueOf((int) idCell.getNumericCellValue());
                    String name    = getCellString(row.getCell(2));
                    String url     = getCellString(row.getCell(3));
                    if (!name.isEmpty() && !url.isEmpty()) {
                        rows.add(new TestRow(testId, currentSection, name, url));
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("❌ Cannot read TestData.xlsx at: " + path, e);
        }

        return rows;
    }

    // Filter helpers for test methods
    public static List<TestRow> getHomePageButtonRows(List<TestRow> all) {
        return all.stream().filter(TestRow::isHomePageButton).toList();
    }

    public static List<TestRow> getSideNavRows(List<TestRow> all) {
        return all.stream().filter(r -> !r.isHomePageButton()).toList();
    }

    private static String getCellString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default      -> "";
        };
    }
}