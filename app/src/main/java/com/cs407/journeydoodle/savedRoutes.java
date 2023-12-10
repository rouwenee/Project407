package com.cs407.journeydoodle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

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
        routes = db.readRoute(new Installation().id(context));

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

        notesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDeleteConfirmationDialog(i, adapter);
                return true;
            }
        });
    }
    private void showDeleteConfirmationDialog(final int position, ArrayAdapter a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete this item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the item and refresh the list
                        a.notifyDataSetChanged();
                        // Add your code to perform the deletion or launch your delete intent
                        SQLiteDatabase sq = openOrCreateDatabase("routes", Context.MODE_PRIVATE, null);
                        DBHelper db = new DBHelper(sq);
                        // db.deleteRoute(r.get(position).getContent(), r.get(position).getTitle());
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel the delete operation
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }
}
