package cn.wang.excel.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @author 王叠  2019-08-07 14:05
 */
@Slf4j
public class ExcelReadUtils {
    private Workbook workbook;


    public ExcelReadUtils(String fileRealPath) throws IOException {
        File file = new File(fileRealPath);
        if (!file.exists()) {
            throw new RuntimeException("文件:" + fileRealPath + ",不存在");
        }
        workbook = WorkbookFactory.create(file);
    }

    /**
     * 获取指定sheet指定col值集合
     *
     * @param sheetIndex 指定sheet下标
     * @param colIndex   指定col下标
     * @param valType    值类型
     * @return col值集合
     */
    public <T> List<T> getColValuesByIndex(Integer sheetIndex, Integer colIndex, Class<T> valType) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        return getColValuesByIndexCore(sheet, colIndex, valType);
    }

    /**
     * 获取指定sheet指定col值集合
     *
     * @param sheetName 指定sheet名称
     * @param colIndex  指定col下标
     * @param valType   值类型
     * @return col值集合
     */
    public <T> List<T> getColValuesByIndex(String sheetName, Integer colIndex, Class<T> valType) {
        Sheet sheet = workbook.getSheet(sheetName);
        return getColValuesByIndexCore(sheet, colIndex, valType);
    }

    /**
     * 获取指定sheet指定col值集合
     *
     * @param sheet    指定sheet对象
     * @param colIndex 指定col下标
     * @param valType  值类型
     * @return col值集合
     */
    private <T> List<T> getColValuesByIndexCore(Sheet sheet, Integer colIndex, Class<T> valType) {
        if (sheet == null || colIndex < 0) {
            return Collections.EMPTY_LIST;
        }
        if (Character.class.equals(valType)) {
            throw new RuntimeException("不支持 Character");
        }
        if (!isLangPackage(valType) || !isSerializable(valType)) {
            throw new RuntimeException("只支持封装类，跟String");
        }
        Iterator<Row> iterator = sheet.rowIterator();
        List<T> result = new ArrayList<>();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            Cell cell = row.getCell(colIndex);
            String val = cell.getStringCellValue();
            try {
                Constructor<T> constructor = valType.getConstructor(String.class);
                T t = constructor.newInstance(val);
                result.add(t);
            } catch (Exception e) {
                log.error("执行String 入参构造异常:{},{}", val, valType.getName());
            }
        }
        return result;
    }

    /**
     * 判断是否在jdk自带lang包下
     *
     * @param clazz class对象
     * @return true-在  false-不在
     */
    public static boolean isLangPackage(Class clazz) {
        if (clazz == null) {
            return false;
        }
        return clazz.getPackage().getName().equalsIgnoreCase("java.lang");
    }

    /**
     * 判断是否实现了序列化接口
     *
     * @param clazz class对象
     * @return true-实现 false-未实现
     */
    public static boolean isSerializable(Class clazz) {
        Class[] interfaces = clazz.getInterfaces();
        if (interfaces == null || interfaces.length == 0) {
            return false;
        }
        List<Class> list = Arrays.asList(interfaces);
        return list.contains(Serializable.class);
    }

    /**
     * 释放io资源
     */
    public void close() {
        try {
            workbook.close();
        } catch (IOException e) {
            log.error("excel读工具类释放io异常", e);
        }
    }


}
