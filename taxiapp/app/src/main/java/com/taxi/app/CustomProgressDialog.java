package com.taxi.app;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class CustomProgressDialog {
    Context context;
    AlertDialog dialog;
    public CustomProgressDialog(Context context, int layout){
        this.context = context;

        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout, null);
        builder.setView(view);
        dialog = builder.create();
    }

    public void show(){
        this.dialog.show();
    }

    public void dismiss(){
        this.dialog.dismiss();
    }
}