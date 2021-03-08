package com.merlincrm.utils;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Sort;

public class CommentUtil {
	
	public static int nextWeight(Integer maxWeight) {
		if (maxWeight == null) {
			return 10;
		}

		int mod = maxWeight % 10;
		if (mod == 0) {
			return maxWeight + 10;
		} else {
			return maxWeight + 20 - mod;
		}
	}

	public static String generateSQL(Sort sort) {
		if (sort == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		Iterator<Sort.Order> iterator = sort.iterator();
		while (iterator.hasNext()) {
			Sort.Order order = iterator.next();
			if (sb.length() < 1) {
				sb.append(" ORDER BY ");
			} else {
				sb.append(", ");
			}
			sb.append(order.getProperty());
			if (order.isDescending()) {
				sb.append(" DESC");
			}
		}
		return sb.toString();
	}
	
	public static CellStyle getRequtredCellStyle(Workbook workbook ) {
		if (workbook == null) {
			return null;
		}
		
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
		return cellStyle;
	}
	
	public static boolean isNumeric(Cell cell) {
		if (cell == null) {
			return false;
		}
		
		return cell.getCellType() == Cell.CELL_TYPE_NUMERIC
				|| (cell.getCellType() == Cell.CELL_TYPE_FORMULA && cell
				.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC);
	}
	
	public static String getStringCellValue(Cell cell) {
		if (cell == null) {
			return null;
		}

		if (cell.getCellType() == Cell.CELL_TYPE_STRING
				|| (cell.getCellType() == Cell.CELL_TYPE_FORMULA && cell
						.getCachedFormulaResultType() == Cell.CELL_TYPE_STRING)) {
			if (cell.getStringCellValue() == null) {
				return null;
			} else {
				return cell.getStringCellValue().trim();
			}
		} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC
				|| (cell.getCellType() == Cell.CELL_TYPE_FORMULA && cell
						.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC)) {
			double value1 = cell.getNumericCellValue();
			long value2 = (long) value1;
			if ((value1 - value2) == 0.0d) {
				return String.valueOf(value2);
			} else {
				return String.valueOf(value1);
			}
		} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN
				|| (cell.getCellType() == Cell.CELL_TYPE_FORMULA && cell
						.getCachedFormulaResultType() == Cell.CELL_TYPE_BOOLEAN)) {
			return String.valueOf(cell.getBooleanCellValue());
		} else {
			return null;
		}
	}

}
