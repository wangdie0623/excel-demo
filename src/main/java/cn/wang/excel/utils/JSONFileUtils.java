package cn.wang.excel.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;

/**
 * @author 王叠  2019-08-08 10:17
 * 公司信息存储json文件
 */
@Slf4j
public class JSONFileUtils {
    private static final String JSON_FILE_PATH = "D:\\xx\\companyJSON.json";

    /**
     * 存储信息到json文件
     *
     * @param infos 待存储信息对象
     */
    public static void writeFile(Object infos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(JSON_FILE_PATH))) {
            if (infos == null) {
                return;
            }
            String line = JSON.toJSONString(infos);
            writer.write(line);
        } catch (IOException e) {
            log.error("存储信息到JSON文件异常", e);
        }
    }

    /**
     * 从指定文件读取信息对象
     *
     * @return 信息对象
     */
    public static <T> T readFile2Obj(Class<T> clazz) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(JSON_FILE_PATH)))) {
            StringBuilder builder = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                builder.append(line);
            }
            return JSON.parseObject(builder.toString(), clazz);
        } catch (Exception e) {
            log.error("从JSON文件读取信息对象异常",e);
        }
        return null;
    }

    public static <T> List<T> readFile2List(Class<T> clazz) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(JSON_FILE_PATH)))) {
            StringBuilder builder = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                builder.append(line);
            }
            return JSON.parseArray(builder.toString(), clazz);
        } catch (Exception e) {
            log.error("从JSON文件读取信息对象集合异常",e);
        }
        return null;
    }
}
