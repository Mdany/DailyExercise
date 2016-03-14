package cn.yuguo.mydoctor.framework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.List;

import cn.yuguo.mydoctor.application.YuguoApplication;
import cn.yuguo.mydoctor.utils.PrefUtils;

public enum ImageLoader {
    INSTANCE;

    public static final String TAG = "UniversalImageLoaderUtil";

    public static final String QI_NIU_IMAGE_2 = "?imageView2/%d";
    /**
     * 模式0
     * /0/w/<LongEdge>/h/<ShortEdge>
     * 限定缩略图的长边最多为<LongEdge>，短边最多为<ShortEdge>，进行等比缩放，不裁剪。
     * 如果只指定 w 参数则表示限定长边（短边自适应），只指定 h 参数则表示限定短边（长边自适应）。
     */
    public static final int QI_NIU_IMAGE_2_MODE_0 = 0;
    /**
     * 模式1
     * /1/w/<Width>/h/<Height>
     * 限定缩略图的宽最少为<Width>，高最少为<Height>，进行等比缩放，居中裁剪。
     * 转后的缩略图通常恰好是 <Width>x<Height> 的大小（有一个边缩放的时候会因为超出矩形框而被裁剪掉多余部分）。
     * 如果只指定 w 参数或只指定 h 参数，代表限定为长宽相等的正方图。
     */
    public static final int QI_NIU_IMAGE_2_MODE_1 = 1;
    /**
     * 模式2
     * /2/w/<Width>/h/<Height>
     * 限定缩略图的宽最多为<Width>，高最多为<Height>，进行等比缩放，不裁剪。
     * 如果只指定 w 参数则表示限定宽（长自适应），只指定 h 参数则表示限定长（宽自适应）。
     * 它和模式0类似，区别只是限定宽和高，不是限定长边和短边。
     * 从应用场景来说，模式0适合移动设备上做缩略图，模式2适合PC上做缩略图。
     */
    public static final int QI_NIU_IMAGE_2_MODE_2 = 2;
    /**
     * 模式3
     * /3/w/<Width>/h/<Height>
     * 限定缩略图的宽最少为<Width>，高最少为<Height>，进行等比缩放，不裁剪。
     * 如果只指定 w 参数或只指定 h 参数，代表长宽限定为同样的值。
     * 你可以理解为模式1是模式3的结果再做居中裁剪得到的。
     */
    public static final int QI_NIU_IMAGE_2_MODE_3 = 3;
    /**
     * 模式4
     * /4/w/<LongEdge>/h/<ShortEdge>
     * 限定缩略图的长边最少为<LongEdge>，短边最少为<ShortEdge>，进行等比缩放，不裁剪。
     * 如果只指定 w 参数或只指定 h 参数，表示长边短边限定为同样的值。
     * 这个模式很适合在手持设备做图片的全屏查看（把这里的长边短边分别设为手机屏幕的分辨率即可），
     * 生成的图片尺寸刚好充满整个屏幕（某一个边可能会超出屏幕）。
     */
    public static final int QI_NIU_IMAGE_2_MODE_4 = 4;
    /**
     * 模式5
     * /5/w/<LongEdge>/h/<ShortEdge>
     * 限定缩略图的长边最少为<LongEdge>，短边最少为<ShortEdge>，进行等比缩放，居中裁剪。
     * 如果只指定 w 参数或只指定 h 参数，表示长边短边限定为同样的值。
     * 同上模式4，但超出限定的矩形部分会被裁剪。
     */
    public static final int QI_NIU_IMAGE_2_MODE_5 = 5;
    /**
     * 七牛图片宽度
     */
    public static final String QI_NIU_IMAGE_2_WIDTH = "/w/%d";
    /**
     * 七牛图片高度
     */
    public static final String QI_NIU_IMAGE_2_HEIGHT = "/h/%d";
    /**
     * 七牛图片是否支持渐进显示
     */
    public static final String QI_NIU_IMAGE_2_INTERLACE = "/interlace/%d";
    /**
     * 不支持渐进显示
     */
    public static final int QI_NIU_IMAGE_2_INTERLACE_0 = 0;
    /**
     * 支持渐进显示
     */
    public static final int QI_NIU_IMAGE_2_INTERLACE_1 = 1;


    DisplayImageOptions options;
    DisplayImageOptions optionsNoFading;

    DisplayImageOptions optionsRoundCorner;

    ImageLoader() {
    }

    private Context mContext = null;

