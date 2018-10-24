package com.kukielko.tadhack.Activities;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.aambekar.tadhack.Data.Database;
import com.aambekar.tadhack.EndPoints.Image_API;
import com.aambekar.tadhack.R;

import java.io.ByteArrayOutputStream;

public class CameraView extends Activity implements SurfaceHolder.Callback, View.OnClickListener
{
    Camera mCamera;
    boolean mPreviewRunning = false;
    double x=0.0d,y=0.0d;
    String TAG="Camera";
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;



    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.cameraview);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.setKeepScreenOn(true);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onStop()
    {
        super.onStop();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        try
        {

            if(mCamera!=null)
            {
                Log.e(TAG, "surfaceChanged camera");
                if(mPreviewRunning) {
                    mCamera.stopPreview();
                }

                Camera.Parameters p = mCamera.getParameters();

                mCamera.setParameters(p);

                mCamera.startPreview();
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mPreviewRunning = true;
                if(checkpermission())
                {
                    mCamera.takePicture(null, null, mPictureCallback);
                }
            }
        }
        catch (Exception e)
        {

        }

    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.e(TAG, "surfaceDestroyed camera");
        stopCamera();
    }



    public void onClick(View v) {

        if(mCamera!=null)
        {
            mCamera.takePicture(null, mPictureCallback, mPictureCallback);
        }

    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d(TAG, "surfaceCreated: camera");

        if(checkpermission())
        {

            int i = findFrontFacingCamera();
            try
            {
                this.mCamera = Camera.open(i);
                this.mCamera.setPreviewDisplay(holder);
            }
            catch(Exception localRuntimeException)
            {
                localRuntimeException.printStackTrace();
                stopCamera();
                try
                {
                    this.mCamera = Camera.open(i);
                    this.mCamera.setPreviewDisplay(holder);

                }
                catch(Exception localIOException1)
                {
                    stopCamera();
                    localIOException1.printStackTrace();

                }

            }
        }
    }

    private void stopCamera()
    {
        try
        {
            if(this.mCamera != null)
            {
                this.mPreviewRunning = false;
//                mCamera.stopPreview();
//                mCamera.release();
            }
        }
        catch (Exception e)
        {

        }
    }
    private int findFrontFacingCamera()
    {
        try
        {
            int i = Camera.getNumberOfCameras();
            for(int j = 0 ; ; j++)
            {
                if(j >= i) return -1;
                Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(j, localCameraInfo);
                if(localCameraInfo.facing == 1) return j;
            }
        }
        catch (Exception e)
        {
            return 0;
        }

    }


    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            if(data != null) {

                mCamera.stopPreview();
                mPreviewRunning = false;
                mCamera.release();

                try {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                            data.length, opts);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 400, 300, false);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int newWidth = 400;
                    int newHeight = 300;
                    float scaleWidth = ((float) newWidth) / width;
                    float scaleHeight = ((float) newHeight) / height;
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    matrix.postRotate(-90);
                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);


                    String image=Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
                    Log.d(TAG, "onPictureTaken: Image is "+image);

                    Database database = new Database(getApplicationContext());

                    new Image_API(database.getContacts(),image,getApplicationContext()).execute();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                setResult(585);
                finish();
            }
        }
    };



    boolean checkpermission()
    {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            return  true;
        }
        else
        {
            return false;
        }

    }



}
