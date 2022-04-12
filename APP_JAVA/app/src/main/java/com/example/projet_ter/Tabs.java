package com.example.projet_ter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;

import com.argos.utils.FrameAnalyzer;

public class Tabs {

    private static final String TAG = "ProjetTER::Tabs";

    private static final int TAB_NONE = 0;
    private static final int TAB_GEAR = 1;
    private static final int TAB_BRUSH = 2;
    private static final int TAB_FILTER = 3;
    private static final int TAB_DETAILS = 4;

    private final Activity mContext;
    private final View mTabLayout;

    private final ImageButton gearButton;
    private final ImageButton brushButton;
    private final ImageButton filterButton;
    private final ImageButton detailButton;

    private final View gearTab;
    private final View brushTab;
    /*private final ImageButton filterTab;
    private final ImageButton detailtab;*/

    private int mCurrentTab = TAB_NONE;

    public Tabs(Activity context, View layout) {
        mContext = context;
        mTabLayout = layout;
        // Getting tabs buttons
        gearButton = mTabLayout.findViewById(R.id.gearButton);
        brushButton = mTabLayout.findViewById(R.id.brushButton);
        filterButton = mTabLayout.findViewById(R.id.filterButton);
        detailButton = mTabLayout.findViewById(R.id.detailButton);
        // Getting tabs layout
        gearTab = mContext.findViewById(R.id.configLayout);
        brushTab = mContext.findViewById(R.id.colorLayout);
        // adding listener
        gearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTabs();
                if (mCurrentTab != TAB_GEAR) {
                    mCurrentTab = TAB_GEAR;
                    gearButton.setColorFilter(Color.argb(255, 0, 103, 247));
                    gearTab.setVisibility(View.VISIBLE);
                } else {
                    mCurrentTab = TAB_NONE;
                }
            }
        });
        brushButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                clearTabs();
                if (mCurrentTab != TAB_BRUSH) {
                    mCurrentTab = TAB_BRUSH;
                    brushButton.setColorFilter(Color.argb(255, 0, 103, 247));
                    brushTab.setVisibility(View.VISIBLE);
                } else {
                    mCurrentTab = TAB_NONE;
                }
            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTabs();
                if (mCurrentTab != TAB_FILTER) {
                    mCurrentTab = TAB_FILTER;
                    filterButton.setColorFilter(Color.argb(255, 0, 103, 247));
                } else {
                    mCurrentTab = TAB_NONE;
                }
            }
        });
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTabs();
                if (mCurrentTab != TAB_DETAILS) {
                    mCurrentTab = TAB_DETAILS;
                    detailButton.setColorFilter(Color.argb(255, 0, 103, 247));
                } else {
                    mCurrentTab = TAB_NONE;
                }
            }
        });

        // Getting config tab seekBar
        SeekBar minAreaSeekBar = mContext.findViewById(R.id.minAreaSeekBar);
        SeekBar hSeekBar = mContext.findViewById(R.id.HSeekBar);
        SeekBar sSeekBar = mContext.findViewById(R.id.SSeekBar);
        SeekBar vSeekBar = mContext.findViewById(R.id.VSeekBar);

        SeekBar minAreaSeekBar2 = mContext.findViewById(R.id.minAreaSeekBar2);
        SeekBar nbClusterSeekBar = mContext.findViewById(R.id.ClusterNumberSeekBar);
        SeekBar attemptsSeekBar = mContext.findViewById(R.id.attemptsSeekBar);
        SeekBar thresholdSeekBar = mContext.findViewById(R.id.thresholdSeekBar);

        minAreaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.HSVTargetZoneFinder.setMin_area_contour(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        hSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.HSVTargetZoneFinder.setHue_value(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        sSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.HSVTargetZoneFinder.setSaturation_value(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        vSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.HSVTargetZoneFinder.setValue_value(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        minAreaSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.KmeansTargetZoneFinder.setMin_area_contour(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        nbClusterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.KmeansTargetZoneFinder.setClustersNumber(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        attemptsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.KmeansTargetZoneFinder.setAttemptNumber(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        thresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.KmeansTargetZoneFinder.setThreshold(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @SuppressLint("ResourceAsColor")
    private void clearTabs() {
        gearButton.setColorFilter(Color.argb(255, 170, 170, 170));
        brushButton.setColorFilter(Color.argb(255, 170, 170, 170));
        filterButton.setColorFilter(Color.argb(255, 170, 170, 170));
        detailButton.setColorFilter(Color.argb(255, 170, 170, 170));
        gearTab.setVisibility(View.GONE);
        brushTab.setVisibility(View.GONE);
    }
}
