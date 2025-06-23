package com.fraud.biz.service.impl;


import com.fraud.biz.cache.redis.RedisUtilService;
import com.fraud.commons.enums.FraudRestResultCode;
import com.sct.bizmsg.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LockService {

    private final String LOCK_PREFIX = "tx:lock:";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 用于记录锁的唯一标识（防止误解锁）
    private static final ThreadLocal<String> LOCK_ID_HOLDER = new ThreadLocal<>();

    private final RedisUtilService redisUtilService;

    public LockService(RedisUtilService redisUtilService) {
        this.redisUtilService = redisUtilService;
    }

    /**
     * 获取锁：key 不存在时设置成功，设置过期时间，防止死锁
     */
    public boolean tryLock(String key) {
        try {
            String lockKey = getLockKey(key);
            String uuid = UUID.randomUUID().toString();
            Boolean success = redisUtilService.setIfAbsentAndTime(lockKey, uuid, 30, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(success)) {
                LOCK_ID_HOLDER.set(uuid);
                return true;
            }
        }catch (Exception exception){
            logger.info("执行上锁失败, error={}",exception.getMessage());
            throw new BusinessException(FraudRestResultCode.ERROR,"锁定交易失败");
        }
        return false;
    }

    /**
     * 释放锁：仅释放自己加的锁，防止误删
     */
    public void unlock(String key) {
        try {
            String lockKey = getLockKey(key);
            String lockId = (String)redisUtilService.get(lockKey);
            if (lockId != null && lockId.equals(LOCK_ID_HOLDER.get())) {
                redisUtilService.deleteValue(lockKey);
            }
            LOCK_ID_HOLDER.remove();
        }catch (Exception exception){
            logger.info("执行解锁失败, error={}",exception.getMessage());
            throw new BusinessException(FraudRestResultCode.ERROR,"解锁交易失败");
        }
    }

    public String getLockKey(String key) {
        return LOCK_PREFIX + key;
    }

}
