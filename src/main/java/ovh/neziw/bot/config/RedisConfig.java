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
package ovh.neziw.bot.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;

@Header("Redis Connection Configuration")
public class RedisConfig extends OkaeriConfig {

    @Comment("URL connection string to connect with Redis")
    public String connectionUrl;
    @Comment("Pool configuration")
    public int maxTotal;
    public int maxIdle;
    public int minIdle;
    public boolean blockWhenExhausted;
    public long maxWaitSeconds;
    public int cacheTTLMinutes;

    public RedisConfig() {
        this.connectionUrl = "redis://localhost:6379";
        this.maxTotal = 128;
        this.maxIdle = 64;
        this.minIdle = 16;
        this.blockWhenExhausted = true;
        this.maxWaitSeconds = 15;
        this.cacheTTLMinutes = 60 * 4; // 4 hours
    }
}