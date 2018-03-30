package com.chapter7;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class RedisClient {
    private Jedis jedis;

    public RedisClient(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    public String get(String key) {
        return jedis.get(key);
    }

    public void add(String key, String value) {
        String statusCode = jedis.set(key, value);
        System.out.println(statusCode);
    }

    public void add(String key, List<String> value) {
        for (int i = 0; i < value.size(); i++) {
            String statusCode = jedis.lset(key, i, value.get(i));
            System.out.println(statusCode);
        }
    }

    public Long delete(String key) {
        return jedis.del(key);
    }
}
