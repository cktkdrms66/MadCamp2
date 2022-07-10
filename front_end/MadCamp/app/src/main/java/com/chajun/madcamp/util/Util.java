package com.chajun.madcamp.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.chajun.madcamp.R;
import com.chajun.madcamp.ui.main.AddRoomActivity;

import org.json.JSONArray;

public class Util {

    public static void makeDialog(Context context, int stringId) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(stringId);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        }

    }

    public static void makeDialog(Context context, int stringId, DialogInterface.OnClickListener listener) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(stringId);
            builder.setPositiveButton(R.string.confirm, listener);
            builder.create().show();
        }

    }

    public static String getTurnPerTotal(int currentTurn, int totalTurn) {
        return currentTurn + " / " + totalTurn;
    }

    public static int[] JSonArray2IntArray(JSONArray jsonArray){
        int[] intArray = new int[jsonArray.length()];
        for (int i = 0; i < intArray.length; ++i) {
            intArray[i] = jsonArray.optInt(i);
        }
        return intArray;
    }
}
