package com.example.campuslostfound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "campuslostfound.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    public static final String TABLE_ITEMS = "items";

    // Column names
    public static final String COL_ID = "id";
    public static final String COL_TYPE = "type";
    public static final String COL_ITEM_NAME = "item_name";
    public static final String COL_CATEGORY = "category";
    public static final String COL_LOCATION = "location";
    public static final String COL_DATE = "date";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_CONTACT = "contact";
    public static final String COL_IMAGE = "image";
    public static final String COL_STATUS = "status";

    // Create table query
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_ITEMS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TYPE + " TEXT, " +
                    COL_ITEM_NAME + " TEXT, " +
                    COL_CATEGORY + " TEXT, " +
                    COL_LOCATION + " TEXT, " +
                    COL_DATE + " TEXT, " +
                    COL_DESCRIPTION + " TEXT, " +
                    COL_CONTACT + " TEXT, " +
                    COL_IMAGE + " TEXT, " +
                    COL_STATUS + " TEXT DEFAULT 'Active'" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // Insert item
    public long insertItem(String type, String itemName, String category,
                           String location, String date, String description,
                           String contact, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TYPE, type);
        values.put(COL_ITEM_NAME, itemName);
        values.put(COL_CATEGORY, category);
        values.put(COL_LOCATION, location);
        values.put(COL_DATE, date);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_CONTACT, contact);
        values.put(COL_IMAGE, imageUri);
        values.put(COL_STATUS, "Active");
        long id = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return id;
    }

    // Get all items
    public List<ItemModel> getAllItems() {
        List<ItemModel> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS +
                " ORDER BY " + COL_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                ItemModel item = new ItemModel();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                item.setType(cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)));
                item.setItemName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
                item.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)));
                item.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)));
                item.setContact(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT)));
                item.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE)));
                item.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)));
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    // Search items by name
    public List<ItemModel> searchItems(String query) {
        List<ItemModel> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS +
                " WHERE " + COL_ITEM_NAME + " LIKE '%" + query + "%'" +
                " OR " + COL_CATEGORY + " LIKE '%" + query + "%'" +
                " OR " + COL_LOCATION + " LIKE '%" + query + "%'" +
                " ORDER BY " + COL_ID + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                ItemModel item = new ItemModel();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                item.setType(cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)));
                item.setItemName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ITEM_NAME)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
                item.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)));
                item.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)));
                item.setContact(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTACT)));
                item.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE)));
                item.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)));
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return itemList;
    }

    // Delete item
    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Update status
    public void updateStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, status);
        db.update(TABLE_ITEMS, values, COL_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
    }
}