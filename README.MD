# ProxyCheck-DiscordBOT

Simple Discord Bot to check IP addresses for proxy detection built with Redis. This bot also displays a lot of useful information about provided IP address, it uses [proxycheck.io API](https://proxycheck.io/) to resolve everything about IP address that you can imagine.

<img src="https://i.imgur.com/rBCiZxM.png">

---
### How it works?

It's very simple, when you ask your bot to display information about specific IP address it will send request to proxycheck.io and cache results in Redis for specific period of time, so we won't send API request to proxycheck.io if we have data in our Redis cache.

**Cache TTL (Time To Live)** can be configured in `redis.yml` configuration file. You can also adjust your own pool and connection settings if you need. 

### What data can be displayed?

According to endpoint we use to download data about IP address, you can get these values:
* `ASN`
* `Range`
* `Hostname`
* `ISP`
* `Organization`
* `Continent data (name and code)`
* `Country + ISO Code`
* `Region data (name and code)`
* `Time Zone`
* `City`
* `Latitude and Longitude`
* `Currency data (code, name and symbol)`
* `Proxy (if IP address is proxy or vpn)`
* `Type`

You can see example API output on [proxycheck.io website](https://proxycheck.io/) or making your own request.

### Why redis?

Redis KV (key-value) cache is very fast and simple to work with, if you want to use [Free Plan](https://proxycheck.io/pricing/) in proxycheck.io then you probably don't want to waste daily requests limit for same IP, you also probably want faster responses so that's why I decided to use Redis in this project.

### Requirements

* Discord BOT token (you can create your own bot [here](https://discord.com/developers/applications))
* Latest (working) Redis server
* JRE (17 or higher)
* proxycheck.io API key (you can request free token by going to [their website](https://proxycheck.io/))

### Configuration files

`bot.yml`
```yaml
# Bot Configuration
# Your Discord bot token
botToken: Bot token
# API key for proxycheck.io
proxyCheckApiKey: Your key
```
`redis.yml`
```yaml
# Redis Connection Configuration
# URL connection string to connect with Redis
connectionUrl: redis://127.0.0.1:6379
# Pool configuration
maxTotal: 128
maxIdle: 64
minIdle: 16
blockWhenExhausted: true
maxWaitSeconds: 15
```

### Important note

⚠️ This code was written only for educational purposes. I'm not responsible for any abuse or damage caused by this application. Remember to follow Discord [TOS](https://discord.com/terms) and [Community Guidelines](https://discord.com/guidelines).

---
Special thanks to [JetBrains](https://www.jetbrains.com/products/) company for providing development tools used to develop this project.

[<img src="https://user-images.githubusercontent.com/65517973/210912946-447a6b9a-2685-4796-9482-a44bffc727ce.png" alt="JetBrains" width="150">](https://www.jetbrains.com)