# beenew

## 新闻Android客户端 基于Material Design

## 依赖的开源类库、工具

 - View注入框架，绑定控件和 OnClick 方法[butterknife](https://github.com/JakeWharton/butterknife)
 - facebook 的图片库，支持圆形、方形，各种缩放等[fresco](https://github.com/facebook/fresco)
 - 启动图片在线生成工具 [Launcher Icon Generator](https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html)
 - 圆形 ProgressBar [materialish-progress](https://github.com/pnikosis/materialish-progress)
 - Sqlite 轻量级ORM [ActiveAndroid](https://github.com/pardom/ActiveAndroid)
 - 访问http请求 [okhttp](https://github.com/square/okhttp)
 - Pretty日志输出 [logger](https://github.com/orhanobut/logger)
 - [阿里icon平台](http://www.iconfont.cn/)


## 后端

  - 采用[Okhttp](https://github.com/square/okhttp/) 发送请求
  - 使用[Jsoup](https://github.com/jhy/jsoup/)解析页面
  - 使用[MongoDB](https://github.com/mongodb/mongo-java-driver)进行存储
  - 使用[七牛SDK](https://github.com/qiniu/java-sdk)将图片自动上传到七牛云中