package com.yupi.springbootinit.manager;

import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AIManager
{
    @Resource
    private SparkClient sparkClient;

    /**
     * 像AI发送消息
     *
     * @return
     */
    public String sendMessageToXingHuoAI(boolean isNeedTemplate, String content)
    {
        List<SparkMessage> messages = new ArrayList<>();
        if (isNeedTemplate)
        {
            String preDefinedInformation = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                    "分析需求：\n" +
                    "{数据分析的需求或者目标}\n" +
                    "原始数据：\n" +
                    "{csv格式的原始数据，用,作为分隔符}\n" +
                    "请根据这两部分内容，严格按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）同时不要使用这个符号 '】'\n" +
                    "'【【【【【'\n" +
                    "{前端 Echarts V5 的 option 配置对象 JSON 代码, 不要生成任何多余的内容，比如注释和代码块标记}\n" +
                    "'【【【【【'\n" +
                    "{明确的数据分析结论、越详细越好，不要生成多余的注释} \n"
                    + "下面是一个具体的例子的模板："
                    + "'【【【【【'\n"
                    + "JSON格式代码"
                    + "'【【【【【'\n" +
                    "结论：";
            messages.add(SparkMessage.systemContent(preDefinedInformation));
        }
        messages.add(SparkMessage.userContent(content));
        SparkRequest sparkRequest = SparkRequest.builder()
                .messages(messages)
                .maxTokens(2048)
                .temperature(0.2)
                .apiVersion(SparkApiVersion.V4_0)
                .build();
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        String responseContent = chatResponse.getContent();
        log.info("星火 AI 返回的结果 {}", responseContent);
        return responseContent;
    }
}
