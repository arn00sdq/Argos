package com.frontend;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.graphics.Color;

import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


public class Tabs {

    private static final String TAG = "ProjetTER::Tabs";

    private static final int TAB_NONE = 0;
    private static final int TAB_GEAR = 1;
    private static final int TAB_BRUSH = 2;
    private static final int TAB_FILTER = 3;
    private static final int TAB_DETAILS = 4;

    private static final boolean FILTER_ENABLE = true;
    private static final boolean FILTER_DISABLE = false;

    private final Activity mContext;
    private final View mTabLayout;
    private final CameraListener mCameraListener;
    private final CameraStateLayout mCameraStateLayout;

    private final ImageButton gearButton;
    private final ImageButton brushButton;
    private final ImageButton filterButton;
    private final ImageButton detailButton;
    private final ImageButton storeButton;

    private final View gearTab;
    private final View brushTab;
    private final View filterTab;

    private final View argileFilterView;
    private final View sableFilterView;
    private final View conglomeraFilterView;
    private final View unknownFilterView;

    private final SeekBar hSeekBar;
    private final SeekBar sSeekBar;
    private final SeekBar vSeekBar;

    private final SeekBar minAreaSeekBar;
    private final SeekBar nbClusterSeekBar;
    private final SeekBar attemptsSeekBar;
    private final SeekBar thresholdSeekBar;
    private final SeekBar accuracySeekBar;
    private final SeekBar divisionSizeSeekBar;

    private final Switch switchMaskMode;

    private boolean argileFilter = FILTER_ENABLE;
    private boolean sableFilter = FILTER_ENABLE;
    private boolean conglomeraFilter = FILTER_ENABLE;
    private boolean unknownFilter = FILTER_DISABLE;

    private int mCurrentTab = TAB_NONE;

