package com.android.poc.keeplife

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.keeplife.KeepBroadCastReceiver

class KeepLifeReceiver : BroadcastReceiver() {

    companion object {
        private const val Type_Listener = "listener"
        private const val Type_Close = "close"
        private const val Type_Open = "open"
        private const val Type_Error = "error"
        private const val TYPE_LIFE = "life"
    }

    override fun onReceive(context: Context, intent: Intent) {
        KeepBroadCastReceiver.init(context)

        when (val type = intent.getStringExtra("type")) {
            Type_Error -> { // 收到关闭的广播
                KeepBroadCastReceiver.close(context)
            }

            Type_Listener -> {
                KeepBroadCastReceiver.listener(type)
            }

            TYPE_LIFE -> {
                KeepBroadCastReceiver.life(type)
            }
        }
    }
}