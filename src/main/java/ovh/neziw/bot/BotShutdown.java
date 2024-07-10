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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotShutdown extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotShutdown.class);
    private final BotBootstrap botBootstrap;

    public BotShutdown(final BotBootstrap botBootstrap) {
        this.botBootstrap = botBootstrap;
    }

    @Override
    public void run() {
        try {
            this.botBootstrap.jda.shutdown();
            this.botBootstrap.redisCacheService.getJedisPool().destroy();
        } catch (final Exception exception) {
            LOGGER.error("An error occurred while shutting down the Discord bot", exception);
        }
    }
}