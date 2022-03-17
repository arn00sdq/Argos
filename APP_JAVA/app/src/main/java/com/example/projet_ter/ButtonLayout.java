package com.example.projet_ter;

import android.text.Layout;
import android.view.View;
import android.widget.Button;

public class ButtonLayout {

    View layout;
    AddButton addButton;

    public ButtonLayout( View layout ) {
        this.layout = layout;
        this.addButton = new AddButton((Button) layout.findViewById(R.id.AddButton));
    }



}
