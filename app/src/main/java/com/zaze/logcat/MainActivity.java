package com.zaze.logcat;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.zaze.aarrepo.commons.log.ZLog;
import com.zaze.aarrepo.utils.ThreadManager;
import com.zaze.aarrepo.utils.ViewUtil;

import static com.zaze.logcat.Util.getAppPid;


public class MainActivity extends AppCompatActivity {

    private TextView timerTv;
    private CountDownTimer timer;

    String packageName = "com.xuehai.response_launcher_teacher";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerTv = ViewUtil.findView(this, R.id.main_timer_tv);
        findViewById(R.id.main_catch_all_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadManager.getInstance().runInSingleThread(new Runnable() {
                    @Override
                    public void run() {
                        Util.shutdownAllLogcat();
                        int pid = getAppPid(packageName);
                        String filePath = "/sdcard/log_test.log";
//                        String cmd = "logcat -v time -r 200 -f /sdcard/log_test.log |grep " + pid;
                        String[] cmdArray = new String[]{
                                "rm -r " + filePath,
                                "logcat -c",
//                                "logcat -v time | grep " + pid + " > " + filePath
                                "logcat -v time -f " + filePath
                        };
//                        String cmd = "logcat -v time -f " + filePath;
                        int code = RootCmd.execCmd(cmdArray);
                        if (!RootCmd.isSuccess(code)) {
                            ZLog.e(TAG.tag, "RootCmd error");
                        }
                    }
                });
//                run();
            }
        });

        findViewById(R.id.main_shutdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.shutdownAllLogcat();
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
                Util.shutdownAllLogcat();
//                int pid = getAppPid("com.zaze.logcat");
//                RootCmd.execCmd("kill -9 " + pid);
//                Process.killProcess(pid);
            }
        };
    }


    public void run() {
        timer.cancel();
        timer.start();
    }
}
