package com.yupi.springbootinit.controller;



import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * 线程池队列测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev", "local"})
public class QueueController
{
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/add")
    public void add(String name)
    {
        CompletableFuture.runAsync(() -> {
            log.info("任务执行中" + name + ", 执行线程: " + Thread.currentThread().getName());
            try{
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, threadPoolExecutor);
    }

    @GetMapping("/get")
    public String get()
    {
        Map<String, Object> map = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        long taskCount = threadPoolExecutor.getTaskCount();
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("队列长度", size);
        map.put("任务总数", taskCount);
        map.put("已经完成的任务总数", completedTaskCount);
        map.put("线程池中当前活跃的线程数", activeCount);
        return JSONUtil.toJsonStr(map);
    }
}
