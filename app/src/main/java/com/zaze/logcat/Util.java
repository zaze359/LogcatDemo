package com.zaze.logcat;

import android.os.Process;

import com.zaze.aarrepo.commons.log.ZLog;
import com.zaze.aarrepo.utils.StringUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Description :
 *
 * @author : ZAZE
 * @version : 2017-07-31 - 10:17
 */
public class Util {
    public static String getLogPath() {
        return "/sdcard/log_test.log";
    }


    public static int getAppPid(String packageName) {
        ZLog.i(TAG.tag, "getAppPid : " + packageName);
        RootCmd.CommandResult result = RootCmd.execCmdForRes("ps | grep " + packageName);
        if (RootCmd.isSuccess(result.result) && result.msgList.size() > 0) {
            String[] fields = result.msgList.get(0).split("\\s+");
            if (fields.length > 1) {
                return StringUtil.parseInt(fields[1]);
            }
        }
        return 0;
    }

    public static int getLogcatPid(String cmd) {
        ZLog.i(TAG.tag, "getLogcatPid");
        Set<String> pidSet = getLogcatPidArray(cmd);
        Iterator<String> iterator = pidSet.iterator();
        if (iterator.hasNext()) {
            return StringUtil.parseInt(iterator.next());
        }
        return 0;
    }

    public static Set<String> getLogcatPidArray(String cmd) {
        ZLog.i(TAG.tag, "getLogcatPid");
        RootCmd.CommandResult result = RootCmd.execCmdForRes(cmd);
        Set<String> pidSet = new HashSet<>();
        if (RootCmd.isSuccess(result.result)) {
            for (String logcatLine : result.msgList) {
                String[] fields = logcatLine.split("\\s+");
                if (fields.length >= 9) {
                    String name = fields[8];
                    if ("logcat".equals(name)) {
                        ZLog.i(TAG.tag, "logcatLine : " + logcatLine);
                        pidSet.add(fields[1]);
                    }
                }
            }
        }
        return pidSet;
    }

    public static void shutdownAllLogcat() {
        Set<String> pidArray = getLogcatPidArray("ps | grep logcat");
        for (String pid : pidArray) {
            ZLog.i(TAG.tag, "shutdown : " + pid);
            Process.killProcess(StringUtil.parseInt(pid));
//            RootCmd.execCmd("kill -9 " + pid);
        }
    }

//    private int getLogcatPid(String packageName) {
//        Log.d(TAG.tag, "Enter: packageName = " + packageName);
//        int pid = 0;
//
//        if (packageName.equals(LogService.PACKAGE_NAME)) {
//            return -1;
//        }
//
//        CommandResult result = ShellUtils.execCommand("ps | grep \""
//                + packageName + "\"", true, true);
//        if (result.result == 127) {
//            Log.e(LogService.LOG_CATCHER_TAG, "CMD can not execute!");
//            return -2;
//        }
//
//        Log.d(LogService.LOG_CATCHER_TAG, "result.successMsg = "
//                + result.successMsg);
//        String[] lines = result.successMsg.split("\n");
//
//        String newLogcatLine = lines[lines.length - 1];
//
//        if (newLogcatLine == null) {
//            return pid;
//        }
//
//        String[] fields = newLogcatLine.split("\\s+");
//
//        if (fields.length <= 1) {
//            return pid;
//        }
//
//        pid = Integer.parseInt(fields[1]);
//
//        return pid;
//
//    }
}
