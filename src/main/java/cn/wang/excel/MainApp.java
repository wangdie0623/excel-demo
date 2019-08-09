package cn.wang.excel;

import cn.wang.excel.utils.ExcelReadUtils;
import cn.wang.excel.utils.ExcelWriteUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author 王叠  2019-08-09 14:46
 */
public class MainApp {
    public static void main(String[] args) throws IOException {
        String filePath = MainApp.class.getResource("/").getPath()+"test.xls";
        String[] titles = {"姓名", "年龄", "性别"};
        ExcelWriteUtils utils = new ExcelWriteUtils(filePath,false);
        utils.setTitle(Arrays.asList(titles), "人员统计");
        utils.setRows("人员统计", new Object[]{"王五333", 2.0, "男"});
        utils.outFile();
        utils.close();
        ExcelReadUtils read = new ExcelReadUtils(filePath);
        System.out.println(read.getColValuesByIndex(0, 0, String.class));

    }
}
