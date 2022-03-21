package com.example.projet_ter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ConfigLayout {

    private static final String TAG = "ProjetTER::ConfigLayout";

    private final View layout;
    private final int layoutInitialHeight;
    private final FloatingActionButton develop_button;
    private final List<AppCompatSeekBar> seekBars = new ArrayList<>();
    private final List<TextView> seekBarsText = new ArrayList<>();
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private final Switch autoModeSwitch;

    public ConfigLayout(View layout) {
        // Getting the layout
        this.layout = layout;
        // Getting the initial height
        this.layoutInitialHeight = layout.getLayoutParams().height;
        // Getting the develop button
        this.develop_button = layout.findViewById(R.id.DevelopButton);
        // bind a listener to the button
        this.develop_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N) // For ??
            @Override
            public void onClick(View view) {
                handle_develop_button_click();
            }
        });
        // Getting the seekBars
        this.seekBars.add(layout.findViewById(R.id.seekBar));
        this.seekBars.add(layout.findViewById(R.id.seekBar2));
        this.seekBars.add(layout.findViewById(R.id.seekBar3));
        this.seekBars.add(layout.findViewById(R.id.seekBar4));
        this.seekBars.add(layout.findViewById(R.id.seekBar5));
        this.seekBars.add(layout.findViewById(R.id.seekBar6));
        // Getting the seekBars text
        this.seekBarsText.add(layout.findViewById(R.id.seekBarText));
        this.seekBarsText.add(layout.findViewById(R.id.seekBarText2));
        this.seekBarsText.add(layout.findViewById(R.id.seekBarText3));
        this.seekBarsText.add(layout.findViewById(R.id.seekBarText4));
        this.seekBarsText.add(layout.findViewById(R.id.seekBarText5));
        this.seekBarsText.add(layout.findViewById(R.id.seekBarText6));
        // Getting the auto mode switch
        this.autoModeSwitch = (Switch) layout.findViewById(R.id.AutoModeSwitch);
        // Bind a listene to the switch
        this.autoModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N) // For ??
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handle_auto_switch_change(isChecked);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N) // For lambda function
    private void handle_develop_button_click() {
        // Getting the current param layout
        ConstraintLayout.LayoutParams layoutP = (ConstraintLayout.LayoutParams) layout.getLayoutParams();
        // Set the new value to the element on the screen
        if (layoutP.height == this.layoutInitialHeight) {
            layoutP.height = this.layoutInitialHeight * 5;
            this.develop_button.setRotation(0);
            this.seekBars.forEach(element -> {
                element.setVisibility(View.VISIBLE);
            });
            this.seekBarsText.forEach(element -> {
                element.setVisibility(View.VISIBLE);
            });
        } else {
            layoutP.height = this.layoutInitialHeight;
            this.develop_button.setRotation(180);
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

    @RequiresApi(api = Build.VERSION_CODES.N) // For lambda function
    private void handle_auto_switch_change(boolean isChecked) {
        if (isChecked) {
            this.autoModeSwitch.setTextColor(Color.parseColor("#2196F3"));
            this.seekBars.forEach(element -> {
                element.setVisibility(View.VISIBLE);
            });
            this.seekBarsText.forEach(element -> {
                element.setTextColor(Color.parseColor("#aaaaaa"));

            });
        } else {
            this.autoModeSwitch.setTextColor(Color.parseColor("#aaaaaa"));
            this.seekBars.forEach(element -> {
                element.setVisibility(View.VISIBLE);
            });
            this.seekBarsText.forEach(element -> {
                element.setTextColor(Color.parseColor("#2196F3"));
            });
        }
    }
}
