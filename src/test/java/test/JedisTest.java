package test;

import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

public class JedisTest {

    @Test
    public void testJedis(){
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("s002611");
        jedis.del("hash1");
        jedis.hset("hash1", "a1", "value1");
        jedis.hset("hash1", "a2", "value2");
        jedis.hset("hash1", "a3", "value3");
        jedis.hset("hash1", "a4", "value4");

        Map<String, String> res = jedis.hgetAll("hash1");
        System.out.println(res);

    }
}
