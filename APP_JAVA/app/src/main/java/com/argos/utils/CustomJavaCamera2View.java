package com.argos.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.core.Mat;

public class temp extends JavaCamera2View implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "ProjetTER::JavaCamera2View";

    @SuppressLint("LongLogTag")
    public temp(Context context) {
        super(context, CameraBridgeViewBase.CAMERA_ID_BACK);
        /*int i = 0;
        try {
            CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            // Getting the Camera list
            // The camera are represented by a string ID
            String[] cameraIds = new String[0];
                cameraIds = cameraManager.getCameraIdList();
            // Getting the back camera
            // Iterates the cameras until we get the id of the back camera
            if (cameraIds.length == 0) {
                Log.d(TAG, "Error : camera isn't detected.");
            }
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraIds[i]);
            // Optie possible mais je comprend pas
            while (i < cameraIds.length && cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                i++;
                cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraIds[i]);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }/*/
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return null;
    }

    private void getCameraId() {

    }
}
