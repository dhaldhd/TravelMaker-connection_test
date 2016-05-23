package com.hardcopy.arduinocontroller;

/**
 * Created by yoon on 2016. 5. 17..
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import android.os.Handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;


public class CameraActivity extends Activity {
    Button mShutter;
    MyCameraSurface mSurface;
    String mRootPath;
    static final String PICFOLDER = "CameraTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);


        mSurface = (MyCameraSurface)findViewById(R.id.previewFrame);
        mSurface.setOnClickListener(new FrameLayout.OnClickListener() {
            public void onClick(View v) {
                mSurface.mCamera.autoFocus(mAutoFocus);
            }
        });

        mShutter = (Button)findViewById(R.id.button1);
        mShutter.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mSurface.mCamera.autoFocus(mAutoFocus);
            }
        });
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            //Do Something
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //Intent i = new Intent(xxx.this, yyy.class); // xxx가 현재 activity,
                //yyy가 이동할 activity
                //startActivity(i);
                //finish();
                mSurface.mCamera.autoFocus(mAutoFocus);

            }
        }, 1000); // 1000ms
        //저장할 공간 /mnt/sdcard/CameraTest 이렇게 폴더 안에 파일이 생성된다
        mRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/" + PICFOLDER;
        File fRoot = new File(mRootPath);
        if (fRoot.exists() == false) {
            if (fRoot.mkdir() == false) {
                Toast.makeText(this, "사진을 저장할 폴더가 없습니다.", 1).show();
                finish();
                return;
            }
            //finish();
            //return;
        }

        //mSurface.mCamera.autoFocus(mAutoFocus);
    }

    // 포커싱 성공하면 촬영 허가

    AutoFocusCallback mAutoFocus = new AutoFocusCallback() {

        public void onAutoFocus(boolean success, Camera camera) {

            mShutter.setEnabled(success);

            mSurface.mCamera.takePicture(null, null, mPicture);



        }

    };



    // 사진 저장.

    PictureCallback mPicture = new PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {



            //날짜로 파일 이름 만들기

            Calendar calendar = Calendar.getInstance();

            String FileName = String.format("SH%02d%02d%02d-%02d%02d%02d.jpg",

                    calendar.get(Calendar.YEAR) % 100, calendar.get(Calendar.MONTH)+1,

                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),

                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

            String path = mRootPath + "/" + FileName;





            File file = new File(path);

            try {

                FileOutputStream fos = new FileOutputStream(file);

                fos.write(data);

                fos.flush();

                fos.close();

            } catch (Exception e) {



                return;

            }



            //파일을 갤러리에 저장

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            Uri uri = Uri.parse("file://" + path);

            intent.setData(uri);

            sendBroadcast(intent);



            Toast.makeText(getApplicationContext(), "사진이 저장 되었습니다", 0).show();

            camera.startPreview();
            finish();
            return;

        }

    };
}

class MyCameraSurface extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder mHolder;

    Camera mCamera;



    public MyCameraSurface(Context context, AttributeSet attrs) {

        super(context, attrs);

        mHolder = getHolder();

        mHolder.addCallback(this);

        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }



// 표면 생성시 카메라 오픈하고 미리보기 설정

    public void surfaceCreated(SurfaceHolder holder) {

        mCamera = Camera.open();

        mCamera.setDisplayOrientation(90);

        try {

            mCamera.setPreviewDisplay(mHolder);

        } catch (IOException e) {

            mCamera.release();

            mCamera = null;

        }

    }



    // 표면 파괴시 카메라도 파괴한다.

    public void surfaceDestroyed(SurfaceHolder holder) {

        if (mCamera != null) {

            mCamera.stopPreview();

            mCamera.release();

            mCamera = null;

        }

    }



// 표면의 크기가 결정될 때 최적의 미리보기 크기를 구해 설정한다.

    public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {

        Camera.Parameters params = mCamera.getParameters();

        params.setPreviewSize(width, height);

        List<Camera.Size> cSize = mCamera.getParameters().getSupportedPreviewSizes();

        Camera.Size tmpSize = cSize.get(1);

        params.setPreviewSize(tmpSize.width, tmpSize.height);

        params.setPictureSize(tmpSize.width, tmpSize.height);

        params.setRotation(90);

       // mCamera.setParameters(params);

        mCamera.startPreview();

    }



}



