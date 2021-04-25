package com.trax.vmsapp.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;


import com.trax.vmsapp.R;

import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.FALSE;


public class ConfirmationDialog {
    public static void show(Context context, final String action,
                            final DialogConfirmationInterface listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ConfirmationDialog);

        DialogInterface.OnClickListener dialogClickListener =
                (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            listener.action(action, TRUE);

                            dialog.dismiss();

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            listener.action(action, FALSE);

                            dialog.dismiss();

                            break;
                    }
                };

        String message = "Are you sure";

        if (action.equals("Next")) {
            message += "?";
        }
        else if (action.equals("Permissions")) {
            message = "If you do not give Permissions, you cannot use this App. " +
                    "Do you wish to give the Permissions?";
        }
        else if (action.equals("Clear")) {
            message = "Are you sure you? All your data will be clear.";
        }
        else if (action.equals("Leave")) {
            message = "Are you sure you? If you leave this screen you won't be able to add more attachments.";
        }
        else {
            message += ", you wish to " + action + "?";
        }

        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public static void buildAlertMessageNoGps(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ConfirmationDialog);
        builder.setMessage("To Continue, turn on device location, which uses Google's location service")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

