package com.cs407.journeydoodle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class activity_login extends AppCompatActivity {
    public void loginFunction(View view) {
        EditText username = (EditText) findViewById(R.id.username);
        String userName = username.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("lab5_milstone", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("username", userName).apply();
        goToActivity(username.getText().toString());
    }

    public void goToActivity(String s) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("message", s);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}