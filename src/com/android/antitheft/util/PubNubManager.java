package com.android.antitheft.util;

import android.util.Log;

import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.parse.ParseHelper;
import com.pubnub.api.*;

import org.json.*;

/**
 * Created by mikalackis on 21.3.16..
 */
public class PubNubManager {

    private static final String TAG = "PubNubManager";

    private static PubNubManager mInstance;

    private Pubnub mPubNub;

    private PubNubManager() {
        mPubNub = new Pubnub("pub-c-2d59b791-04a9-41bb-8fa9-e22cc7b1ea90", "sub-c-c1d545b6-ef3b-11e5-871f-0619f8945a4f");
    }

    public static PubNubManager getInstance() {
        if (mInstance == null) {
            mInstance = new PubNubManager();
        }
        return mInstance;
    }

    public void preparePubNub() {
        Log.i(TAG,"Preparing PubNub...");
        try {
            mPubNub.subscribe(String.format(Config.CONFIG_CHANNEL_UPDATE, DeviceInfo.getInstance().getDeviceConfiguration().getClientId()), new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                            Log.i(TAG,"CONNECT CALLBACK: "+channel);
//                            pubnub.publish("my_channel", "Hello from the PubNub Java SDK", new Callback() {
//                            });
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                            System.out.println(TAG+" DISCONNECT CALLBACK");
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                            System.out.println(TAG + " RECONNECT CALLBACK");
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBEDD : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());
                            if(channel.equals(String.format(Config.CONFIG_CHANNEL_UPDATE, DeviceInfo.getInstance().getDeviceConfiguration().getClientId()))){
                                handleConfigMessage();
                            }
                            Log.i(TAG,"MESSAGE: "+message.toString());
                            System.out.println(TAG+" SUBSCRIBED CALLBACK");
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                            System.out.println(TAG + " ERROR CALLBACK");
                        }
                    }
            );
        } catch (PubnubException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    private void handleConfigMessage(){
        Log.i(TAG,"Fetch config from server");
        ParseHelper.getConfigFromServer();
    }

}
