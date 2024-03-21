package com.ieagle.auto.site.util;

import com.ieagle.auto.site.exceptions.MasterTestDataFileException;

public class DataDrivenManager {
	private final static int TEST_NAME_COLUMN = 1;
	private final static int TEST_DATA_STRAT_COLUMN = 2;

	private ExcelManager excelManager;

	public DataDrivenManager(String filePath) {
		try {
			excelManager = new ExcelManager(filePath);
		} catch (Exception e) {
			MasterTestDataFileException ex = new MasterTestDataFileException(
					"Unable to find or read MasterTestDataFile due to " + e.getClass().getSimpleName() + " "
							+ e.getMessage(),
					e);
			throw ex;
		}
	}

	public Object[][] getTestCaseDataSets(String sheetName, String testName) {
		try {
			int testRowNumber = excelManager.getRowNumber(sheetName, TEST_NAME_COLUMN, testName);
			int testDataStartRow = testRowNumber + 1;

			int testDataRows = 0;
			for (int i = testDataStartRow; excelManager.getCellData(sheetName, TEST_NAME_COLUMN, i)
					.equalsIgnoreCase(testName); i++) {
				testDataRows++;
			}

			int testDataCols = excelManager.getCellCount(sheetName, testRowNumber) - TEST_DATA_STRAT_COLUMN + 1;

			Object[][] testCaseDataSets = new Object[testDataRows][testDataCols];

			for (int i = 0; i < testDataRows; i++) {
				for (int j = 0; j < testDataCols; j++) {
					testCaseDataSets[i][j] = excelManager.getCellData(sheetName, TEST_DATA_STRAT_COLUMN + j,
							testDataStartRow + i);
				}
			}
			return testCaseDataSets;
		} catch (Exception e) {
			MasterTestDataFileException ex = new MasterTestDataFileException("Unable get test data for " + testName
					+ " from " + sheetName + " sheet from MasterTestDataFile due to "
					+ e.getClass().getSimpleName() + " " + e.getMessage(), e);
			throw ex;
		}
	}
}
