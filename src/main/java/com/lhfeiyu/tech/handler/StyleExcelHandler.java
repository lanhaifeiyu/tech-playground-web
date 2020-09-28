package com.lhfeiyu.tech.handler;

import com.alibaba.excel.event.WriteHandler;
import org.apache.poi.ss.usermodel.*;

public class StyleExcelHandler implements WriteHandler {

    @Override
    public void sheet(int i, Sheet sheet) {

    }

    @Override
    public void row(int i, Row row) {

    }

    @Override
    public void cell(int i, Cell cell) {
        if (cell.getRowIndex() == 0) {
            Workbook workbook = cell.getSheet().getWorkbook();
            CellStyle cellStyle = this.headStyle(workbook);
            cell.setCellStyle(cellStyle);
        }
    }

    private CellStyle headStyle (Workbook workbook) {

        CellStyle cellStyle = workbook.createCellStyle();
        // 下边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        // 左边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        // 上边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        // 右边框
        cellStyle.setBorderRight(BorderStyle.THIN);
        // 水平对齐方式
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 垂直对齐方式
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFillForegroundColor((short) 13);


        Font headFont = workbook.createFont();
        headFont.setFontHeightInPoints((short) 12);// 设置字体大小
        cellStyle.setFont(headFont);

        return cellStyle;
    }
}
