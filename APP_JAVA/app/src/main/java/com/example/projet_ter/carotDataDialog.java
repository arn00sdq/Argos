package com.example.projet_ter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class carotDataDialog extends Dialog implements View.OnClickListener {

    private String data;
    private Activity activity;

    public carotDataDialog(Context context, String data) {
        super(context);
        this.data = data;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_up_carot_data);
        TextView dataText = this.findViewById(R.id.dataText);
        dataText.setText(this.data);
        Button button = this.findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        //this.activity.finish();
        this.dismiss();
    }
}
