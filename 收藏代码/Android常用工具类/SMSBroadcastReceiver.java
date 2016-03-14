package cn.yuguo.mydoctor.utils.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import cn.yuguo.mydoctor.utils.SMSUtils;

/**
 * Created by chenyu on 15/12/17.
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static final String KEY_PDUS = "pdus";
    /**
     * 发送者
     */
    private String sender;
    /**
     * 短信内容
     */
    private String body;
    /**
     * context实现的listener
     */
    private SMSUtils.SMSBodyListener listener;

    public SMSBroadcastReceiver(SMSUtils.SMSBodyListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SMS_RECEIVED_ACTION.equals(action)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // 通过pdus获取接收到的所有短信息，获取短信内容
                Object[] pdus = (Object[]) bundle.get(KEY_PDUS);
                //短信数组
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < messages.length; i++) {
                    byte[] pdu = (byte[]) pdus[i];
                    messages[i] = SmsMessage.createFromPdu(pdu);
                }
                for (SmsMessage message : messages) {
                    sender = message.getOriginatingAddress();
                    body = message.getMessageBody();
                    if (!TextUtils.isEmpty(body) && body.contains(SMSUtils.SENDER)) {
                        listener.backFill(SMSUtils.dynamicSMSBody(body));
                    }
                }
                this.abortBroadcast();
            }
        }
    }
}
