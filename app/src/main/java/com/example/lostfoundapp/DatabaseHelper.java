package com.example.lostfoundapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lost_found.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "adverts";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create SQLite table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "postType TEXT, " +
                "category TEXT, " +
                "name TEXT, " +
                "phone TEXT, " +
                "description TEXT, " +
                "date TEXT, " +
                "location TEXT, " +
                "imageUri TEXT, " +
                "timestamp TEXT," +
                "latitude REAL, " +
                "longitude REAL)";
        db.execSQL(query);
    }

    // Recreate table if database version changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert new advert
    public boolean insertAdvert(String postType, String category, String name, String phone,
                                String description, String date, String location,
                                String imageUri, String timestamp, double latitude, double longitude) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("postType", postType);
        values.put("category", category);
        values.put("name", name);
        values.put("phone", phone);
        values.put("description", description);
        values.put("date", date);
        values.put("location", location);
        values.put("imageUri", imageUri);
        values.put("timestamp", timestamp);
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    // Get all adverts
    public ArrayList<Advert> getAllAdverts() {
        ArrayList<Advert> adverts = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                Advert advert = new Advert(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getDouble(10),
                        cursor.getDouble(11)
                );
                adverts.add(advert);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return adverts;
    }

    // Filter adverts by category
    public ArrayList<Advert> getAdvertsByCategory(String category) {
        ArrayList<Advert> adverts = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE category = ? ORDER BY id DESC",
                new String[]{category}
        );

        if (cursor.moveToFirst()) {
            do {
                Advert advert = new Advert(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9),
                        cursor.getDouble(10),
                        cursor.getDouble(11)
                );
                adverts.add(advert);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return adverts;
    }

    // Get advert by ID
    public Advert getAdvertById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE id = ?",
                new String[]{String.valueOf(id)}
        );

        if (cursor.moveToFirst()) {
            Advert advert = new Advert(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9),
                    cursor.getDouble(10),
                    cursor.getDouble(11)
            );

            cursor.close();
            return advert;
        }

        cursor.close();
        return null;
    }

    // Delete advert
    public void deleteAdvert(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)});
    }
}