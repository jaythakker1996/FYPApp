package com.example.jaythakker.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;

/**
 * Created by Kaushal on 26-03-2018.
 */

public class ViewDialog {

    public void showDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.rating_dialog);

        /*TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/

        dialog.show();

    }
}

