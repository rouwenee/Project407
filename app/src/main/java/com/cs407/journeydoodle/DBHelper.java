package com.cs407.journeydoodle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBHelper {
    static SQLiteDatabase sqLiteDatabase;
    public DBHelper (SQLiteDatabase sqLiteDatabase){ this.sqLiteDatabase = sqLiteDatabase;}
    public static void createTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS routes (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, date TEXT, content TEXT, title TEXT)");
    }
    public ArrayList<Route> readRoute(String username) {
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM routes WHERE username LIKE ?",
                new String[]{"%" + username + "%"});
        int idIndex = c.getColumnIndex("id");
        int dateIndex = c.getColumnIndex("date");
        int titleIndex = c.getColumnIndex("title");
        int contentIndex = c.getColumnIndex("content");
        c.moveToFirst();
        ArrayList<Route> routesList = new ArrayList<>();
        while(!c.isAfterLast()){
            int id = c.getInt(idIndex);
            String title = c.getString(titleIndex);
            String date = c.getString(dateIndex);
            String content = c.getString(contentIndex);

            Route notes = new Route(id, date, username, title, content);
            routesList.add(notes);
            c.moveToNext();
        }
        c.close();
        sqLiteDatabase.close();
        return routesList;
    }
    public void saveRoute(String username, String title, String date, String content){
        createTable();
        sqLiteDatabase.execSQL("INSERT INTO routes (username, date, title, content) VALUES (?, ?, ?, ?)",
                new String[]{username, date, title, content});
        // Route newRoute = new Route(username, date, title, content);
    }
    public void deleteRoute(int id){
        createTable();
        sqLiteDatabase.execSQL("DELETE FROM routes WHERE id = ?",
                new Integer[]{id});
    }

}