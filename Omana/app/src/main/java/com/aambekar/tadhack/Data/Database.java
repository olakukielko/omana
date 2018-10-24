package com.aambekar.tadhack.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.aambekar.tadhack.Model.Contact;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper
{
    private SQLiteDatabase database;
    private static final String DATABASE_NAME = "ContactDATA";
    private static final int version = 1;
    private static final String TAG = "Database";
    private static final String TABLE_NAME = "Contacts";


    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                     + "Name TEXT not null,"
                     + "Number TEXT not null unique)" ;

    Context context;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, version);
        database = getWritableDatabase();
        this.context = context;
    }


    public void addContact(Contact contact) {

        List<Contact> list = getContacts();
        if(!list.contains(contact)){

            ContentValues values = new ContentValues();
            values.put("Name", contact.name);
            values.put("Number", contact.contact_no);
            long key = database.insert(TABLE_NAME, null, values);
            Log.d(TAG, "addBook: " + key);
        }
        else
        {
            Toast.makeText(context, "This contact already exists in your active list.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteBook(Contact contact) {
        Log.d(TAG, "deleteBook: " + contact.name);

        int cnt = database.delete(TABLE_NAME, "Number" + " = ?", new String[]{contact.contact_no});

        Log.d(TAG, "deleteBook: " + cnt);
    }

    public List<Contact> getContacts(){
        Log.d(TAG, "loadBooks: START");
        ArrayList<Contact> contacts = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{"Name", "Number"}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null); // The sort order

        if (cursor != null) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String name = cursor.getString(0);
                String no = cursor.getString(1);
                Contact contact = new Contact(name, no);
                cursor.moveToNext();
                contacts.add(contact);
            }
            cursor.close();
        }
        Log.d(TAG, "loadContacts: DONE");

        return contacts;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
