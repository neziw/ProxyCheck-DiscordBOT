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
package ovh.neziw.bot.listener;

import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ovh.neziw.bot.result.sub.AddressResult;
import ovh.neziw.bot.result.sub.CurrencyResult;
import ovh.neziw.bot.service.ProxyCheckService;
import ovh.neziw.bot.service.RedisCacheService;

public class CheckCommandListener extends ListenerAdapter {

    private final RedisCacheService redisCacheService;
    private final ProxyCheckService proxyCheckService;

    public CheckCommandListener(final RedisCacheService redisCacheService, final ProxyCheckService proxyCheckService) {
        this.redisCacheService = redisCacheService;
        this.proxyCheckService = proxyCheckService;
    }

    @Override
    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {
        if (!event.getName().equals("check-address")) return;
        final String address = Objects.requireNonNull(event.getOption("address")).getAsString();
        event.reply("Sending `GET` request to **proxycheck.io** API...").queue();

        this.redisCacheService.getFromCache(address)
                .or(() -> this.proxyCheckService.getProxyResult(address))
                .ifPresentOrElse(proxyResult -> {
                    final AddressResult addressResult = proxyResult.addressResult();
                    final CurrencyResult currencyResult = addressResult.currencyResult();
                    final String responseString = "Proxy Information for: **" + address + "**\n" + "```" +
                            "\nAddress: " + address +
                            "\nASN: " + addressResult.asn() +
                            "\nRange: " + addressResult.range() +
                            "\nHostname: " + addressResult.hostname() +
                            "\nProvider: " + addressResult.provider() +
                            "\nOrganization: " + addressResult.organisation() +
                            "\nContinent: " + addressResult.continent() +
                            "\nContinent Code: " + addressResult.continentCode() +
                            "\nCountry: " + addressResult.country() +
                            "\nISO Code: " + addressResult.isoCode() +
                            "\nRegion: " + addressResult.region() +
                            "\nRegion Code: " + addressResult.regionCode() +
                            "\nTime Zone: " + addressResult.timeZone() +
                            "\nCity: " + addressResult.city() +
                            "\nLatitude: " + addressResult.latitude() +
                            "\nLongitude: " + addressResult.longitude() +
                            "\nCurrency Code: " + currencyResult.code() +
                            "\nCurrency Name: " + currencyResult.name() +
                            "\nCurrency Symbol: " + currencyResult.symbol() +
                            "\nIs Proxy: " + addressResult.proxy() +
                            "\nType: " + addressResult.type() +
                            "```";
                    event.getChannel().sendMessage(responseString).queue();
                }, () -> event.getChannel().sendMessage("No data found for `" + address + "`!").queue());
    }
}