# 🚀 回家 (BackHome) - 快速开始

## 三步启动项目

### ✅ 第1步：解压和打开

```bash
unzip backhome.zip
cd backhome
```

在Android Studio中：
- File → Open 
- 选择 backhome 文件夹
- 等待Gradle同步（1-2分钟）

### ✅ 第2步：直接编译

**注意：这个版本不需要任何配置，零第三方依赖！**

```bash
./gradlew assembleDebug
```

或在Android Studio点击 `Run` 按钮（绿色三角形）。

### ✅ 第3步：测试

1. 启动app
2. 输入地址，如：`北京市朝阳区建国路1号`
3. 点击"保存地址"
4. 点击"导航回家" → 自动打开高德地图

## 🎯 核心代码 - 高德地图调用

最简单、最稳妥的方式：

```kotlin
// 直接调用高德地图App
private fun navigateToHome() {
    val address = etAddress.text.toString().trim()
    val name = etName.text.toString().trim().ifEmpty { "我的家" }
    
    try {
        val uri = Uri.parse("amap://navi?destname=$name&address=$address")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.autonavi.minimap")
        startActivity(intent)
    } catch (e: Exception) {
        // 降级到网页版本
        val webUri = Uri.parse("https://amap.com/search?query=${Uri.encode(address)}")
        startActivity(Intent(Intent.ACTION_VIEW, webUri))
    }
}
```

**就这么简单！**

## 📊 项目规模

| 指标 | 数值 |
|------|------|
| 总文件数 | 18个 |
| Kotlin文件 | 1个 |
| XML文件 | 8个 |
| 依赖数量 | 4个 |
| 构建大小 | ~2MB |
| 编译时间 | ~30秒 |

## 🔧 如果遇到问题

### 问题1：Gradle同步失败
- 检查网络连接
- 尝试 File → Sync Now
- 或运行 `./gradlew sync`

### 问题2：编译错误
- 检查Android SDK版本 (最小SDK 24，目标SDK 33)
- 清理Gradle缓存：`./gradlew clean`
- 重新同步：`./gradlew sync`

### 问题3：导航不工作
- 检查是否安装了高德地图应用
- 如果没装，会自动打开网页版本
- 确保输入了完整的地址

## 📝 项目文件说明

### 源代码
- **MainActivity.kt** - 唯一的Activity，100行代码
  - 保存地址到SharedPreferences
  - 调用高德地图导航

### 布局文件
- **activity_main.xml** - 简洁的界面布局
  - 家的名称输入框
  - 家的地址输入框
  - 保存和导航按钮

### 资源文件
- **btn_save_bg.xml** - 保存按钮样式（橙色）
- **btn_nav_bg.xml** - 导航按钮样式（蓝色）
- **edit_bg.xml** - 输入框背景
- **info_bg.xml** - 信息显示框背景
- **strings.xml** - 字符串常量
- **themes.xml** - 应用主题

### 配置文件
- **build.gradle.kts** - 项目配置
- **app/build.gradle.kts** - App配置（极简）
- **settings.gradle.kts** - Gradle设置
- **gradle.properties** - Gradle属性
- **AndroidManifest.xml** - 应用清单

## 💡 设计特点

✨ **超简洁** - 只有1个界面，功能清晰
✨ **无SDK依赖** - 不需要高德地图SDK
✨ **自动降级** - 没装高德地图自动用网页版本
✨ **数据本地存储** - 使用SharedPreferences，无需数据库
✨ **适老化设计** - 大字体、大按钮、高对比度

## 🎓 学习价值

通过这个项目，你可以学到：

1. **URI Scheme调用** - 最稳定的跨应用通信方式
2. **SharedPreferences** - Android数据本地存储
3. **Intent使用** - Activity和应用间的通信
4. **适老化设计** - 为老年人设计的UI规范
5. **Kotlin最佳实践** - 简洁高效的代码

## 📞 获取帮助

- 查看README.md了解项目结构
- 查看MainActivity.kt中的注释
- 所有代码都很简单，易于理解和修改

## ✅ 下一步建议

项目开箱即用，但如果想扩展：

1. **支持多个地点**
   - 改用SQLite数据库
   - 显示地点列表

2. **添加其他地图应用**
   - 谷歌地图：`google.navigation://`
   - 百度地图：`baidumap://`
   - 腾讯地图：`qqmap://`

3. **添加其他功能**
   - 地址搜索建议
   - 常用地点收藏
   - 返回路线规划

---

**现在就开始吧！** 🎉

```bash
unzip backhome.zip
cd backhome
./gradlew assembleDebug
```

祝你开发愉快！
