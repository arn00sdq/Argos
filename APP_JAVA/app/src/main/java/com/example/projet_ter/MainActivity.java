package com.example.projet_ter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TableLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ProjetTER::MainActivity";

    private CameraListener camera_component;
    private ConfigLayout configLayout;
    private float x1 = 0;
    private float y1 = 0;

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 100);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        Objects.requireNonNull(this.getSupportActionBar()).hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.i(TAG, "onCreate");
        this.camera_component = new CameraListener((JavaCameraView) findViewById(R.id.camera_view));
        this.configLayout = new ConfigLayout((View) findViewById(R.id.ButtonLayout));

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = event.getAction();
        float x2 = event.getX();
        float y2 = event.getY();
        float dx = x2-x1;
        float dy = y2-y1;

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(TAG,"Action was DOWN");
                x1 = event.getX();
                y1 = event.getY();
                return true;

            case (MotionEvent.ACTION_UP) :
                Log.d(TAG,"Action was UP");
                // Use dx and dy to determine the direction of the move
                if(Math.abs(dx) > Math.abs(dy)) {
                    if(dx>0)
                        Log.i(TAG, "right");
                    else
                        Log.i(TAG, "left");
                } else {
                    if (dy > 0) {
                        this.configLayout.setVisible(false);
                        Log.i(TAG, "down");
                    } else {
                        this.configLayout.setVisible(true);
                        Log.i(TAG, "up");
                    }
                }
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }
}
