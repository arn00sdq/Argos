package com.example.projet_ter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CameraStateLayout {

    private static final String TAG = "ProjectTER::CameraStateLayout";

    private final Activity mContext;
    private final View mLayout;

    private int mCameraState = CameraListener.CAMERA_STATE_PREVIEW;

    private final List<TextView> mStatesViews = new ArrayList<>();
    private View.OnClickListener mStatesViewsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mCameraState = mStatesViews.indexOf(view);
            updateView();
        }
    };

    public CameraStateLayout(Activity context) {
        mContext = context;
        mLayout = mContext.findViewById(R.id.camera_state_layout);
        mStatesViews.add(CameraListener.CAMERA_STATE_PREVIEW, mContext.findViewById(R.id.state0));
        mStatesViews.add(CameraListener.CAMERA_STATE_ANALYSE, mContext.findViewById(R.id.state1));
        mStatesViews.add(CameraListener.CAMERA_STATE_MASK, mContext.findViewById(R.id.state2));
        mStatesViews.add(CameraListener.CAMERA_STATE_COLOR, mContext.findViewById(R.id.state3));
        for (int i = 0; i < mStatesViews.size(); i++) {
            mStatesViews.get(i).setOnClickListener(mStatesViewsListener);
        }
    }

    public void previouslyState() {
        if (mCameraState < CameraListener.CAMERA_STATE_COLOR) {
            mCameraState++;
            updateView();
        }

    }
    public void nextState() {
        if (mCameraState > CameraListener.CAMERA_STATE_PREVIEW) {
            mCameraState--;
            updateView();
        }
    }

    private void updateView() {
        for (int i = 0; i < mCameraState; i++) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mStatesViews.get(i).getLayoutParams();
            layoutParams.addRule(RelativeLayout.START_OF, mStatesViews.get(i+1).getId());
            layoutParams.removeRule(RelativeLayout.END_OF);
            layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
            mStatesViews.get(i).setLayoutParams(layoutParams);
            mStatesViews.get(i).setTextColor(Color.argb(255, 170,170,170));
        }
        for (int i = mCameraState + 1; i < mStatesViews.size(); i++) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mStatesViews.get(i).getLayoutParams();
            layoutParams.addRule(RelativeLayout.END_OF, mStatesViews.get(i-1).getId());
            layoutParams.removeRule(RelativeLayout.START_OF);
            layoutParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
            mStatesViews.get(i).setLayoutParams(layoutParams);
            mStatesViews.get(i).setTextColor(Color.argb(255, 170,170,170));
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mStatesViews.get(mCameraState).getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layoutParams.removeRule(RelativeLayout.END_OF);
        layoutParams.removeRule(RelativeLayout.START_OF);
        mStatesViews.get(mCameraState).setLayoutParams(layoutParams);
        mStatesViews.get(mCameraState).setTextColor(Color.argb(255, 109,204,252));
    }

    public int getCameraState() {
        return mCameraState;
    }

}
