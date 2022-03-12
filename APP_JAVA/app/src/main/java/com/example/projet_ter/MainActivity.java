package com.example.projet_ter;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2  {

    FrameLayout frame_layout;
    JavaCameraView java_camera_view;
    Mat mRGBA, mRGBAT;

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d("MainActivity", "OpenCV is Configured or Connected Successfuly.");
        } else {
            Log.d("MainActivity", "OpenCV is not Working or Loaded");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.java_camera_view = (JavaCameraView) findViewById(R.id.camera_view);
        java_camera_view.enableView();
        this.java_camera_view.setCvCameraViewListener(MainActivity.this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        this.mRGBA = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        this.mRGBA.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        this.mRGBA = inputFrame.rgba();
        this.mRGBAT = this.mRGBA.t();
        Core.flip(this.mRGBA.t(), this.mRGBAT, 1);
        Imgproc.resize(this.mRGBAT, this.mRGBAT, this.mRGBA.size());
        return this.mRGBAT;
    }

}