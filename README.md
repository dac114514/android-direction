# 回家 (BackHome) - 简化版

一个超简洁的Android应用，专为老年人设计，帮助快速回家。

## 💡 关键特性

- ✅ **超简洁** - 只有1个界面，2个按钮
- ✅ **无复杂依赖** - 只用系统库，构建稳定
- ✅ **保存地址** - 使用SharedPreferences本地存储
- ✅ **调用高德地图** - 使用URI Scheme，无需SDK
- ✅ **适老化设计** - 大字体、大按钮、清晰简洁

## 📋 使用方法

1. 输入家的名称（可选，默认"我的家"）
2. 输入完整的家庭地址
3. 点击"保存地址"
4. 点击"导航回家" - 打开高德地图开始导航

## 🔧 高德地图调用方式

**不需要SDK，只用URI Scheme：**

```kotlin
// 构建URI
val uri = Uri.parse("amap://navi?destname=$name&address=$address")
val intent = Intent(Intent.ACTION_VIEW, uri)
intent.setPackage("com.autonavi.minimap")
startActivity(intent)
```

**优点：**
- 无需配置API Key
- 无需添加高德SDK依赖
- 用户体验好 - 直接打开高德地图App
- 如果没装高德地图 - 自动降级到网页版本

## 📦 项目结构

```
backhome/
├── app/src/main/
│   ├── java/com/example/backhome/
│   │   └── MainActivity.kt          (唯一的Activity)
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml    (简洁布局)
│   │   ├── drawable/                (4个背景文件)
│   │   └── values/
│   │       ├── strings.xml
│   │       └── themes.xml
│   └── AndroidManifest.xml
├── build.gradle.kts                 (极简配置)
├── app/build.gradle.kts             (只4个依赖)
└── settings.gradle.kts
```

## ⚡ 快速开始

### 步骤1：打开项目
```bash
unzip backhome.zip
# 在Android Studio中打开 File → Open → 选择文件夹
```

### 步骤2：直接编译运行
```bash
./gradlew assembleDebug
```

### 步骤3：安装到设备
```bash
./gradlew installDebug
```

或在Android Studio点击绿色Run按钮。

## 🎯 最小依赖

```gradle
implementation("androidx.core:core-ktx:1.10.1")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")
implementation("com.google.android.material:material:1.9.0")
```

只有4个基础库，没有其他复杂依赖。

## 📱 适老化设计

- 标题字体：36sp
- 标签字体：18sp  
- 输入框：60dp高度
- 按钮：70dp高度
- 高对比度：深色文字+浅色背景
- 简洁布局：竖向单列排列

## ❓ 常见问题

**Q: 没有SDK怎么调用高德地图？**  
A: 使用URI Scheme直接调用高德地图App，这是最稳妥的方式。

**Q: 如果用户没装高德地图怎么办？**  
A: 自动降级到网页版高德地图，用浏览器打开。

**Q: 数据保存在哪里？**  
A: 使用SharedPreferences保存在本地，不需要数据库。

**Q: 怎样添加多个地址？**  
A: 修改MainActivity.kt，支持多条记录即可扩展。

## 🔐 权限

只需要一个权限：
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

用于打开网页版高德地图（如果App未安装）。

## 📄 许可证

MIT License

---

**这个版本最稳妥、最简洁、最容易维护！**
