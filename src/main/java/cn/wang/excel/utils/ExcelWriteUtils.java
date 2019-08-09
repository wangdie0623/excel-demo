package cn.wang.excel.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author 王叠  2019-08-07 16:35
 */
@Slf4j
public class ExcelWriteUtils {


    private Workbook workbook;
    private String outFileRealPath;

    public ExcelWriteUtils(String fileRealPath,boolean xssf) throws IOException {
        this.outFileRealPath = fileRealPath;
        File file = new File(fileRealPath);
        if (file.exists()) {
            file.delete();
        }
        workbook = WorkbookFactory.create(xssf);
    }

    /**
     * 通过标识获取sheet
     *
     * @param sheetName sheet标识
     * @return sheet对象
     */
    private Sheet getSheet(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        }
        return sheet;
    }

    /**
     * 对指定sheet第一行设置title
     *
     * @param titles    抬头集合
     * @param sheetName 指定sheet名称
     */
    public void setTitle(List<String> titles, String sheetName) {
        writeRow(getSheet(sheetName), 0, titles.toArray());
    }

    /**
     * 对指定 sheet 第二行开始设置每行值
     *
     * @param rows      行值集合
     * @param sheetName 指定sheet
     */
    public void setRows(String sheetName, Object[]... rows) {
        if (rows == null) {
            return;
        }
        Sheet sheet = getSheet(sheetName);
        for (int i = 0; i < rows.length; i++) {
            writeRow(sheet, i + 1, rows[i]);
        }
    }

    /**
     * 对指定 sheet 第二行开始设置每行值
     *
     * @param rows      行值集合
     * @param sheetName 指定sheet
     */
    public void setRows(String sheetName, List<Object[]> rows) {
        if (rows == null) {
            return;
        }
        Sheet sheet = getSheet(sheetName);
        for (int i = 0; i < rows.size(); i++) {
            writeRow(sheet, i + 1, rows.get(i));
        }
    }

    public void outFile() {
        try (FileOutputStream out = new FileOutputStream(outFileRealPath)) {
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            log.error("输出excel文件异常:{}", outFileRealPath, e);
        }
    }


    /**
     * 对指定行指定列执行写操作
     *
     * @param row      指定行对象
     * @param colIndex 指定列下标
     * @param colVal   列值
     */
    private static void writeCol(Row row, int colIndex, Object colVal) {
        if (colVal == null) {
            return;
        }
        if (row == null) {
            throw new RuntimeException("row not is null");
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }

        switch (colVal.getClass().getName()) {
            case "java.lang.String":
                cell.setCellValue(colVal.toString());
                break;
            case "java.lang.Boolean":
                cell.setCellValue(Boolean.parseBoolean(colVal.toString()));
                break;
            case "java.lang.Double":
                cell.setCellValue(Double.parseDouble(colVal.toString()));
                break;
            default:
                throw new RuntimeException("col write not support type");
        }
    }

    /**
     * 对指定行执行整行写操作
     *
     * @param sheet    指定sheet
     * @param rowIndex 指定行下标
     * @param values   整行值数组
     */
    private static void writeRow(Sheet sheet, Integer rowIndex, Object[] values) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        for (int i = 0; i < values.length; i++) {
            writeCol(row, i, values[i]);
        }
    }

    /**
     * 释放io资源
     */
    public void close() {
        try {
            workbook.close();
        } catch (IOException e) {
            log.error("excel写工具类释放io异常", e);
        }
    }

}