    public Tabs(Activity context, View layout, CameraListener cm, CameraStateLayout csl) {
        mContext = context;
        mTabLayout = layout;
        mCameraListener = cm;
        mCameraStateLayout = csl;
        // Getting tabs buttons
        gearButton = mTabLayout.findViewById(R.id.gearButton);
        brushButton = mTabLayout.findViewById(R.id.brushButton);
        filterButton = mTabLayout.findViewById(R.id.filterButton);
        detailButton = mTabLayout.findViewById(R.id.detailButton);
        storeButton = mContext.findViewById(R.id.takePictureButton);
        // Getting tabs layout
        gearTab = mContext.findViewById(R.id.configLayout);
        brushTab = mContext.findViewById(R.id.colorLayout);
        filterTab = mContext.findViewById(R.id.filterLayout);

        // Getting config tab seekBar
        hSeekBar = mContext.findViewById(R.id.HSeekBar);
        sSeekBar = mContext.findViewById(R.id.SSeekBar);
        vSeekBar = mContext.findViewById(R.id.VSeekBar);

        minAreaSeekBar = mContext.findViewById(R.id.minAreaSeekBar);
        nbClusterSeekBar = mContext.findViewById(R.id.ClusterNumberSeekBar);
        attemptsSeekBar = mContext.findViewById(R.id.attemptsSeekBar);
        thresholdSeekBar = mContext.findViewById(R.id.thresholdSeekBar);
        accuracySeekBar = mContext.findViewById(R.id.accuracySeekBar);
        divisionSizeSeekBar = mContext.findViewById(R.id.divisionSizeSeekBar);

        // Getting the filters
        argileFilterView = mContext.findViewById(R.id.filter1);
        sableFilterView = mContext.findViewById(R.id.filter2);
        conglomeraFilterView = mContext.findViewById(R.id.filter3);
        unknownFilterView = mContext.findViewById(R.id.filter4);

        switchMaskMode = mContext.findViewById(R.id.switch1);
        switchMaskMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                CameraListener.mFrameAnalyzer.HSVTargetZoneFinder.setMode(b? 1 : 0);
            }
        });

        initButtonListener();
        initColorTabListener();
        initConfigTabListener();
        initFilterListener();

    }

    private void initButtonListener() {
        // adding listener
        gearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentTab(TAB_GEAR);
            }
        });
        brushButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                setCurrentTab(TAB_BRUSH);
            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentTab(TAB_FILTER);
            }
        });
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentTab(TAB_DETAILS);
                if (mCurrentTab == TAB_DETAILS) {
                    mCameraListener.setCameraState(CameraListener.CAMERA_STATE_PREVIEW);
                } else {
                    mCameraListener.setCameraState(mCameraStateLayout.getCameraState());
                }
            }
        });
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraListener.setCameraState(CameraListener.CAMERA_STATE_PICTURE);
            }
        });
    }

    private void initColorTabListener() {
        hSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.HSVTargetZoneFinder.setHue_value(i);
                TextView textValue = mContext.findViewById(R.id.HValue);
                textValue.setText("(" + i + ")");
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
                TextView textValue = mContext.findViewById(R.id.SValue);
                textValue.setText("(" + i + ")");
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
                TextView textValue = mContext.findViewById(R.id.VValue);
                textValue.setText("(" + i + ")");
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
    }

    private void initConfigTabListener() {
        // Setting the seekBar listener
        minAreaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.HSVTargetZoneFinder.setMin_area_contour(i);
                TextView textValue = mContext.findViewById(R.id.minAreaValue);
                textValue.setText("(" + i + ")");
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

        nbClusterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.targetZoneMaterialsExtractor.setNumberOfClusters(i);
                TextView textValue = mContext.findViewById(R.id.ClusterNumberValue);
                textValue.setText("(" + i + ")");
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
                TextView textValue = mContext.findViewById(R.id.attemptsValue);
                textValue.setText("(" + i + ")");
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
                TextView textValue = mContext.findViewById(R.id.thresholdValue);
                textValue.setText("(" + i + ")");
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
        accuracySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.targetZoneMaterialsExtractor.setConfidence(i);
                TextView textValue = mContext.findViewById(R.id.accuracyValue);
                textValue.setText("(" + i + ")");
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
        divisionSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CameraListener.mFrameAnalyzer.targetZoneMaterialsExtractor.setLengthOfCut(i);
                TextView textValue = mContext.findViewById(R.id.divisionSizeValue);
                textValue.setText("(" + i + ")");
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

    private void initFilterListener() {
        // Setting filter listener
        argileFilterView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                argileFilter = !argileFilter;
                updateFiltersView();
                mCameraListener.setFilters(getFilters());
                return false;
            }
        });
        sableFilterView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                sableFilter = !sableFilter;
                updateFiltersView();
                mCameraListener.setFilters(getFilters());
                return false;
            }
        });
        conglomeraFilterView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                conglomeraFilter = !conglomeraFilter;
                updateFiltersView();
                mCameraListener.setFilters(getFilters());
                return false;
            }
        });
        unknownFilterView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                unknownFilter = !unknownFilter;
                updateFiltersView();
                mCameraListener.setFilters(getFilters());
                return false;
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    public void clearTabs() {
        gearButton.setColorFilter(Color.argb(255, 255, 255, 255));
        brushButton.setColorFilter(Color.argb(255, 255, 255, 255));
        filterButton.setColorFilter(Color.argb(255, 255, 255, 255));
        detailButton.setColorFilter(Color.argb(255, 255, 255, 255));
        gearTab.setVisibility(View.GONE);
        brushTab.setVisibility(View.GONE);
        filterTab.setVisibility(View.GONE);
        storeButton.setVisibility(View.GONE);
    }

    private void updateFiltersView() {
        argileFilterView.setBackgroundResource(argileFilter? R.drawable.card_bg : 0);
        sableFilterView.setBackgroundResource(sableFilter? R.drawable.card_bg : 0);
        conglomeraFilterView.setBackgroundResource(conglomeraFilter? R.drawable.card_bg : 0);
        unknownFilterView.setBackgroundResource(unknownFilter? R.drawable.card_bg : 0);
    }

    public boolean[] getFilters() {
        return new boolean[]{argileFilter, sableFilter, conglomeraFilter, unknownFilter};
    }

    private void setCurrentTab(int newTab) {
        clearTabs();
        if (mCurrentTab != newTab) {
            mCurrentTab = newTab;
            if (getTabButton() != null) {
                getTabButton().setColorFilter(Color.argb(255, 109,204,252));
            }
            if (getTabLayout() != null) {
                getTabLayout().setVisibility(View.VISIBLE);
            }
        } else {
            mCurrentTab = TAB_NONE;
        }
    }

    private ImageButton getTabButton() {
        switch(mCurrentTab) {
            case TAB_GEAR:
                return gearButton;
            case TAB_BRUSH:
                return brushButton;
            case TAB_FILTER:
                return filterButton;
            case TAB_DETAILS:
                return detailButton;
            default:
                return null;
        }
    }

    private View getTabLayout() {
        switch(mCurrentTab) {
            case TAB_GEAR:
                return gearTab;
            case TAB_BRUSH:
                return brushTab;
            case TAB_FILTER:
                return filterTab;
            case TAB_DETAILS:
                return storeButton;
            default:
                return null;
        }
    }
}
