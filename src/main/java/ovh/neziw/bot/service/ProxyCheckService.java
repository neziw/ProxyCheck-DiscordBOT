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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.neziw.bot.result.ProxyResult;
import ovh.neziw.bot.result.sub.AddressResult;
import ovh.neziw.bot.util.GsonUtil;

public class ProxyCheckService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyCheckService.class);
    private static final String ENDPOINT_URL = "https://proxycheck.io/v2/%s?vpn=1&asn=1&key=%s";
    private final String apiKey;
    private final RedisCacheService redisCacheService;

    public ProxyCheckService(final String apiKey, final RedisCacheService redisCacheService) {
        this.apiKey = apiKey;
        this.redisCacheService = redisCacheService;
    }

    public Optional<ProxyResult> getProxyResult(final String ipAddress) {
        try {
            LOGGER.info("Sending request to proxycheck.io for IP address: {}", ipAddress);
            final URL url = URI.create(String.format(ENDPOINT_URL, ipAddress, this.apiKey)).toURL();
            final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept", "application/json");

            try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                final StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                final JsonObject jsonObject = JsonParser.parseString(stringBuilder.toString()).getAsJsonObject();
                final String status = jsonObject.get("status").getAsString();
                if (!status.equalsIgnoreCase("ok")) {
                    LOGGER.error("Failed to check proxy status for IP address: {} (Message: {})", ipAddress, jsonObject.get("message").getAsString());
                    return Optional.empty();
                }

                final ProxyResult proxyResult = new ProxyResult(status, GsonUtil.getGson().fromJson(jsonObject.get(ipAddress), AddressResult.class));
                this.redisCacheService.addToCache(ipAddress, proxyResult);
                return Optional.of(proxyResult);
            } finally {
                httpURLConnection.disconnect();
            }
        } catch (final Exception exception) {
            LOGGER.error("Error checking proxy status", exception);
            return Optional.empty();
        }
    }
}