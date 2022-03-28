package com.example.projet_ter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class carotDataDialog extends DialogFragment {

    private String data;

    public carotDataDialog(String Data) {
        super();
        this.data = data;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setView(inflater.inflate(R.layout.pop_up_carot_data))
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
