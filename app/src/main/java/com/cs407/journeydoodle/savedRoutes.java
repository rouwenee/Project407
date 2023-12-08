package com.cs407.journeydoodle;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class savedRoutes extends AppCompatActivity {

    public static ArrayList<Route> routes = new ArrayList<>();
    public void exit(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_routes);
        getSupportActionBar().setTitle("Saved Routes");

        Context context = getApplicationContext();
        SQLiteDatabase sq = openOrCreateDatabase("routes", Context.MODE_PRIVATE, null);
        DBHelper db = new DBHelper(sq);

        routes = db.readRoute("");

        ArrayList<String> displayRoutes = new ArrayList<>();

        for (Route routes : routes) {
            displayRoutes.add(String.format("Title:%s\nDate:%s\n", routes.getTitle(), routes.getDate()));
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, displayRoutes);
        ListView notesListView = (ListView) findViewById(R.id.listView);
        notesListView.setAdapter(adapter);

        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("noteId", i);
                startActivity(intent);
            }
        });
    }
    /*public void addRoute() {

    }*/
}
