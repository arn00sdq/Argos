package com.example.projet_ter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final int REGUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final String TAG = "ProjetTER::MainActivity";

    private CameraListener camera_component;
    private ConfigLayout configLayout;
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

        Log.i(TAG, "onCreate");
        //this.camera_component = new CameraListener(this, this.findViewById(R.id.textureView));
        this.camera_component = new CameraListener(this.findViewById(R.id.javaCamera2View));
        this.configLayout = new ConfigLayout((View) this.findViewById(R.id.ButtonLayout), this.camera_component.getFrameAnalyzer());
        this.mTabs = new Tabs(this, this.findViewById(R.id.tabLayout));
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
        /*try {
            this.camera_component.closeCamera();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        this.camera_component.disable();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        /*try {
            this.camera_component.closeCamera();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

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
                    if(dx>0) {
                        mCameraStateLayout.nextState();
                    } else {
                        mCameraStateLayout.previouslyState();
                    }
                    camera_component.setCameraState(mCameraStateLayout.getCameraState());
                } else {
                    if (dy > 0) {
                        this.configLayout.setVisible(false);
                        Log.i(TAG, "down");
                    } else {
                        this.configLayout.setVisible(true);
                        Log.i(TAG, "up");
                    }
                }
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
