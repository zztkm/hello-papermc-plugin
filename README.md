# DiscordNotify

Discord の Webhook を利用して PaperMC サーバーのアクティビティを Discord に通知するためのプラグインです。

## Build

```bash
./gradlew build
```

ビルドすると `build/libs` ディレクトリに `DiscordNotify-1.0-SNAPSHOT.jar` が作成されます。
それを PaperMC のプラグインディレクトリに格納することでプラグインを読み込ませることができます。

## 参考

- [Development Guide | PaperMC Docs](https://docs.papermc.io/paper/dev)
- [Minecraft Plugin Tutorial (in Kotlin) | Project Setup (YouTube)](https://youtu.be/5DBJcz0ceaw?si=b_EPI-PP8ozrZMb3)
- [configuration Shadow](https://gradleup.com/shadow/configuration/#configuring-shadow)
- <https://github.com/Taraj/PaperMC_Plugin>
- <https://github.com/flytegg/paper-plugin-template>
