package com.neucore.neulink.impl;

import android.content.Context;
import android.util.Log;

import com.neucore.neulink.IMqttCallBack;
import com.neucore.neulink.IProcessor;
import com.neucore.neulink.util.RequestContext;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.UUID;

public class NeulinkMsgCallBack implements IMqttCallBack {

    private String TAG = "EasyMqttCallBack";

    private Context context;
    private NeulinkService service;
    //private MessageDaoUtils messageDaoUtils;

    public NeulinkMsgCallBack(Context context, NeulinkService service){
        this.context = context;
        this.service = service;
    }
    @Override
    public void messageArrived(String topicStr, String message, int qos) {

        NeulinkTopicParser.Topic topic = NeulinkTopicParser.getInstance().parser(topicStr,qos);

        String reqId = topic.getReqId();
        RequestContext.setId(reqId==null? UUID.randomUUID().toString():reqId);

        IProcessor processor = NeulinkProcessorFactory.build(context,topic);

        try {
            if(processor!=null){
                processor.execute(topic,message);
            }
            else {
                throw new Exception(topicStr+"没有找到相关的Processor实现类...");
            }
        }
        catch (Throwable ex){
            Log.e(TAG,"messageArrived",ex);
        }
        finally {
            Log.d(TAG,"topic:"+ topicStr+",message:"+message);
            RequestContext.remove();
        }
    }

    public void connectComplete(boolean reconnect, String serverURI){

    }

    @Override
    public void connectionLost(Throwable arg0) {
        //连接断开
        Log.d(TAG,"connectionLost");
        Log.e(TAG,"connectFailed",arg0);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        Log.d(TAG,"deliveryComplete");
    }

    @Override
    public void connectSuccess(IMqttToken arg0) {
        Log.d(TAG,"connectSuccess");
    }

    @Override
    public void connectFailed(IMqttToken arg0, Throwable arg1) {
        //连接失败
        Log.e(TAG,"connectFailed",arg1);
    }

}
