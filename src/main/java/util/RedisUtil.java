package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import me.empty.redis.CityCountry;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class RedisUtil {
    private static RedisClient redisClient;

    public static RedisClient getClient() {
        return initOrGetRedisClient();
    }

    public static void shutdown() {

        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }

    private static RedisClient initOrGetRedisClient() {

        if (isNull(redisClient)) {
            redisClient = RedisClient.create(RedisURI.create("localhost", 6379));

            try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
                System.out.println("\nConnected to Redis\n");
            }
        }

        return redisClient;
    }

    public static void pushToRedis(List<CityCountry> data) {

        ObjectMapper mapper = new ObjectMapper();

        try (StatefulRedisConnection<String, String> connection = RedisUtil.getClient().connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), mapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
