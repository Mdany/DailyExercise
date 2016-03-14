package cn.yuguo.mydoctor.utils.tools;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import cn.yuguo.mydoctor.utils.SMSUtils;

/**
 * Created by chenyu on 15/12/17.
 */
public class SMSContentObserver extends ContentObserver {
    private static final String KEY_BODY = "body";
    private static final String KEY_READ = "read";
    private static final String KEY_CODE = "code";
    /**
     * 短信cursor
     */
    private Cursor cursor = null;
    /**
     * 调用content
     */
    private Context mContext;
    /**
     * 短信内容
     */
    private String body;
    /**
     * 纪录resolver
     */
    private ContentResolver resolver;
    /**
     * update UI
     */
    private Handler mHandler;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SMSContentObserver(Context mContext, Handler handler) {
        super(handler);
        this.mContext = mContext;
        this.mHandler = handler;
        this.resolver = mContext.getContentResolver();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        cursor = resolver.query(Uri.parse(SMSUtils.SMS_URI_INBOX),
                new String[]{"_id", "address", KEY_READ, KEY_BODY},
                " read=?",
                new String[]{"0"}, "_id desc");

        ContentValues contentValues = new ContentValues();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                body = cursor.getString(cursor.getColumnIndex(KEY_BODY));
                if (!TextUtils.isEmpty(body) && body.contains(SMSUtils.SENDER)) {
                    //标记为已读
                    contentValues.clear();
                    contentValues.put(KEY_READ, 1);
                    resolver.update(Uri.parse(SMSUtils.SMS_URI_INBOX), contentValues, " _id=?", new String[]{cursor.getLong(0) + ""});

                    //更新UI
                    Message msg = mHandler.obtainMessage(1);
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_CODE,SMSUtils.dynamicSMSBody(body));
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                    break;
                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}
