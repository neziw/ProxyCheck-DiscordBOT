/*
 * This file is part of "ProxyCheck-DiscordBOT", licensed under MIT License.
 *
 *  Copyright (c) 2024 neziw
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package ovh.neziw.bot.service;

import java.time.Duration;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.neziw.bot.config.RedisConfig;
import ovh.neziw.bot.result.ProxyResult;
import ovh.neziw.bot.util.GsonUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisCacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheService.class);
    private JedisPool jedisPool;
    private int cacheTTLSeconds;

    public RedisCacheService(final RedisConfig redisConfig) {
        try {
            final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxTotal(redisConfig.maxTotal);
            jedisPoolConfig.setMaxIdle(redisConfig.maxIdle);
            jedisPoolConfig.setMinIdle(redisConfig.minIdle);
            jedisPoolConfig.setBlockWhenExhausted(redisConfig.blockWhenExhausted);
            jedisPoolConfig.setMaxWait(Duration.ofSeconds(redisConfig.maxWaitSeconds));
            this.jedisPool = new JedisPool(jedisPoolConfig, redisConfig.connectionUrl);
            this.cacheTTLSeconds = (int) Duration.ofMinutes(redisConfig.cacheTTLMinutes).toSeconds();
        } catch (final Exception exception) {
            LOGGER.error("Error creating Redis connection pool", exception);
        }
    }

    public void addToCache(final String ipAddress, final ProxyResult proxyResult) {
        try (final Jedis jedis = this.jedisPool.getResource()) {
            LOGGER.info("Adding proxy result to cache for IP address: {}", ipAddress);
            jedis.setex(ipAddress, this.cacheTTLSeconds, GsonUtil.getGson().toJson(proxyResult));
        } catch (final Exception exception) {
            LOGGER.error("Error adding proxy result to cache", exception);
        }
    }

    public Optional<ProxyResult> getFromCache(final String ipAddress) {
        try (final Jedis jedis = this.jedisPool.getResource()) {
            LOGGER.info("Trying to get proxy result from cache for IP address: {}", ipAddress);
            final String json = jedis.get(ipAddress);
            return Optional.ofNullable(GsonUtil.getGson().fromJson(json, ProxyResult.class));
        } catch (final Exception exception) {
            LOGGER.error("Error getting proxy result from cache", exception);
            return Optional.empty();
        }
    }

    public JedisPool getJedisPool() {
        return this.jedisPool;
    }
}