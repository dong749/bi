package com.yupi.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel相关工具类
 */
@Slf4j
public class ExcelUtils
{
    public static String excelToCSV(MultipartFile multipartFile)
    {
        // 读取数据
//        File file = null;
//        try {
//            file = ResourceUtils.getFile("classpath:网站数据.xlsx");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }

        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet().headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        // 转为CSV
        StringBuilder stringBuilder = new StringBuilder();
        // 读取表头
        if (CollUtil.isEmpty(list))
        {
            return "";
        }
        LinkedHashMap<Integer, String> tableHeader = (LinkedHashMap<Integer, String>) list.get(0);
        // 去除空字符
        List<String> headerList = tableHeader.values()
                .stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");
        // 读取除表头以外的每行数据
        for (int i = 1; i < list.size(); i++)
        {
            LinkedHashMap<Integer, String> dataInLine = (LinkedHashMap<Integer, String>) list.get(i);
            // 去除空字符
            List<String> dataLineList = dataInLine.values()
                    .stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(dataLineList, ",")).append("\n");
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args)
    {
        System.out.println(excelToCSV(null));
    }
}
