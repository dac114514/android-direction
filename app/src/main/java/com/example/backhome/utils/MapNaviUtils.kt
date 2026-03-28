package com.example.backhome.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.backhome.R
import java.net.URLEncoder

object MapNaviUtils {

    private const val GAODE_PACKAGE = "com.autonavi.minimap"

    /**
     * 判断高德地图是否已安装
     */
    fun isGaodeInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(GAODE_PACKAGE, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 跳转高德地图进行驾车导航（从当前位置 → 目的地）
     * @param context       Context
     * @param dlat          目的地纬度（必须是 GCJ-02 火星坐标）
     * @param dlon          目的地经度（必须是 GCJ-02 火星坐标）
     * @param dname         目的地名称（建议中文进行 URL 编码）
     * @param strategy      导航策略（可选）：
     *                      0=最快时间, 2=最短距离, 4=躲避拥堵, 其他见下方注释
     * @param transportMode 交通方式：0=驾车, 1=公交, 2=骑行, 3=步行, 4=电动车
     */
    fun navigateToGaode(
        context: Context,
        dlat: Double,
        dlon: Double,
        dname: String = "目的地",
        strategy: Int = 0,   // 常用：0最快, 2最短, 4躲避拥堵
        transportMode: Int = 0 // 默认驾车：0=驾车, 1=公交, 2=骑行, 3=步行, 4=电动车
    ) {
        if (!isGaodeInstalled(context)) {
            Toast.makeText(context, "未安装高德地图，即将跳转应用市场", Toast.LENGTH_SHORT).show()
            goToMarket(context)
            return
        }

        try {
            // 推荐方案：amapuri://route/plan （新版更稳定）
            val encodedName = URLEncoder.encode(dname, "UTF-8")
            val appName = context.getString(R.string.navi_app_name)
            val uriStr = "amapuri://route/plan/?" +
                    "sourceApplication=$appName" +
                    "&dlat=$dlat" +
                    "&dlon=$dlon" +
                    "&dname=$encodedName" +
                    "&dev=0" +           // 0=坐标已是高德坐标（推荐）
                    "&t=$transportMode" + // 交通方式
                    "&policy=$strategy"  // 策略参数

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(uriStr)
                setPackage(GAODE_PACKAGE)   // 强烈建议加上，防止被浏览器拦截
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)

        } catch (e: Exception) {
            // 兜底方案：旧版 androidamap://navi
            try {
                val encodedNameOld = URLEncoder.encode(dname, "UTF-8")
                val appName = context.getString(R.string.navi_app_name)
                val oldUriStr = "androidamap://navi?" +
                        "sourceApplication=$appName" +
                        "&poiname=$encodedNameOld" +
                        "&lat=$dlat" +
                        "&lon=$dlon" +
                        "&dev=0" +
                        "&style=$strategy"

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(oldUriStr)).apply {
                    setPackage(GAODE_PACKAGE)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (ex: Exception) {
                Toast.makeText(context, "跳转高德地图失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 跳转高德地图进行电动车导航（从当前位置 → 目的地）
     * @param context       Context
     * @param dlat          目的地纬度（必须是 GCJ-02 火星坐标）
     * @param dlon          目的地经度（必须是 GCJ-02 火星坐标）
     * @param dname         目的地名称（建议中文进行 URL 编码）
     * @param strategy      导航策略（可选）：
     *                      0=最快时间, 2=最短距离, 4=躲避拥堵, 其他见下方注释
     */
    fun navigateToGaodeByElectricBike(
        context: Context,
        dlat: Double,
        dlon: Double,
        dname: String = "目的地",
        strategy: Int = 0   // 常用：0最快, 2最短, 4躲避拥堵
    ) {
        navigateToGaode(context, dlat, dlon, dname, strategy, 4) // 4=电动车
    }

    /**
     * 按详细地址跳转高德（适合只有地址文本，没有经纬度的场景）
     */
    fun navigateToGaodeByAddress(
        context: Context,
        destinationAddress: String,
        destinationName: String = "目的地"
    ) {
        if (!isGaodeInstalled(context)) {
            Toast.makeText(context, "未安装高德地图，即将跳转应用市场", Toast.LENGTH_SHORT).show()
            goToMarket(context)
            return
        }

        try {
            val appName = context.getString(R.string.navi_app_name)
            val encodedAddress = URLEncoder.encode(destinationAddress, "UTF-8")
            val encodedName = URLEncoder.encode(destinationName, "UTF-8")

            // 关键：address 使用用户填写的详细地址
            val uriStr = "amapuri://route/plan/?" +
                "sourceApplication=$appName" +
                "&dlat=0.0" +           // Placeholder coordinates
                "&dlon=0.0" +           // Placeholder coordinates
                "&dname=$encodedName" +
                "&dev=0" +
                "&t=3" +                // 骑行模式（电动车）
                "&rideType=elebike"     // 电动车类型

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(uriStr)
                setPackage(GAODE_PACKAGE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "跳转高德地图失败", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 跳转高德地图路线规划（支持自定义起点 + 终点）
     */
    fun planRouteInGaode(
        context: Context,
        slat: Double? = null,      // 起点纬度（null 则使用当前位置）
        slon: Double? = null,      // 起点经度
        sname: String? = null,
        dlat: Double,
        dlon: Double,
        dname: String = "目的地"
    ) {
        if (!isGaodeInstalled(context)) {
            goToMarket(context)
            return
        }

        try {
            val encodedDName = URLEncoder.encode(dname, "UTF-8")
            val appName = context.getString(R.string.navi_app_name)
            val builder = StringBuilder("amapuri://route/plan/?sourceApplication=$appName")

            // 添加起点（可选）
            if (slat != null && slon != null) {
                builder.append("&slat=$slat&slon=$slon")
                sname?.let { builder.append("&sname=${URLEncoder.encode(it, "UTF-8")}") }
            }

            builder.append("&dlat=$dlat&dlon=$dlon&dname=$encodedDName&dev=0&t=0")

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(builder.toString())).apply {
                setPackage(GAODE_PACKAGE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(context, "路线规划跳转失败", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 跳转到应用市场下载高德地图
     */
    private fun goToMarket(context: Context) {
        try {
            val uri = Uri.parse("market://details?id=$GAODE_PACKAGE")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            // 市场打开失败则用浏览器
            val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$GAODE_PACKAGE")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }
}