package com.android.poc.keeplife

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.apphelper2.utils.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class KeepLifeReceiver : BroadcastReceiver() {

    private val mSharedFlow: MutableSharedFlow<String> by lazy {
        return@lazy MutableSharedFlow()
    }
    private val mScope: CoroutineScope by lazy {
        return@lazy CoroutineScope(Dispatchers.IO)
    }

    override fun onReceive(context: Context, intent: Intent) {
        mScope.launch {
            mSharedFlow.debounce(5000)
                .collect() {
                    LogUtil.e("开始重新启动")

                }
        }
    }
}