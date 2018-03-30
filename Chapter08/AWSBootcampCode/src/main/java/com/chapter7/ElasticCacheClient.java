package com.chapter7;

import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

public class ElasticCacheClient {
    private MemcachedClient memcachedClient;

    public ElasticCacheClient(String url, int port) throws IOException {
        this.memcachedClient = new MemcachedClient(new InetSocketAddress(url, port));
    }

    public Object get(String key) {
        return memcachedClient.get(key);
    }

    public boolean add(String key, int expiry, Object value) throws ExecutionException, InterruptedException {
        return memcachedClient.add(key, expiry, value).get();
    }

    public boolean replace(String key, int expiry, Object value) throws ExecutionException, InterruptedException {
        return memcachedClient.replace(key, expiry, value).get();
    }

    public boolean delete(String key) throws ExecutionException, InterruptedException {
        return memcachedClient.delete(key).get();
    }
}
