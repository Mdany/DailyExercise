package cn.yuguo.mydoctor.utils.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.*;
import android.os.Process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

import cn.yuguo.mydoctor.application.YuguoApplication;
import cn.yuguo.mydoctor.utils.DateUtils;
import cn.yuguo.mydoctor.utils.PrefUtils;

/**
 * Created by chenyu on 15/01/18.
 * 运行时异常，三次以上清除数据
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    /**
     * 缓存路径
     */
    private static final String filePath = DataCleanManager.getSDPath() + "/" + "yuguo";
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/yuguo/crashLog/";
    private static final String FILE_NAME = "crash";
    private static final String FILE_TRACE = ".trace";
    /**
     * 单例
     */
    private static CrashHandler crashHandler;
    /**
     * 系统默认的handler
     */
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    /**
     * 上下文
     */
    private Context mContext;

    public static CrashHandler getInsutance() {
        if (crashHandler == null) {
            crashHandler = new CrashHandler();
        }
        return crashHandler;
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //dumpExceptionToSDCard(ex);
        //uploadExceptionToServer();

        int times = PrefUtils.getIntPreference(YuguoApplication.yuguoApplication, PrefUtils.CONFIG, PrefUtils.KEY_CRASH_FREQUENCY, 0);
        if (times >= 2) {
            //TODO 清空数据
            DataCleanManager.cleanAllData(YuguoApplication.yuguoApplication, filePath);
            PrefUtils.saveIntPreferences(YuguoApplication.yuguoApplication, PrefUtils.CONFIG, PrefUtils.KEY_CRASH_FREQUENCY, 0);
        } else {
            PrefUtils.saveIntPreferences(YuguoApplication.yuguoApplication, PrefUtils.CONFIG, PrefUtils.KEY_CRASH_FREQUENCY, ++times);
        }

        ex.printStackTrace();

        //有默认异常处理则用默认异常处理，没有异常处理则我们自己处理
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(Process.myPid());
        }
    }

    /**
     * 异常信息存到sdCard
     *
     * @param ex 异常
     */
    private void dumpExceptionToSDCard(Throwable ex) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }

        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String currentTime = DateUtils.dateFormatYMDHM(new Date(System.currentTimeMillis()));

        File file = new File(PATH + FILE_NAME + currentTime + FILE_TRACE);

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.write(currentTime);
            pw.println();
            dumpPhoneInfo(pw);
            ex.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 手机信息
     *
     * @param pw
     * @throws PackageManager.NameNotFoundException
     */
    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pio = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);

        //应用版本
        pw.print("APP Version: ");
        pw.print(pio.versionName);
        pw.print("-");
        pw.println(pio.versionCode);

        //系统版本
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("-");
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);

        //CPU架构
        pw.print("CPU ABI: ");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                ) {
            String[] abis = Build.SUPPORTED_ABIS;
            for (String abi : abis) {
                pw.print("/" + abi);
            }
        } else {
            pw.print(Build.CPU_ABI);
        }

    }

    private void uploadExceptionToServer() {
        //TODO 上传log到server
    }
}