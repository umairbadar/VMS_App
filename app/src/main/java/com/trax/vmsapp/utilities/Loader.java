package com.trax.vmsapp.utilities;

import android.app.ProgressDialog;
import android.content.Context;

import com.trax.vmsapp.R;

public class Loader {
    public static ProgressDialog show(Context context) {
        ProgressDialog dialog = new ProgressDialog(context, R.style.Loader);

        dialog.setMessage("Please wait...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);

        dialog.show();

        return dialog;
    }
}
