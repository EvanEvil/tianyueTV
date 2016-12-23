package com.tianyue.tv.Util;


import android.content.Context;
import android.util.Log;

import com.aodianyun.dms.android.DMS;
import com.tianyue.tv.MyApplication;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

/**
 * 奥点云双向聊天消息
 * Created by hasee on 2016/12/9.
 */
public class DmsUtil {
    private static DmsUtil dmsUtil;

    private static String pubKey = "pub_10b9de636853250321775ca9cafd79fe";

    private static String subKey = "sub_150bb87bced5009531ab1cc5471c2be7";

    private String TAG = this.getClass().getSimpleName();

    private String topic;

    private Context context;

    public static final int SUCCESS = 0xff01;

    public static final int FAILURE = 0xff02;

    private MessageCallBack messageCallBack;

    private SendMessageCallBack sendMessageCallBack;

    private DmsInitCallBack dmsInitCallBack;

    private DmsUtil(Context context, String pubKey, String subKey) {
        this.context = context;
        this.pubKey = pubKey;
        this.subKey = subKey;
    }

    public static DmsUtil instance(Context context) {
        return instance(context, pubKey, subKey);
    }

    public static DmsUtil instance(Context context, String pubKey, String subKey) {
        if (dmsUtil == null) {
            synchronized (DmsUtil.class) {
                if (dmsUtil == null) {
                    dmsUtil = new DmsUtil(context, pubKey, subKey);
                }
            }
        }
        return dmsUtil;
    }

    /**
     * 初始化DMS
     */
    public void initDMS(String topic) {
        this.topic = topic;
        DMS.init(context, pubKey, subKey, new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                if (messageCallBack != null) {
                    messageCallBack.onMessage(mqttMessage.toString());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    public void connectDMS() {
        try {
            DMS.connect(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.i("connect dms success", " ");
                    if (dmsInitCallBack != null) {
                        dmsInitCallBack.connectState(SUCCESS,iMqttToken.toString());
                    }
                    subscribeTopic();
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.i("failed", throwable.toString());
                    if (dmsInitCallBack != null) {
                        dmsInitCallBack.connectState(FAILURE,iMqttToken.toString());
                    }
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    //绑定话题
    private void subscribeTopic() {
        try {
            if (topic == null) return;
            DMS.subscribe(topic, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.i("SubscribeTopic", "onSuccess");
                    if (dmsInitCallBack != null) {
                        dmsInitCallBack.subscribeState(SUCCESS,iMqttToken.toString());
                    }
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.i("SubscribeTopic", "onFailure");
                    if (dmsInitCallBack != null) {
                        dmsInitCallBack.subscribeState(FAILURE,iMqttToken.toString());
                    }
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    //解绑话题
    private void unSubscribeTopic() {
        try {
            if (topic == null) return;
            DMS.unsubscribe(topic, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.i(TAG, "onSuccess: " + iMqttToken.toString());
                    if (dmsInitCallBack != null) {
                        dmsInitCallBack.unSubscribeState(SUCCESS,iMqttToken.toString());
                    }
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    if (dmsInitCallBack != null) {
                        dmsInitCallBack.unSubscribeState(FAILURE,iMqttToken.toString());
                    }
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    //断开连接
    public void disconnect() {
        unSubscribeTopic();
        try {
            DMS.disconnect(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.e(TAG,"disconnect:"+"onSuccess");
                    if (dmsInitCallBack != null) {
                        dmsInitCallBack.disconnectState(SUCCESS,iMqttToken.toString());
                    }
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.e(TAG,"disconnect:"+"onFailure");
                    if (dmsInitCallBack != null) {
                        dmsInitCallBack.disconnectState(FAILURE,iMqttToken.toString());
                    }
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMessage(String message) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String seconds = "";
        if (date.getSeconds()<10) {
            seconds = "0"+date.getSeconds();
        } else {
            seconds = seconds+date.getSeconds();
        }
        message = MyApplication.instance().getUser().getNickName() + "{aodiandmssplit}" +
                date.getHours() + ':' + date.getMinutes() + ':' + seconds + "{aodiandmssplit}" + message;
        try {
            DMS.publish(topic, message.getBytes("UTF-8"), new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken iMqttToken) {
                    Log.i("dms", "publish message success");
                    if (sendMessageCallBack != null) {
                        sendMessageCallBack.onSuccess(iMqttToken.toString());
                    }
                }

                @Override
                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                    Log.i("dms", "publish message failure");
                    if (sendMessageCallBack != null) {
                        sendMessageCallBack.onFailure(iMqttToken.toString());
                    }
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解码
     *
     * @param src
     * @return
     */
    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    public void setMessageCallBack(MessageCallBack messageCallBack) {
        this.messageCallBack = messageCallBack;
    }

    public void setSendMessageCallBack(SendMessageCallBack sendMessageCallBack) {
        this.sendMessageCallBack = sendMessageCallBack;
    }

    public void setDmsInitCallBack(DmsInitCallBack dmsInitCallBack) {
        this.dmsInitCallBack = dmsInitCallBack;
    }

    /**
     * -----------------------------------------回调监听---------------------------------------------------
     */
    public interface MessageCallBack {
        void onMessage(String result);
    }

    public interface SendMessageCallBack {
        void onSuccess(String result);

        void onFailure(String result);
    }

    public interface DmsInitCallBack {
        void connectState(int state, String result);

        void subscribeState(int state, String result);

        void unSubscribeState(int state, String result);

        void disconnectState(int state, String result);
    }

}
