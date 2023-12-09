package com.cs407.journeydoodle;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
interface StringDataCallback {
    void onStringDataReceived(String data);
}
public class SaveDialog extends AppCompatDialogFragment {
    String userInput = "";
    private StringDataCallback stringDataCallback;
    public void setStringDataCallback(StringDataCallback callback) {
        this.stringDataCallback = callback;
    }
    private void passStringData(String data) {
        if(stringDataCallback != null) {
            stringDataCallback.onStringDataReceived(data);
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText taskEditText = new EditText(getContext().getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Save Route")
                .setMessage("Please Enter the Route Name")
                .setView(taskEditText)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        String userInput = taskEditText.getText().toString();
                        passStringData(userInput);
                        Log.i("INFO", "Printing user input from dialog: " + userInput);
                        Toast.makeText(getContext(),
                                "Successfully Saved Route", Toast.LENGTH_LONG).show();

                    }
                });

        return builder.create();
    }
}