package org.dargor.product.app.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dargor.product.core.entity.Product;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@AllArgsConstructor
public class RedisUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final RedisTemplate<String, byte[]> redisTemplate;

    public void storeValues(final String key, final String hashKey, List<Product> value, int expire) throws JsonProcessingException {
        redisTemplate.opsForHash().put(key, hashKey, OBJECT_MAPPER.writeValueAsBytes(value));
        redisTemplate.expire(key, expire, TimeUnit.MINUTES);
    }

    public List<Product> getValues(final String key, String hashKey) throws ClassNotFoundException, JsonProcessingException {
        var values = Optional.ofNullable(redisTemplate.opsForHash().get(key, hashKey));
        if (values.isPresent())
            log.info(OBJECT_MAPPER.writeValueAsString(values.get()));
        return values.isPresent() ? OBJECT_MAPPER.convertValue(OBJECT_MAPPER.writeValueAsString(values.get()), new TypeReference<>() {
        }) : Collections.emptyList();

    }

}
