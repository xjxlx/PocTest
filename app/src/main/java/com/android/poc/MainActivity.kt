package com.android.poc

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.accessibility.util.AccessibilityUtil
import com.android.apphelper2.utils.FileUtil
import com.android.apphelper2.utils.LogUtil
import com.android.apphelper2.utils.SystemUtil
import com.android.apphelper2.utils.ToastUtil
import com.android.apphelper2.utils.permission.PermissionCallBackListener
import com.android.apphelper2.utils.permission.PermissionMultipleCallBackListener
import com.android.apphelper2.utils.permission.PermissionUtil
import com.android.keeplife.account.LifecycleManager
import com.android.poc.databinding.ActivityMainBinding

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
        // 文件读写权限
        permissionUtil.setCallBackListener(object : PermissionCallBackListener {
            override fun onCallBack(permission: String, isGranted: Boolean) {
                LogUtil.e("是否用后文件读写权限：$isGranted")
            }
        })
            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        // 开启保活
        mBinding.btnStartBh.setOnClickListener {
            permissionUtil.requestArray(
                arrayOf(Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_SYNC_SETTINGS, Manifest.permission.FOREGROUND_SERVICE),
                object : PermissionMultipleCallBackListener {
                    override fun onCallBack(allGranted: Boolean, map: MutableMap<String, Boolean>) {
                        LifecycleManager.instance.paddingActivity(this@MainActivity.packageName,
                            FileUtil.instance.getCanonicalNamePath(this@MainActivity::class.java))
                        LifecycleManager.instance.startLifecycle(this@MainActivity)
                        ToastUtil.show("开启保活服务")
                    }
                })
        }

        // 关闭保活
        mBinding.btnStopBh.setOnClickListener {
            LifecycleManager.instance.stopLifecycle(this@MainActivity) {
                ToastUtil.show(it)
            }
        }

        // 开启自动化服务
        mBinding.btnStartAccessibility.setOnClickListener {
            mAccessibilityUtil?.paddingActivity(this@MainActivity.packageName,
                FileUtil.instance.getCanonicalNamePath(this@MainActivity::class.java))
            mAccessibilityUtil?.startAccessibility(list) {
                ToastUtil.show(this, it)
            }
        }
        // 关闭自动化服务
        mBinding.btnStopAccessibility.setOnClickListener {
            mAccessibilityUtil?.forceStopAccessibility(this@MainActivity) {
                ToastUtil.show(it)
            }
        }
        // 打开自动化设置
        mBinding.btnOpenAccessibility.setOnClickListener {
            mAccessibilityUtil?.openAccessibilitySetting()
            ToastUtil.show("打开自动化设置")
        }

        // 打开开机启动权限
        mBinding.btnOpenQd.setOnClickListener {
            SystemUtil.openApplicationSetting(this)
        }
        // 打开电池优化权限
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

        // 打开app
        mBinding.btnOpenApp.setOnClickListener {
             SystemUtil.openApplication(this@MainActivity, "com.mobilityasia.hcp3.deepbreath.audi.deepbreathpoc")
         }
    }
}