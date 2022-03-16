package com.example.projet_ter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ProjetTER::MainActivity";

    private CameraListener camera_component;


    private final BaseLoaderCallback base_loader_callback = new BaseLoaderCallback(MainActivity.this) {
        @Override
        public void onManagerConnected(int status) {

            if (status == BaseLoaderCallback.SUCCESS) {
                camera_component.enable();
            } else {
                super.onManagerConnected(status);
            }
            super.onManagerConnected(status);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "onCreate");
        this.camera_component = new CameraListener( (JavaCameraView) findViewById(R.id.camera_view) );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            this.base_loader_callback.onManagerConnected(this.base_loader_callback.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.camera_component.disable();
    }

}
