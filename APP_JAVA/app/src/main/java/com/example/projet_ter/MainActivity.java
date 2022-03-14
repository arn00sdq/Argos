package com.example.projet_ter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2  {

    private static final String TAG = "ProjetTER::MainActivity";

    private JavaCameraView java_camera_view;

    private Mat rgba_matrix;
    private Mat gray_matrix;

    private BaseLoaderCallback base_loader_callback = new BaseLoaderCallback(MainActivity.this) {

        @Override
        public void onManagerConnected(int status) {

            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "onManagerConnected");
                    java_camera_view.setCameraPermissionGranted();
                    java_camera_view.enableView();
                    Log.i(TAG, "cameraView enable");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
            super.onManagerConnected(status);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "onCreate");

        this.java_camera_view = (JavaCameraView) findViewById(R.id.camera_view);
        this.java_camera_view.setCvCameraViewListener(MainActivity.this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        Log.i(TAG, "onCameraViewStarted");
        this.rgba_matrix = new Mat();
        this.gray_matrix = new Mat();
    }

    @Override
    public void onCameraViewStopped() {

        Log.i(TAG, "onCameraViewStopped");
        this.rgba_matrix.release();
        this.gray_matrix.release();

    }

    /**
     * This function is calles each time a frame is send by the camera
     * @param inputFrame The frame
     * @return the matrix to display
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Log.i(TAG, "onFrame");

        Size original_size;

        this.rgba_matrix = inputFrame.rgba();
        this.gray_matrix = inputFrame.gray();

        original_size = this.rgba_matrix.size();

        Core.rotate(this.rgba_matrix, this.rgba_matrix, Core.ROTATE_90_CLOCKWISE);
        Imgproc.resize(this.rgba_matrix, this.rgba_matrix, original_size);

        /*
            Ajouter fonction analyse d'image ICI
         */
        return this.rgba_matrix;
    }

    /**
     * The camera is initially paused so the onResume function is called at start
     */
    @Override
    protected void onResume() {

        super.onResume();
        Log.i(TAG, "onResume");
        if (OpenCVLoader.initDebug()) {
            this.base_loader_callback.onManagerConnected(this.base_loader_callback.SUCCESS);
        }
        this.rgba_matrix = new Mat();
        this.gray_matrix = new Mat();
        Log.i(TAG, "Mat created");
    }

    public void onDestroy() {
        super.onDestroy();
        java_camera_view.disableView();
    }
}