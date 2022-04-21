package com.example.projet_ter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;

import android.view.MotionEvent;

import android.view.View;
import android.view.WindowManager;

import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final int REGUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final String TAG = "ProjetTER::MainActivity";

    private CameraListener camera_component;
    private CameraStateLayout mCameraStateLayout;
    private Tabs mTabs;
    private float x1 = 0;
    private float y1 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Log.i(TAG, "onCreate");
        this.camera_component = new CameraListener(this);
        this.mTabs = new Tabs(this, this.findViewById(R.id.tabLayout), camera_component);
        this.mCameraStateLayout = new CameraStateLayout(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    camera_component.enable();
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "This Application can not run without camera services.", Toast.LENGTH_SHORT).show();
                    }
                    this.requestPermissions(new String[] {Manifest.permission.CAMERA}, MainActivity.REGUEST_CAMERA_PERMISSION_RESULT);
                }
            } else {
                this.camera_component.enable();
            }
        }
    }

    @Override
    public void onDestroy() {
        this.camera_component.disable();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        this.camera_component.disable();
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = event.getAction();
        float x2 = event.getX();
        float y2 = event.getY();
        float dx = x2-x1;
        float dy = y2-y1;
        Log.d(TAG, "Tpouch");

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(TAG,"Action was DOWN");
                x1 = event.getX();
                y1 = event.getY();
                mTabs.clearTabs();
                return true;

            case (MotionEvent.ACTION_UP) :
                Log.d(TAG,"Action was UP");
                // Use dx and dy to determine the direction of the move
                if(Math.abs(dx) > Math.abs(dy)) {
                    if(dx>0) {
                        mCameraStateLayout.nextState();
                    } else {
                        mCameraStateLayout.previouslyState();
                    }
                } else {
                    if (dy > 0) {
                        Log.i(TAG, "down");
                    } else {
                        Log.i(TAG, "up");
                    }
                }
                camera_component.setFilters(mTabs.getFilters());
                camera_component.setCameraState(mCameraStateLayout.getCameraState());
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REGUEST_CAMERA_PERMISSION_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this.getApplicationContext(), "This Application can not run without camera services.", Toast.LENGTH_SHORT).show();
            } else {
                onResume();
            }
        }
    }

}
