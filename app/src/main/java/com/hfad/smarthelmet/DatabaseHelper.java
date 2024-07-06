package com.hfad.smarthelmet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "UserManager.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "user";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_AGE = "user_age";
    private static final String COLUMN_USER_BLOODTYPE = "user_bloodtype";
    private static final String COLUMN_USER_SEX = "user_sex";
    private static final String COLUMN_CONTACT_PERSON = "contact_person";
    private static final String COLUMN_CONTACT_NUMBER = "contact_number";
    private static final String COLUMN_MEDICAL_CONDITION = "medical_condition";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private Context context;

    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_AGE + " INTEGER,"
            + COLUMN_USER_BLOODTYPE + " TEXT,"
            + COLUMN_USER_SEX + " TEXT,"
            + COLUMN_CONTACT_PERSON + " TEXT,"
            + COLUMN_CONTACT_NUMBER + " TEXT,"
            + COLUMN_MEDICAL_CONDITION + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT,"
            + COLUMN_USER_PASSWORD + " TEXT" + ")";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d(TAG, "Database path: " + context.getDatabasePath(DATABASE_NAME));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Creating table");
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: Dropping table and calling onCreate");
        db.execSQL(DROP_USER_TABLE);
        onCreate(db);
    }

    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.d(TAG, "Adding user: " + user.toString());

        if (user.email == null) {
            Log.e(TAG, "Email is NULL before insertion");
            Toast.makeText(context, "Email cannot be NULL", Toast.LENGTH_SHORT).show();
            return false;
        }

        values.put(COLUMN_USER_NAME, user.name);
        values.put(COLUMN_USER_AGE, user.age);
        values.put(COLUMN_USER_BLOODTYPE, user.bloodtype);
        values.put(COLUMN_USER_SEX, user.sex);
        values.put(COLUMN_CONTACT_PERSON, user.contactPerson);
        values.put(COLUMN_CONTACT_NUMBER, user.contactNumber);
        values.put(COLUMN_MEDICAL_CONDITION, user.medicalCondition);
        values.put(COLUMN_USER_EMAIL, user.email);
        values.put(COLUMN_USER_PASSWORD, user.password);

        Log.d(TAG, "Values to insert: " + values.toString());

        long result = db.insert(TABLE_NAME, null, values);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public User authenticate(User user) {
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_NAME,
                COLUMN_USER_AGE,
                COLUMN_USER_BLOODTYPE,
                COLUMN_USER_SEX,
                COLUMN_CONTACT_PERSON,
                COLUMN_CONTACT_NUMBER,
                COLUMN_MEDICAL_CONDITION,
                COLUMN_USER_EMAIL,
                COLUMN_USER_PASSWORD
        };
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                User dbUser = new User(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_AGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_BLOODTYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_SEX)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_PERSON)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDICAL_CONDITION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD))
                );

                if (user.password.equalsIgnoreCase(dbUser.password) && user.email.equals(dbUser.email)) {
                    cursor.close();
                    return dbUser;
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Other methods...
}
