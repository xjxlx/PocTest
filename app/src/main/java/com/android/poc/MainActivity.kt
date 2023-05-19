package com.android.poc

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.accessibility.util.AccessibilityUtil
import com.android.apphelper2.utils.AppUtil
import com.android.apphelper2.utils.SystemUtil
import com.android.apphelper2.utils.ToastUtil
import com.android.apphelper2.utils.permission.PermissionCallBackListener
import com.android.apphelper2.utils.permission.PermissionMultipleCallBackListener
import com.android.apphelper2.utils.permission.PermissionUtil
import com.android.keeplife.account.LifecycleManager
import com.android.poc.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *  * 权限指令： adb shell pm grant com.android.poc android.permission.WRITE_SECURE_SETTINGS
 *
 */
@RequiresApi(Build.VERSION_CODES.P)
class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
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
    private val permissionUtil = PermissionUtil.PermissionActivity(this)
    private val mAccessibilityUtil: AccessibilityUtil? by lazy {
        return@lazy AccessibilityUtil.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        intiData()
    }

    private fun intiData() {
        mBinding.btnStartBh.setOnClickListener {
            permissionUtil.requestArray(
                arrayOf(Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_SYNC_SETTINGS, Manifest.permission.FOREGROUND_SERVICE),
                object : PermissionMultipleCallBackListener {
                    override fun onCallBack(allGranted: Boolean, map: MutableMap<String, Boolean>) {
                        LifecycleManager.instance.startLifecycle(this@MainActivity)
                        ToastUtil.show("开启保活服务")
                    }
                })
        }
        mBinding.btnStopBh.setOnClickListener {
            LifecycleManager.instance.stopLifecycle(this@MainActivity) {
                ToastUtil.show(it)
            }
        }

        mBinding.btnStartAccessibility.setOnClickListener {
            mAccessibilityUtil?.startAccessibility(list) {
                ToastUtil.show(this, it)
            }
        }
        mBinding.btnStopAccessibility.setOnClickListener {
            mAccessibilityUtil?.forceStopAccessibility(this@MainActivity) {
                ToastUtil.show(it)
            }
        }
        mBinding.btnOpenAccessibility.setOnClickListener {
            mAccessibilityUtil?.openAccessibilitySetting()
            ToastUtil.show("打开自动化设置")
        }


        mBinding.btnOpenQd.setOnClickListener {
//            SystemUtil.openApplicationSetting(this)

            lifecycleScope.launch(Dispatchers.IO) {
                while (true) {
                    AppUtil.sendAppRunningBroadcast(this@MainActivity, "com.xjx.kotlin")
                    delay(5000)
                }
            }
        }

        mBinding.btnOpenDc.setOnClickListener {
            permissionUtil.setCallBackListener(object : PermissionCallBackListener {
                override fun onCallBack(permission: String, isGranted: Boolean) {
                    val batteryOptimizations = SystemUtil.isIgnoringBatteryOptimizations(this@MainActivity)
                    if (!batteryOptimizations) {
                        SystemUtil.requestIgnoreBatteryOptimizations(this@MainActivity)
                    } else {
                        ToastUtil.show("已经优化完毕！")
                    }
                }
            })
                .request(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        }
    }
}