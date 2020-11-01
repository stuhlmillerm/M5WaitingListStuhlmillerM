package com.csit551.m5waitingliststuhlmillerm.database;

/**
 * Created by ravi on 21/02/18.  Adapted by stuhlmillerm on 01/11/20.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import com.csit551.m5waitingliststuhlmillerm.database.model.Student;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "students_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create students table
        db.execSQL(Student.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Student.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    // Add Student record
    public long insertStudent(String identifier, String name, String priority) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Student.COLUMN_IDENTIFIER, identifier);
        values.put(Student.COLUMN_NAME, name);
        values.put(Student.COLUMN_PRIORITY, priority);

        // insert row
        long id = db.insert(Student.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    // Retrieve Student record
    public Student getStudent(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Student.TABLE_NAME,
                new String[]{Student.COLUMN_ID, Student.COLUMN_IDENTIFIER,
                        Student.COLUMN_NAME, Student.COLUMN_PRIORITY, Student.COLUMN_TIMESTAMP},
                Student.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare student object
        Student student = new Student(
                cursor.getInt(cursor.getColumnIndex(Student.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_IDENTIFIER)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_PRIORITY)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return student;
    }

    // Retrieve all students, ordered by priority and time added
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Student.TABLE_NAME + " ORDER BY " +
                Student.COLUMN_PRIORITY + " DESC, " + Student.COLUMN_TIMESTAMP + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student(
                        cursor.getInt(cursor.getColumnIndex(Student.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(Student.COLUMN_IDENTIFIER)),
                        cursor.getString(cursor.getColumnIndex(Student.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(Student.COLUMN_PRIORITY)),
                        cursor.getString(cursor.getColumnIndex(Student.COLUMN_TIMESTAMP)));

                students.add(student);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // close db connection
        db.close();

        // return student list
        return students;
    }

    // Retrieve count of students on waiting list
    public int getStudentsCount() {
        String countQuery = "SELECT  * FROM " + Student.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Remove student from the list
    public void deleteStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Student.TABLE_NAME, Student.COLUMN_ID + " = ?",
                new String[]{String.valueOf(student.getId())});
        db.close();
    }

    // Update student info
    public void updateStudent(Student student) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Student.COLUMN_IDENTIFIER, student.getIdentifier());
        values.put(Student.COLUMN_NAME, student.getName());
        values.put(Student.COLUMN_PRIORITY, student.getPriority());

        // update row
        db.update(Student.TABLE_NAME, values, Student.COLUMN_ID + " = ?",
                new String[]{String.valueOf(student.getId())});

        // close db connection
        db.close();

    }

}