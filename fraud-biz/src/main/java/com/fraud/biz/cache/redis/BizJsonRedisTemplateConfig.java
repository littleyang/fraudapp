package com.fraud.biz.cache.redis;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.google.common.collect.Lists;
import com.sct.bizmsg.codes.CommonResultCode;
import com.sct.bizmsg.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */

@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class BizJsonRedisTemplateConfig {


    private String host;

    private int port;

    private String password;

    private int db;

    private int timeout;

    private Integer maxTotal=1000;

    private Integer maxIdle=300;

    private Integer maxWaitMillis=3000;

    private Boolean testOnBorrow=true;

    private boolean sentinelMode=false;

    private String sentinelNodes;


    @Bean(name = "bizJedisPoolConfig")
    JedisPoolConfig bizJedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxWaitMillis(this.getMaxWaitMillis());
        jedisPoolConfig.setMaxIdle(this.getMaxIdle());
        jedisPoolConfig.setMaxTotal(this.getMaxTotal());
        jedisPoolConfig.setTestOnBorrow(this.getTestOnBorrow());
        return jedisPoolConfig;
    }

    @Bean(name="bizJedisConnectionFactory")
    JedisConnectionFactory bizJedisConnectionFactory(@Qualifier("bizJedisPoolConfig") JedisPoolConfig jedisPoolConfig){

        if(ObjectUtil.isNotNull(this.getSentinelNodes()) ||this.isSentinelMode()){
            // 代表着哨兵模式
            if(StringUtils.isBlank(this.getSentinelNodes())){
                throw new BusinessException(CommonResultCode.ERROR,"redis sentinel nodes is null");
            }
            // 接卸哨兵模式配置
            String[] nodes = this.getSentinelNodes().split(",");
            List<String> nodeList = Lists.newArrayList(nodes);
            Set<String> redisNodes = new HashSet<>(nodeList);
            RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration("mymaster",redisNodes);
            redisSentinelConfiguration.setPassword(this.getPassword());
            redisSentinelConfiguration.setSentinelPassword(this.getPassword());
            redisSentinelConfiguration.setMaster("mymaster");
            redisSentinelConfiguration.setDatabase(this.getDb());
            JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisSentinelConfiguration);
            jedisConnectionFactory.setDatabase(this.getDb());
            jedisConnectionFactory.setUsePool(true);
            jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
            return jedisConnectionFactory;
        }else{
            // 普通模式
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName(this.getHost());
            redisStandaloneConfiguration.setPort(this.getPort());
            redisStandaloneConfiguration.setDatabase(this.getDb());
            redisStandaloneConfiguration.setPassword(RedisPassword.of(this.getPassword()));
            JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jpcb = (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
            jpcb.poolConfig(jedisPoolConfig);
            JedisClientConfiguration jedisClientConfiguration = jpcb.build();
            JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
            jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
            jedisConnectionFactory.setUsePool(true);
            return jedisConnectionFactory;

        }
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("bizJedisConnectionFactory") JedisConnectionFactory bizJedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        //序列化
        Jackson2JsonRedisSerializer fastJsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        // value值的序列化采用fastJsonRedisSerializer
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setHashValueSerializer(fastJsonRedisSerializer);
        template.setKeySerializer(jdkSerializationRedisSerializer);
        template.setHashKeySerializer(jdkSerializationRedisSerializer);
        template.setConnectionFactory(bizJedisConnectionFactory);
        return template;
    }


    @Bean(name = "bizRedisTemplate")
    public RedisTemplate<String, Object> getRedisTemplate(@Qualifier("bizJedisConnectionFactory") JedisConnectionFactory bizJedisConnectionFactory) {

        RedisTemplate<String, Object> bizRedisTemplate = new RedisTemplate<String, Object>();
        bizRedisTemplate.setConnectionFactory(bizJedisConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        bizRedisTemplate.setKeySerializer(stringRedisSerializer); // key的序列化类型
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance ,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        bizRedisTemplate.setValueSerializer(jackson2JsonRedisSerializer); // value的序列化类型
        bizRedisTemplate.setHashKeySerializer(stringRedisSerializer);
        bizRedisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        bizRedisTemplate.afterPropertiesSet();

        return bizRedisTemplate;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDb() {
        return db;
    }

    public void setDb(int db) {
        this.db = db;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isSentinelMode() {
        return sentinelMode;
    }

    public void setSentinelMode(boolean sentinelMode) {
        this.sentinelMode = sentinelMode;
    }

    public String getSentinelNodes() {
        return sentinelNodes;
    }

    public void setSentinelNodes(String sentinelNodes) {
        this.sentinelNodes = sentinelNodes;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(Integer maxIdle) {
        this.maxIdle = maxIdle;
    }

    public Integer getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(Integer maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }
}
