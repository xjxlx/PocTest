package com.android.keeplife

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.android.accessibility.util.AccessibilityUtil
import com.android.apphelper2.utils.KeepLifeBroadCast
import com.android.apphelper2.utils.LogUtil
import com.android.apphelper2.utils.SystemUtil

@RequiresApi(Build.VERSION_CODES.P)
class KeepLifeReceiver : BroadcastReceiver() {
    private var context: Context? = null
    private val mAccessibilityUtil: AccessibilityUtil? by lazy {
        return@lazy AccessibilityUtil.getInstance(context!!)
    }
    private val list: ArrayList<String> by lazy {
        val listOf = arrayListOf<String>().apply {
            add("rl_root_view") // 首页 rl_root_view
            add("start_btn")// 滑动主题 start_btn
            add("iv_selector_number_start")// 数字选择页面 iv_selector_number_start
            add("iv_guidance_start")// 引导页的页面 iv_guidance_start
            add("finish_click")// 结束页面 finish_click
        }
        return@lazy listOf
    }

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        val action = intent.action
        if (TextUtils.equals(action, "com.android.lifecycle")) {
            // 1：检查自己的应用是否还活着，如果没有活着，就把自己的应用拉活
            if (!KeepLifeStatus.isRunning) {
                SystemUtil.openApplication(context, context.packageName)
                KeepLifeStatus.isRunning = true
            }

            // 2: 检测 accessiblity 服务
            LogUtil.e("收到了轮询的检测 : ------>")
            mAccessibilityUtil?.startAccessibility(list) {
                LogUtil.e("keep receiver : ", it)
            }

            //3：发送保活的权限
            KeepLifeBroadCast.sendAppKeepLifeReceiver(context, context.packageName)
        }
    }
}