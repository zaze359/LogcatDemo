package com.zaze.logcat

import android.os.Process
import com.zaze.aarrepo.commons.log.ZLog
import com.zaze.aarrepo.utils.StringUtil
import java.util.*

/**
 * Description :

 * @author : ZAZE
 * *
 * @version : 2017-07-31 - 10:17
 */
object Util {
    val logPath: String
        get() = "/sdcard/log_test.log"


    fun getAppPid(packageName: String): Int {
        ZLog.i(TAG.tag, "getAppPid : " + packageName)
        val result = RootCmd.execCmdForRes("ps | grep " + packageName)
        if (RootCmd.isSuccess(result.result) && result.msgList.size > 0) {
            val fields = result.msgList[0].split("\\s+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (fields.size > 1) {
                return StringUtil.parseInt(fields[1])
            }
        }
        return 0
    }

    fun getLogcatPid(cmd: String): Int {
        ZLog.i(TAG.tag, "getLogcatPid")
        val pidSet = getLogcatPidArray(cmd)
        val iterator = pidSet.iterator()
        if (iterator.hasNext()) {
            return StringUtil.parseInt(iterator.next())
        }
        return 0
    }

    fun getLogcatPidArray(cmd: String): Set<String> {
        ZLog.i(TAG.tag, "getLogcatPid")
        val result = RootCmd.execCmdForRes(cmd)
        val pidSet = HashSet<String>()
        if (RootCmd.isSuccess(result.result)) {
            for (logcatLine in result.msgList) {
                val fields = logcatLine.split("\\s+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                if (fields.size >= 9) {
                    val name = fields[8]
                    if ("logcat" == name) {
                        ZLog.i(TAG.tag, "logcatLine : " + logcatLine)
                        pidSet.add(fields[1])
                    }
                }
            }
        }
        return pidSet
    }

    fun shutdownAllLogcat() {
        val pidArray = getLogcatPidArray("ps | grep logcat")
        for (pid in pidArray) {
            ZLog.i(TAG.tag, "shutdown : " + pid)
            Process.killProcess(StringUtil.parseInt(pid))
            //            RootCmd.execCmd("kill -9 " + pid);
        }
    }
}
