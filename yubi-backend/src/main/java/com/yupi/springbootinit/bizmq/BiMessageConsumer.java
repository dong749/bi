package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.manager.AIManager;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.service.ChartService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class BiMessageConsumer
{
    @Resource
    private ChartService chartService;

    @Resource
    private AIManager aiManager;


    @SneakyThrows
    @RabbitListener(queues = BiMQConstant.BI_QUEUE_NAME, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag)
    {
        if (StringUtils.isBlank(message))
        {
            // 任务处理失败, 消息拒绝
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
        }
        Long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null)
        {
            // 任务处理失败, 消息拒绝
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图表为空");
        }
        Chart updatedChart = new Chart();
        updatedChart.setId(chart.getId());
        updatedChart.setStatus("running");
        boolean b = chartService.updateById(updatedChart);
        if (!b)
        {
            // 任务处理失败, 消息拒绝
            channel.basicNack(deliveryTag, false, false);
            handleUpdateError(chart.getId(), "更新图表状态失败");
            return;
        }
        String s = aiManager.sendMessageToXingHuoAI(true, builderUserInput(chart));
        String[] answerOfAI = s.split("```");
        System.out.println(answerOfAI.length);
        if (answerOfAI.length < 3)
        {
            // 任务处理失败, 消息拒绝
            channel.basicNack(deliveryTag, false, false);
            handleUpdateError(chart.getId(), "更新图表状态失败");
            return;
        }
        String displayCode = answerOfAI[1].replace("json", "");
        String analysisResult = answerOfAI[2];
        Chart finalChart = new Chart();
        finalChart.setId(chart.getId());
        finalChart.setStatus("succeed");
        finalChart.setGenChart(displayCode);
        finalChart.setGenResult(analysisResult);
        boolean isSucceed = chartService.updateById(finalChart);
        if (!isSucceed)
        {
            // 任务处理失败, 消息拒绝
            channel.basicNack(deliveryTag, false, false);
            handleUpdateError(chart.getId(), "更新图表状态失败");
        }
        // 任务处理成功, 手动确认消息
        channel.basicAck(deliveryTag, false);
    }

    private void handleUpdateError(Long chartId, String execMessage)
    {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage(execMessage);
        boolean isSucceed = chartService.updateById(updateChartResult);
        if (!isSucceed)
        {
            log.error("图表更新失败" + chartId + ", " + execMessage);
        }
    }

    /**
     * 构建用户输入
     * @param chart
     * @return
     */
    private String builderUserInput(Chart chart)
    {
        String goal = chart.getGoal();
        String charType = chart.getCharType();
        String result = chart.getChartData();
        StringBuilder userInput = new StringBuilder();
        // AI系统预设
        userInput.append("分析需求: ").append("\n");
        String userGoal = goal;
        if (StringUtils.isNotBlank(charType))
        {
            userGoal += ", 请使用" + charType;
        }
        userInput.append(userGoal).append("\n");
        // 压缩数据

        userInput.append("原始数据: ").append("\n");
        userInput.append(result).append("\n");
        return userInput.toString();
    }
}