    public void init(Context ctx) {
        mContext = ctx;

        File cacheDir = StorageUtils.getCacheDirectory(mContext);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                //.memoryCacheExtraOptions(1024, 1024)
                // default = device screen dimensions
                // .discCacheExtraOptions(480, 200, CompressFormat.JPEG, 75,
                // null)
                .threadPoolSize(3)
                        // default
                .threadPriority(Thread.NORM_PRIORITY - 1)
                        // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                        // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(4 * 1024 * 1024))
                .memoryCacheSize(4 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                        // default
                .discCache(new UnlimitedDiscCache(cacheDir))
                        // default
                .discCacheSize(50 * 1024 * 1024).discCacheFileCount(100).discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(mContext)) // default
                        // .imageDecoder(new BaseImageDecoder()) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
//				.writeDebugLogs()
                .build();

        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
//		 .showImageOnLoading(R.drawable.default_icon_2)
                // .showImageForEmptyUri(R.drawable.empty) // resource or drawable
                // .showImageOnFail(R.drawable.error) // resource or drawable
                .resetViewBeforeLoading(false) // default
                .delayBeforeLoading(0).cacheInMemory(true).cacheOnDisc(true)
                        // .preProcessor(...)
                        // .postProcessor(...)
                        // .extraForDownloader(...)
                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                        // .decodingOptions(...)
                        // universalimageloader中有三个类实现了这个接口。
                        // SimpleBitmapDisplayer没有任何动画效果
                        // RoundedBitmapDisplayer图片加上圆角
                        // FadeInBitmapDisplayer加入渐入动画
                .displayer(new FadeInBitmapDisplayer(1000, true, false, false)).handler(new Handler()) // default
                .build();

        optionsNoFading = new DisplayImageOptions.Builder().resetViewBeforeLoading(false) // default
                .delayBeforeLoading(0).cacheInMemory(true).cacheOnDisc(true).considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .cacheOnDisc(true).displayer(new SimpleBitmapDisplayer()).handler(new Handler()) // default
                .build();

        optionsRoundCorner = new DisplayImageOptions.Builder().resetViewBeforeLoading(false).delayBeforeLoading(0).cacheInMemory(true).cacheOnDisc(true).considerExifParams(false)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new RoundedBitmapDisplayer(6)).handler(new Handler()) // default
                .build();
    }

    public String convertedImageUri(String uri) {
        if (uri == null) {
            return null;
        } else if (null == Uri.parse(uri).getScheme()) {
            return "http://" + getCDNHost() + "/" + uri;
        }
        return uri;
    }

    private String getCDNHost() {
//		return "7xikx2.com1.z0.glb.clouddn.com";
        return PrefUtils.getUserCityPreference(YuguoApplication.yuguoApplication.getApplicationContext(),
                PrefUtils.KEY_QINIU,
                PrefUtils.KEY_QINIU_KEY,
                PrefUtils.KEY_QINIU_DEFAULT);
    }

    public void loadSynchronized(String uri, ImageView v) {
        if (uri == null) return;
        v.setImageBitmap(com.nostra13.universalimageloader.core.ImageLoader.getInstance().loadImageSync(convertedImageUri(uri)));
    }

    public void load(String uri, ImageView v) {
        //这个会将图片清空，可以简单解决ListView图片跳动问题。最根本的解决之道是设置默认图片
        //com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(null, v, options);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(convertedImageUri(uri), v, options);
    }

    public void loadNoFading(String uri, ImageView v) {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(convertedImageUri(uri), v, optionsNoFading);
    }

    public void loadRoundCorner(String uri, ImageView v) {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(convertedImageUri(uri), v, optionsRoundCorner);
    }

    public void load(String uri, ImageView v, ImageLoadingListener lis, ImageLoadingProgressListener lis2) {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(convertedImageUri(uri), v, options, lis, lis2);
    }

    public void clearCache() {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().clearDiscCache();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().clearMemoryCache();
    }

    public Bitmap getCacheImage(String url) {
        if (url == null) return null;
        List<Bitmap> list = MemoryCacheUtil.findCachedBitmapsForImageUri(convertedImageUri(url), com.nostra13.universalimageloader.core.ImageLoader.getInstance().getMemoryCache());
        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    /**
     * @param url
     * @return
     */
    public Bitmap getCacheImage(String url, int mode, int width, int height, int interlace) {
        if (url == null) return null;
        List<Bitmap> list = MemoryCacheUtil.findCachedBitmapsForImageUri(convertedImageUri(url, mode, width, height, interlace), com.nostra13.universalimageloader.core.ImageLoader.getInstance().getMemoryCache());
        if (list != null && list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    /**
     * @param url：图片路径
     * @param v：资源控件
     * @param mode：模式
     * @param width：图片宽度
     * @param height：图片高度
     * @param interlace：渐进显示
     */
    public void load(String url, ImageView v, int mode, int width, int height, int interlace) {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(convertedImageUri(url, mode, width, height, interlace), v, options);
    }

    /**
     * 对Uri进行处理
     *
     * @param uri：图片路径
     * @param mode：模式
     * @param width：图片宽度
     * @param height：图片高度
     * @param interlace：渐进显示
     * @return
     */
    public String convertedImageUri(String uri, int mode, int width, int height, int interlace) {
        if (mode < QI_NIU_IMAGE_2_MODE_0 || mode > QI_NIU_IMAGE_2_MODE_5) {
            mode = QI_NIU_IMAGE_2_MODE_0;
        }
        String formatUri = uri + String.format(QI_NIU_IMAGE_2, mode);
        String formatWidth = "";
        if (width >= 0) {
            formatWidth = String.format(QI_NIU_IMAGE_2_WIDTH, width);
        }
        String formatHeight = "";
        if (height >= 0) {
            formatHeight = String.format(QI_NIU_IMAGE_2_HEIGHT, height);
        }
        String formatInterlace = "";
        if (interlace == QI_NIU_IMAGE_2_INTERLACE_0 || interlace == QI_NIU_IMAGE_2_INTERLACE_1) {
            formatInterlace = String.format(QI_NIU_IMAGE_2_INTERLACE, interlace);
        }
        String formatUrl = formatUri + formatWidth + formatHeight + formatInterlace;
        return convertedImageUri(formatUrl);
    }

    /**
     * 加载本地图片
     *
     * @param imagePath
     * @param imageView
     */
    public static void loadNativeImage(String imagePath, ImageView imageView, int width, int height) {
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().build();
        BitmapFactory.Options options = displayImageOptions.getDecodingOptions();
        setBitmapFactoryOptions(imagePath, options, width, height);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("file://" + imagePath, imageView, displayImageOptions);
    }

    /**
     * 加载Drawable图片
     *
     * @param imageID
     * @param imageView
     */
    public static void loadDrawableImage(int imageID, ImageView imageView) {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("drawable://" + imageID, imageView);
    }

    public static void setBitmapFactoryOptions(String imgPath, BitmapFactory.Options newOpts, float pixelW, float pixelH) {
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
    }
}