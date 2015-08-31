package com.android.antitheft;

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
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;  
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

/** Takes a single photo on service start. */
public class WhosThatService extends Service {
	
	public static final String TAG="WhosThatService";
	
    private static final int FRONT_CAMERA = CameraCharacteristics.LENS_FACING_FRONT;
    
    public static final int CAMERA_IMAGE = 0;
    public static final int CAMERA_VIDEO = 1;
    public static final int CAMERA_STOP_RECORDING = 2;
    
    public static final String SERVICE_PARAM = "service_param";
    
    private static final int CAMERA_VIDEO_LENGHT = 5000; //MILISECONDS
	
	private CameraDevice mCameraDevice;
	private CaptureRequest.Builder mPreviewBuilder;
	private CameraCaptureSession mPreviewSession;
	private ImageReader mImageReader;
	
	private int mCurrentCameraMode=CAMERA_IMAGE;
	
	private boolean mIsRecordingVideo = false;
	
	private File mVideFile;
	
	 /**
     * The {@link android.util.Size} of video recording.
     */
    private Size mVideoSize;
	
	/**
     * MediaRecorder
     */
    private MediaRecorder mMediaRecorder;
	
	private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 270);// orinal: 90
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 90);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
	
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	try {
        		if(msg.what == CAMERA_IMAGE){
	        		mCurrentCameraMode = msg.what;
	                Toast.makeText(WhosThatService.this, "Starting photo service "+msg.what, Toast.LENGTH_LONG).show();
	                openCamera();
        		}
        		else if(msg.what == CAMERA_VIDEO){
        			mCurrentCameraMode = msg.what;
	                Toast.makeText(WhosThatService.this, "Starting video service "+msg.what, Toast.LENGTH_LONG).show();
	                openCamera();
        		}
        		else if(msg.what == CAMERA_STOP_RECORDING){
        			stopRecordingVideo();
        		}
            } catch (Exception e) {
                // Log, don't crash!
                Log.e(TAG, "Exception in AntiTheftWorkerHandler.handleMessage:", e);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return new Messenger(mHandler).getBinder();
    }
    
    
    
    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
    	 int mode = intent.getIntExtra(SERVICE_PARAM, 0);
    	 Message msg=new Message();
	     msg.what=mode;
	     mHandler.sendMessage(msg);
    	 
    	 return Service.START_NOT_STICKY;
	}



	/**
     *  Return the Camera Id which matches the field CAMERA.
     */
    public String getCamera(CameraManager manager){
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == FRONT_CAMERA) {
                    return cameraId;
                }
            }
        } catch (CameraAccessException e){
            e.printStackTrace();
        }
        return null;
    }
    
    
    protected void takePicture() {
		Log.e(TAG, "takePicture");
		if(null == mCameraDevice) {
			Log.e(TAG, "mCameraDevice is null, return");
			return;
		}
		
		CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		try {
			CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraDevice.getId());
			
			Size[] jpegSizes = null;
			if (characteristics != null) {
	            jpegSizes = characteristics
	                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
	                    .getOutputSizes(ImageFormat.JPEG);
	        }
			int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            
            mImageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            
            // Orientation
            WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE); 
            int rotation = window.getDefaultDisplay().getRotation();
            Log.i(TAG, "Orientation: "+rotation);
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            final File file = new File(Environment.getExternalStorageDirectory()+"/DCIM", "pic"+System.currentTimeMillis()+".jpg");

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {

				@Override
				public void onImageAvailable(ImageReader reader) {

					Image image = null;
					try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        ParseHelper.initializeFileParseObject(DeviceInfo.getIMEI(getApplicationContext()), bytes, file.getName()).saveInBackground();
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
				}

				private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            	
            };
            
            HandlerThread thread = new HandlerThread("CameraPicture");
            thread.start();
            final Handler backgroudHandler = new Handler(thread.getLooper());
            mImageReader.setOnImageAvailableListener(readerListener, backgroudHandler);
            
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {

				@Override
				public void onCaptureCompleted(CameraCaptureSession session,
						CaptureRequest request, TotalCaptureResult result) {

					super.onCaptureCompleted(session, request, result);
					Toast.makeText(WhosThatService.this, "Saved:"+file, Toast.LENGTH_SHORT).show();
					closeCamera();  
					stopSelf();
				}
            	
            };
            
            mCameraDevice.createCaptureSession(Arrays.asList(mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
				
				@Override
				public void onConfigured(CameraCaptureSession session) {

					try {
						session.capture(captureBuilder.build(), captureListener, backgroudHandler);
					} catch (CameraAccessException e) {

						e.printStackTrace();
					}
				}
				
				@Override
				public void onConfigureFailed(CameraCaptureSession session) {
					
				}
			}, backgroudHandler);
            
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}

	}
    
    protected void takeVideo() {
		Log.e(TAG, "takeVideo");
		if(null == mCameraDevice) {
			Log.e(TAG, "mCameraDevice is null, return");
			return;
		}
		try {
			
			setUpMediaRecorder();
			
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            captureBuilder.addTarget(mMediaRecorder.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            
            HandlerThread thread = new HandlerThread("CameraVideo");
            thread.start();
            final Handler backgroudHandler = new Handler(thread.getLooper());
            
            mCameraDevice.createCaptureSession(Arrays.asList(mMediaRecorder.getSurface()), new CameraCaptureSession.StateCallback() {
				
				@Override
				public void onConfigured(CameraCaptureSession session) {
					mPreviewSession = session;
					HandlerThread thread = new HandlerThread("CameraPreview");
		            thread.start();
		            try {
						mPreviewSession.setRepeatingRequest(captureBuilder.build(), null, backgroudHandler);
					} catch (CameraAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        startRecordingVideo();
			        Message msg=new Message();
			        msg.what=CAMERA_STOP_RECORDING;
			        mHandler.sendMessageDelayed(msg, CAMERA_VIDEO_LENGHT);
				}
				
				@Override
				public void onConfigureFailed(CameraCaptureSession session) {
					
				}
			}, backgroudHandler);
            
		} catch (CameraAccessException e) {
			e.printStackTrace();
		} catch (IOException ioe){
			ioe.printStackTrace();
		}

	}
    
    private void startRecordingVideo() {
        try {
            mIsRecordingVideo = true;
            // Start recording
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    
    private void stopRecordingVideo() {
        // UI
        mIsRecordingVideo = false;
        try {
			mPreviewSession.abortCaptures();
		} catch (CameraAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Stop recording
        try{
        	mMediaRecorder.stop();
        }
        catch(RuntimeException re){
        	re.printStackTrace();
        }
        mMediaRecorder.reset();
        Toast.makeText(getApplicationContext(), "Video saved",
                    Toast.LENGTH_SHORT).show();
        
        byte[] bFile = new byte[(int) mVideFile.length()];
        try {
            //convert file into array of bytes
        	FileInputStream fileInputStream = new FileInputStream(mVideFile);
        	fileInputStream.read(bFile);
        	fileInputStream.close();
        	ParseHelper.initializeFileParseObject(DeviceInfo.getIMEI(getApplicationContext()), bFile, mVideFile.getName()).saveInBackground();
        }
        catch(Exception e){
        	
        }
        closeCamera();
        stopSelf();
    }
    
    private void closeCamera() {
        try {
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } 
    }
    
    private void setUpMediaRecorder() throws IOException {
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mVideFile = new File(Environment.getExternalStorageDirectory()+"/DCIM", "video"+System.currentTimeMillis()+".mp4");
        mMediaRecorder.setOutputFile(mVideFile.getAbsolutePath());
        mMediaRecorder.setVideoEncodingBitRate(10000000);
        mMediaRecorder.setVideoFrameRate(30);//30
        mMediaRecorder.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public void onError(MediaRecorder mr, int what, int extra) {
				Log.i(TAG, "MediaRecorderError: "+what+", "+extra);
			}
		});
        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE); 
        int rotation = window.getDefaultDisplay().getRotation();
        int orientation = ORIENTATIONS.get(rotation);
        mMediaRecorder.setOrientationHint(orientation);
        mMediaRecorder.prepare();
    }
    
    private void openCamera() {
		
		CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
		Log.e(TAG, "openCamera E");
		try {
			String cameraId = getCamera(manager);
			if(mCurrentCameraMode == CAMERA_VIDEO){
				mMediaRecorder = new MediaRecorder();
				CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
		        StreamConfigurationMap map = characteristics
		                .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
		        mVideoSize = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
			}
			manager.openCamera(cameraId, mStateCallback, null);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
		Log.e(TAG, "openCamera X");
	}
    
    /**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes larger
     * than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }
	
    
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

		@Override
		public void onOpened(CameraDevice camera) {

			Log.e(TAG, "onOpened");
			mCameraDevice = camera;
			if(mCurrentCameraMode == CAMERA_IMAGE){
				takePicture();
			}
			else{
				takeVideo();
			}
		}

		@Override
		public void onDisconnected(CameraDevice camera) {

			Log.e(TAG, "onDisconnected");
		}

		@Override
		public void onError(CameraDevice camera, int error) {

			Log.e(TAG, "onError: "+error);
		}
		
	};

    
   
   
}
