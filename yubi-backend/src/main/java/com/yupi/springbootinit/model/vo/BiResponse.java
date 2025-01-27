package com.yupi.springbootinit.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BI返回结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BiResponse
{
    private Long chartId;

    private String displayCode;

    private String analysisResult;
}
