
package com.android.antitheft.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.Config;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.ParseHelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import com.parse.SaveCallback;
import com.parse.ParseException;

/** Records an audio on service start. */
public class WhosThatSoundService extends AntiTheftService implements Recorder.OnStateChangedListener {

    public static final String TAG = "WhosThatSoundService";

    public static final int SOUND_STOP_RECORDING = 1;

    private static final int SOUND_RECORDING_LENGHT = 10000; // MILISECONDS

    // Handler thread off of ui
    private HandlerThread mRecordThread;
    private static final String RECORDER_THREAD = "recorder_thread";
    private Handler mRecordHandler;
    private Recorder mRecorder;
    static final int SAMPLERATE_AMR_WB = 16000;
    static final int BITRATE_AMR_WB = 16000;
    static final int START_RECORDING = 1;
    static final int STOP_AND_SAVE = 2;
    static final int DELETE = 9;
    int mAudioOutputFormat = MediaRecorder.OutputFormat.AMR_WB;
    String mAmrWidebandExtension = ".awb";
    int mAudioSourceType = MediaRecorder.AudioSource.MIC;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == SOUND_STOP_RECORDING) {
                    // stop recording sound
                    mRecorder.stop();
                    saveSample();
                }
            } catch (Exception e) {
                // Log, don't crash!
                e.printStackTrace();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecorder = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initRecorder();
        return Service.START_NOT_STICKY;
    }

    /**
     * sound recording part
     */
    private void initRecorder() {
        mRecorder = new Recorder();
        mRecorder.setOnStateChangedListener(this);
        startRecordingAmrWideband();
        Message msg = new Message();
        msg.what = SOUND_STOP_RECORDING;
        mHandler.sendMessageDelayed(msg, SOUND_RECORDING_LENGHT);
    }

    /*
     * If we have just recorded a smaple, this adds it to the media data base and sets the result to
     * the sample's URI.
     */
    private void saveSample() {
        byte[] bFile = new byte[(int) mRecorder.sampleFile().length()];
        try {
            // convert file into array of bytes
            FileInputStream fileInputStream = new FileInputStream(mRecorder.sampleFile());
            fileInputStream.read(bFile);
            fileInputStream.close();
            ParseHelper.initializeFileParseObject(DeviceInfo.getIMEI(getApplicationContext()),
                    bFile, mRecorder.sampleFile().getName()).saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException parseException) {
                    mRecorder.delete();
                    stopSelf();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRecordingAmrWideband() {
        mRecorder.setSamplingRate(BITRATE_AMR_WB);
        mRecorder.startRecording(mAudioOutputFormat, mAmrWidebandExtension,
                this, mAudioSourceType, MediaRecorder.AudioEncoder.AMR_WB);
    }

    /*
     * Called when Recorder changed it's state.
     */
    public void onStateChanged(int state) {
        if (state == Recorder.PLAYING_STATE || state == Recorder.RECORDING_STATE) {
        }

        if (state == Recorder.RECORDING_STATE) {
        } else {
        }
    }

    @Override
    public void onError(int error) {
        // TODO Auto-generated method stub

    }

}
