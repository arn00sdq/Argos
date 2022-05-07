package com.backend;

import android.annotation.SuppressLint;
import android.content.Context;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;

public class CustomJavaCamera2View extends JavaCamera2View {

    private static final String TAG = "ProjetTER::JavaCamera2View";

    @SuppressLint("LongLogTag")
    public CustomJavaCamera2View(Context context) {
        super(context, CameraBridgeViewBase.CAMERA_ID_BACK);
    }



}
