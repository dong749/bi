package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供RedisLimiter基础服务的工具类(提供了一个通用的能力)
 */
@Service
public class RedissonLimiterManager
{
    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     * @param key 区分不同的限流器，比如不同的用户ID应该分别统计
     */
    public void doRateLimit(String key)
    {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当有一个请求就需要取出一个令牌
        boolean isOperation = rateLimiter.tryAcquire(1);
        if (!isOperation)
        {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST, "请求过于频繁");
        }
    }
}
