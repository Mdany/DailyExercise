package cn.yuguo.mydoctor.utils.tools;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.math.BigDecimal;

/**
 * Created by ania on 15/7/3.
 * 主要功能有清除内/外缓存，清除数据库，清除sharedPreference，清除files和清除自定义目录
 */
public class DataCleanManager {
    /**
     * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
     *
     * @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * *
     *
     * @param context
     */
    public static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
     *
     * @param context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * * 按名字清除本应用数据库 * *
     *
     * @param context
     * @param dbName
     */
    public static void cleanDatabaseByName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * * 清除/data/data/com.xxx.xxx/files下的内容 * *
     *
     * @param context
     */
    public static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)
     *
     * @param context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * *
     *
     * @param filePath
     */
    public static void cleanCustomCache(String filePath) {
        deleteFilesByDirectory(new File(filePath));
    }

    /**
     * * 清除本应用所有的数据 * *
     *
     * @param context
     * @param filepath
     */
    public static void cleanApplicationData(Context context, String... filepath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
//        cleanDatabases(context);
//        cleanSharedPreference(context);
        //cleanFiles(context);
        for (String filePath : filepath) {
            if (filePath != null) {
                cleanCustomCache(filePath);
            }
        }
    }

    /**
     * * 删除方法 递归删除文件夹 * *
     */
    private static boolean deleteFilesByDirectory(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteFilesByDirectory(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir != null && dir.delete();
    }

    /**
     * 获取文件或者目录大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    // 获取文件
    //Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
    //Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
    public static long getFolderSize(File file) throws Exception {
        if (file == null) return 0;
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                // 如果下面还有文件
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @param deleteThisPath
     * @param filePath
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 如果下面还有文件
                    File files[] = file.listFiles();
                    for (File file1 : files) {
                        deleteFolderFile(file1.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
//        double kiloByte = size / 1024;
//        if (kiloByte < 1) {
//            return size + "Byte";
//        }
//
//        double megaByte = kiloByte / 1024;
//        if (megaByte < 1) {
//            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
//            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
//                    .toPlainString() + "KB";
//        }
//
//        double gigaByte = megaByte / 1024;
//        if (gigaByte < 1) {
//            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
//            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
//                    .toPlainString() + "MB";
//        }
//
//        double teraBytes = gigaByte / 1024;
//        if (teraBytes < 1) {
//            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
//            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
//                    .toPlainString() + "GB";
//        }
//        BigDecimal result4 = new BigDecimal(teraBytes);
//        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
//                + "TB";
        double megaByte = size / 1024;
        double gigaByte = megaByte / 1024;
        BigDecimal result2 = new BigDecimal(Double.toString(gigaByte));
        return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "MB";
    }

    /**
     * 获取格式化缓存文件或者目录大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String getCacheSize(Context mContext, File file) throws Exception {
        long size = 0;
        size += getFolderSize(file);
//        size += getFolderSize(new File("/data/data/"
//                + mContext.getPackageName() + "/databases"));
//        size += getFolderSize(mContext.getExternalCacheDir());
        size += getFolderSize(mContext.getCacheDir());
        //size += getFolderSize(mContext.getFilesDir());
        return getFormatSize(size);
    }

    /**
     * 获取格式化缓存文件或者目录大小
     *
     * @param mContext
     * @return
     * @throws Exception
     */
    public static String getCacheSize(Context mContext) throws Exception {
        long size = 0;
        size += getFolderSize(null);
//        size += getFolderSize(new File("/data/data/"
//                + mContext.getPackageName() + "/databases"));
//        size += getFolderSize(mContext.getExternalCacheDir());
        size += getFolderSize(mContext.getCacheDir());
        //size += getFolderSize(mContext.getFilesDir());
        return getFormatSize(size);
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        return sdDir != null ? sdDir.toString() : null;

    }

    /**
     * * 清除本应用所有的数据，包括登录信息及数据库信息 * *
     *
     * @param context
     * @param filepath
     */
    public static void cleanAllData(Context context, String... filepath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        if (filepath == null) {
            return;
        }
        for (String filePath : filepath) {
            cleanCustomCache(filePath);
        }
    }
}
