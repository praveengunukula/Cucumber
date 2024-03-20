package com.test.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {
	 public static List<Map<String, String>> readExcel(String filePath, String sheetName) throws IOException {
	        List<Map<String, String>> data = new ArrayList<>();

	        FileInputStream file = new FileInputStream(filePath);
	        Workbook workbook = new XSSFWorkbook(file);
	        Sheet sheet = workbook.getSheet(sheetName);

	        Row headerRow = sheet.getRow(0);
	        int numColumns = headerRow.getPhysicalNumberOfCells();
	        List<String> headers = new ArrayList<>();
	        for (int i = 0; i < numColumns; i++) {
	            headers.add(headerRow.getCell(i).getStringCellValue());
	        }

	        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	            Row row = sheet.getRow(i);
	            Map<String, String> rowData = new HashMap<>();
	            for (int j = 0; j < numColumns; j++) {
	                Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
	                switch (cell.getCellType()) {
	                    case STRING:
	                        rowData.put(headers.get(j), cell.getStringCellValue());
	                        break;
	                    case NUMERIC:
	                        rowData.put(headers.get(j), String.valueOf(cell.getNumericCellValue()));
	                        break;
	                    case BOOLEAN:
	                        rowData.put(headers.get(j), String.valueOf(cell.getBooleanCellValue()));
	                        break;
	                    default:
	                        rowData.put(headers.get(j), "");
	                }
	            }
	            data.add(rowData);
	        }

	        workbook.close();
	        return data;
	    }

	    public static Map<String, String> findRowByValue(List<Map<String, String>> data, String columnName, String value) {
	        for (Map<String, String> row : data) {
	            if (row.containsKey(columnName) && row.get(columnName).equals(value)) {
	                return row;
	            }
	        }
	        return null; // Row not found
	    }
}
