package com.zaze.logcat

import android.app.Service
import android.content.Intent
import android.os.IBinder

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Description :

 * @author : ZAZE
 * *
 * @version : 2017-07-31 - 14:09
 */
class LoopService : Service() {
    val singleExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    override fun onCreate() {
        super.onCreate()
        singleExecutor.execute {
            while (true) {
                try {
                    loopRun()
                    Thread.sleep(1000L)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun onDestroy() {
        singleExecutor.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }


    private fun loopRun() {

    }

}
