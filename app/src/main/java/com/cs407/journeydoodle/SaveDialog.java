package com.cs407.journeydoodle;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class SaveDialog extends AppCompatDialogFragment {
    String userInput;
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
                        userInput = taskEditText.getText().toString();
                        Log.i("INFO", "Printing user input from dialog: " + userInput);
                        Toast.makeText(getContext(),
                                "Successfully Saved Route", Toast.LENGTH_LONG).show();

                    }
                });

        return builder.create();
    }
    public String getRouteName() {
        return userInput;
    }
}