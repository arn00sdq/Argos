package com.example.projet_ter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class AddButton implements View.OnClickListener {

    Button button;

    public AddButton(Button button) {
        this.button = button;
        this.button.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        Log.i("ICI", "Clicked");
        this.button.setText("suppr");
    }
}
