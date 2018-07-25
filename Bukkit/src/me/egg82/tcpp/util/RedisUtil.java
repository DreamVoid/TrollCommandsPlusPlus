package me.egg82.tcpp.util;

import me.egg82.tcpp.Configuration;
import ninja.egg82.patterns.ServiceLocator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisUtil {
	//vars
	
	//constructor
	public RedisUtil() {
		
	}
	
	//public
	@SuppressWarnings("resource")
	public static Jedis getRedis() {
		Jedis redis = null;
		
		Configuration config = ServiceLocator.getService(Configuration.class);
		JedisPool redisPool = ServiceLocator.getService(JedisPool.class);
		if (redisPool != null) {
			redis = redisPool.getResource();
			String pass = config.getNode("messaging", "redis", "pass").getString();
			if (pass != null && !pass.isEmpty()) {
				redis.auth(pass);
			}
		}
		
		return redis;
	}
	
	//private
	
}
