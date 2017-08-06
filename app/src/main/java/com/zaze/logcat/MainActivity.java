package com.zaze.logcat;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.zaze.aarrepo.commons.log.ZLog;
import com.zaze.aarrepo.utils.ThreadManager;
import com.zaze.aarrepo.utils.ViewUtil;


public class MainActivity extends AppCompatActivity {

    private TextView timerTv;
    private CountDownTimer timer;

    String packageName = "com.zaze.logcat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerTv = ViewUtil.findView(this, R.id.main_timer_tv);
        findViewById(R.id.main_catch_all_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });

        findViewById(R.id.main_shutdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.INSTANCE.shutdownAllLogcat();
            }
        });

        ThreadManager.getInstance().runInBackgroundThread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    ZLog.i(TAG.tag, "测试日志" + i);
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
            }
        });

        timer = new CountDownTimer(5000L, 1000L) {
            @Override
            public void onTick(long l) {
                ZLog.i(TAG.tag, l + "");
                timerTv.setText("剩余时间 : " + l);
            }

            @Override
            public void onFinish() {
                timerTv.setText("剩余时间 : " + 0);
                Util.INSTANCE.shutdownAllLogcat();
//                int pid = getAppPid("com.zaze.logcat");
//                RootCmd.execCmd("kill -9 " + pid);
//                Process.killProcess(pid);
            }
        };
    }


    private void start() {
        ThreadManager.getInstance().runInSingleThread(new Runnable() {
            @Override
            public void run() {
                Util.INSTANCE.shutdownAllLogcat();
                int pid = Util.INSTANCE.getAppPid(packageName);
                String filePath = Environment.getExternalStorageDirectory().getPath() + "/log/log_test.log";
//                ZFileUtil.INSTANCE.createFile(filePath);
                ZLog.e(TAG.tag, "filePath : " + filePath);
//                        String cmd = "logcat -v time -r 200 -f /sdcard/log_test.log |grep " + pid;
                String[] cmdArray = new String[]{
//                        "rm -r " + filePath,
//                        "logcat -c",
//                                "logcat -v time | grep " + pid + " > " + filePath
                        "logcat -v time -r 250 -f " + filePath + "|grep " + pid
                };
                // 上传的时候 合并Log到一个文件上传
//                        String cmd = "logcat -v time cf " + filePath;
                int code = RootCmd.execCmd(cmdArray);
                if (!RootCmd.isSuccess(code)) {
                    ZLog.e(TAG.tag, "RootCmd error");
                }
            }
        });
//                run();
    }

    public void run() {
        timer.cancel();
        timer.start();
    }
}
