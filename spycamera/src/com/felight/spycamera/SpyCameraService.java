package com.felight.spycamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SpyCameraService extends Service {
	
	 protected static final int  MEDIA_TYPE_IMAGE = 1;
	 Camera mCamera;
	 private CameraPreview mPreview;
	 PictureCallback mPicture;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		
		/*Intent i = new Intent();
		i.setClass(this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);*/
		
		mCamera = getCameraInstance();
		mPreview = new CameraPreview(this, mCamera);
		
		mPicture = new PictureCallback() {

    	    @Override
    	    public void onPictureTaken(byte[] data, Camera camera) {

    	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
    	        if (pictureFile == null){
    	      
    	            return;
    	        }

    	        try {
    	            FileOutputStream fos = new FileOutputStream(pictureFile);
    	            fos.write(data);
    	            fos.close();
    	        } catch (FileNotFoundException e) {
    	            //Log.d(TAG, "File not found: " + e.getMessage());
    	        } catch (IOException e) {
    	            //Log.d(TAG, "Error accessing file: " + e.getMessage());
    	        }
    	    }
    	};
    	
    	
    	
    	
	}
	
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
	    private SurfaceHolder mHolder;
	    private Camera mCamera;

	    public CameraPreview(Context context, Camera camera) {
	        super(context);
	        mCamera = camera;

	        // Install a SurfaceHolder.Callback so we get notified when the
	        // underlying surface is created and destroyed.
	        mHolder = getHolder();
	        mHolder.addCallback(this);
	        // deprecated setting, but required on Android versions prior to 3.0
	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    }

	    public void surfaceCreated(SurfaceHolder holder) {
	        // The Surface has been created, now tell the camera where to draw the preview.
	        try {
	            mCamera.setPreviewDisplay(holder);
	            mCamera.startPreview();
	        } catch (IOException e) {
	            //Log.d(TAG, "Error setting camera preview: " + e.getMessage());
	        }
	    }

	    public void surfaceDestroyed(SurfaceHolder holder) {
	        // empty. Take care of releasing the Camera preview in your activity.
	    }

	    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	        // If your preview can change or rotate, take care of those events here.
	        // Make sure to stop the preview before resizing or reformatting it.

	        if (mHolder.getSurface() == null){
	          // preview surface does not exist
	          return;
	        }

	        // stop preview before making changes
	        try {
	            mCamera.stopPreview();
	        } catch (Exception e){
	          // ignore: tried to stop a non-existent preview
	        }

	        // set preview size and make any resize, rotate or
	        // reformatting changes here

	        // start preview with new settings
	        try {
	            mCamera.setPreviewDisplay(mHolder);
	            mCamera.startPreview();

	        } catch (Exception e){
	            //Log.d(TAG, "Error starting camera preview: " + e.getMessage());
	        }
	    }
	}
	
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("Service started");
		mCamera.takePicture(null, null, mPicture);
		
		return super.onStartCommand(intent, flags, startId);
	}

	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		System.out.println("service end");
	}
	
	
	private File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}
	
	
}
