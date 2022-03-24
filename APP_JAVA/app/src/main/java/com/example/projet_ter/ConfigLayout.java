package com.example.projet_ter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.Layout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class ConfigLayout {

    private static final String TAG = "ProjetTER::ConfigLayout";

    private final View layout;

    private final List<AppCompatSeekBar> seekBars = new ArrayList<>();
    private final List<TextView> seekBarsText = new ArrayList<>();
    private final int layoutInitialHeight;

    public ConfigLayout(View layout) {
        // Getting the layout
        this.layout = layout;
        // Getting the initial height
        this.layoutInitialHeight = layout.getLayoutParams().height;
        // Getting the seekBars
        this.seekBars.add(layout.findViewById(R.id.seekBar));
        this.seekBars.add(layout.findViewById(R.id.seekBar2));
        this.seekBars.add(layout.findViewById(R.id.seekBar3));
        this.seekBars.add(layout.findViewById(R.id.seekBar4));
        // Getting the seekBars text
        this.seekBarsText.add(layout.findViewById(R.id.seekBarText));
        this.seekBarsText.add(layout.findViewById(R.id.seekBarText2));
        this.seekBarsText.add(layout.findViewById(R.id.seekBarText3));
        this.seekBarsText.add(layout.findViewById(R.id.seekBarText4));

    }

    @RequiresApi(api = Build.VERSION_CODES.N) // For lambda function
    public void setVisible(boolean visible) {
        // Getting the current param layout
        ConstraintLayout.LayoutParams layoutP = (ConstraintLayout.LayoutParams) layout.getLayoutParams();
        // Set the new value to the element on the screen
        if (visible) {
            this.layout.setVisibility(View.VISIBLE);
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    500,
                    layout.getContext().getResources().getDisplayMetrics());
            layoutP.height = (int) pixels;
            this.seekBars.forEach(element -> {
                element.setVisibility(View.VISIBLE);
            });
            this.seekBarsText.forEach(element -> {
                element.setVisibility(View.VISIBLE);
            });
        } else {
            this.layout.setVisibility(View.INVISIBLE);
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    1,
                    layout.getContext().getResources().getDisplayMetrics());
            layoutP.height = (int) pixels;
            this.seekBars.forEach(element -> {
                element.setVisibility(View.INVISIBLE);
            });
            this.seekBarsText.forEach(element -> {
                element.setVisibility(View.INVISIBLE);
            });
        }
        // Apply new param to the layout
        this.layout.setLayoutParams(layoutP);
    }


    @RequiresApi(api = Build.VERSION_CODES.O) // For lambda function and set min and max
    public void setManualMode(boolean isChecked) {
        int active_color = Color.parseColor("#2196F3");
        int disable_color = Color.parseColor("#aaaaaa");
        if (isChecked) {
            this.seekBars.forEach(element -> {
                element.setProgressBackgroundTintList(ColorStateList.valueOf(disable_color));
                element.setProgressTintList(ColorStateList.valueOf(disable_color));
                element.setThumbTintList(ColorStateList.valueOf(disable_color));
                element.setEnabled(false);
            });
            this.seekBarsText.forEach(element -> {
                element.setTextColor(disable_color);
            });
        } else {
            this.seekBars.forEach(element -> {
                element.setProgressBackgroundTintList(ColorStateList.valueOf(active_color));
                element.setProgressTintList(ColorStateList.valueOf(active_color));
                element.setThumbTintList(ColorStateList.valueOf(active_color));
                element.setEnabled(true);
            });
            this.seekBarsText.forEach(element -> {
                element.setTextColor(active_color);
            });
        }
    }

    public boolean is_visible() {
        return this.layout.getVisibility() == View.VISIBLE;
    }

}
