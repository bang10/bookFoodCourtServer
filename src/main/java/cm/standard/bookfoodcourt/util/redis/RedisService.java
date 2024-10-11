package cm.standard.bookfoodcourt.util.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveData(String key, Object value, Long timeOut, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeOut, timeUnit);
    }

    public void saveDateSecond(String key, Object value, Long timeOut) {
        redisTemplate.opsForValue().set(key, value, timeOut, TimeUnit.SECONDS);
    }

    public <T> T getData(String key, Class<T> clazz) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
