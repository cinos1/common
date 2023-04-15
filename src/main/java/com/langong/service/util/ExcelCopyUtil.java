package com.langong.service.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.Iterator;

public class ExcelCopyUtil {


    /**
     * 行复制功能
     *
     * @param fromRow 从哪行开始
     * @param toRow   目标行
     */
    public static void copyRow(Row fromRow, Row toRow) {
        toRow.setHeight(fromRow.getHeight());

        for (Iterator<Cell> cellIt = fromRow.cellIterator(); cellIt.hasNext(); ) {
            Cell tmpCell = cellIt.next();
            Cell newCell = toRow.createCell(tmpCell.getColumnIndex());
            copyCell(tmpCell, newCell);
        }

        Sheet worksheet = fromRow.getSheet();

        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == fromRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(toRow.getRowNum(),
                        (toRow.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
                        cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
                worksheet.addMergedRegionUnsafe(newCellRangeAddress);
            }
        }
    }

    /**
     * 复制单元格
     */
    public static void copyCell(Cell srcCell, Cell distCell) {
        CellStyle srcStyle = srcCell.getCellStyle();
        // 样式
        distCell.setCellStyle(srcStyle);
        //值
        if (srcCell.getCellType() == CellType.STRING) {
            distCell.setCellValue(srcCell.getStringCellValue());
        }

    }
}
