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
                    gearButton.setColorFilter(Color.argb(255, 109,204,252));
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
                    brushButton.setColorFilter(Color.argb(255, 109,204,252));
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
                    filterButton.setColorFilter(Color.argb(255, 109,204,252));
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
                    detailButton.setColorFilter(Color.argb(255, 109,204,252));
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

        SeekBar nbClusterSeekBar = mContext.findViewById(R.id.ClusterNumberSeekBar);
        SeekBar attemptsSeekBar = mContext.findViewById(R.id.attemptsSeekBar);
        SeekBar thresholdSeekBar = mContext.findViewById(R.id.thresholdSeekBar);

        minAreaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.HSVTargetZoneFinder.setMin_area_contour(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                gearTab.setAlpha(0.4f);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                gearTab.setAlpha(1f);
            }
        });
        hSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.HSVTargetZoneFinder.setHue_value(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                brushTab.setAlpha(0.4f);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                brushTab.setAlpha(1f);
            }
        });
        sSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.HSVTargetZoneFinder.setSaturation_value(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                brushTab.setAlpha(0.4f);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                brushTab.setAlpha(1f);
            }
        });
        vSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.HSVTargetZoneFinder.setValue_value(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                brushTab.setAlpha(0.4f);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                brushTab.setAlpha(1f);
            }
        });

        nbClusterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.targetZoneMaterialsExtractor.setNumberOfClusters(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                gearTab.setAlpha(0.4f);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                gearTab.setAlpha(1f);
            }
        });
        attemptsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.targetZoneMaterialsExtractor.setNumberOfIterations(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                gearTab.setAlpha(0.4f);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                gearTab.setAlpha(1f);
            }
        });
        thresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.KmeansTargetZoneFinder.setThreshold(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                gearTab.setAlpha(0.4f);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                gearTab.setAlpha(1f);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void clearTabs() {
        gearButton.setColorFilter(Color.argb(255, 255, 255, 255));
        brushButton.setColorFilter(Color.argb(255, 255, 255, 255));
        filterButton.setColorFilter(Color.argb(255, 255, 255, 255));
        detailButton.setColorFilter(Color.argb(255, 255, 255, 255));
        gearTab.setVisibility(View.GONE);
        brushTab.setVisibility(View.GONE);
    }
}
