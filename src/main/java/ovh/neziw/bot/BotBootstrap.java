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
package ovh.neziw.bot;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import java.io.File;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ovh.neziw.bot.config.BotConfig;
import ovh.neziw.bot.config.RedisConfig;
import ovh.neziw.bot.listener.CheckCommandListener;
import ovh.neziw.bot.service.ProxyCheckService;
import ovh.neziw.bot.service.RedisCacheService;

public class BotBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotBootstrap.class);
    protected JDA jda;
    protected RedisCacheService redisCacheService;

    private BotBootstrap() {
        try {
            final BotConfig botConfig = ConfigManager.create(BotConfig.class, (config) -> {
                config.withConfigurer(new YamlSnakeYamlConfigurer());
                config.withBindFile(new File("bot.yml"));
                config.withRemoveOrphans(true);
                config.saveDefaults();
                config.load(true);
            });
            final RedisConfig redisConfig = ConfigManager.create(RedisConfig.class, (config) -> {
                config.withConfigurer(new YamlSnakeYamlConfigurer());
                config.withBindFile(new File("redis.yml"));
                config.withRemoveOrphans(true);
                config.saveDefaults();
                config.load(true);
            });

            final JDABuilder jdaBuilder = JDABuilder.createDefault(botConfig.botToken);
            jdaBuilder.setActivity(Activity.competing("proxycheck.io API"));
            jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);
            this.jda = jdaBuilder.build();
            this.jda.awaitReady();

            this.redisCacheService = new RedisCacheService(redisConfig);
            final ProxyCheckService proxyCheckService = new ProxyCheckService(botConfig.proxyCheckApiKey, this.redisCacheService);

            this.jda.addEventListener(new CheckCommandListener(this.redisCacheService, proxyCheckService));
            for (final Guild guild : this.jda.getGuilds()) {
                guild.upsertCommand(Commands.slash("check-address", "Check proxy information for an IP address")
                        .addOption(OptionType.STRING, "address", "IP address to check", true)).queue();
                LOGGER.info("Connected to Discord server: {} (ID: {})", guild.getName(), guild.getId());
            }
        } catch (final Exception exception) {
            LOGGER.error("Failed to initialize Discord bot", exception);
        }
    }

    public static void main(final String[] args) {
        final BotBootstrap bootstrap = new BotBootstrap();

        Runtime.getRuntime().addShutdownHook(new BotShutdown(bootstrap));
    }
}