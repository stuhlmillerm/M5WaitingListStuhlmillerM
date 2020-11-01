package com.csit551.m5waitingliststuhlmillerm.database.model;

/**
 * Created by ravi on 21/02/18.  Adapted by stuhlmillerm on 01/11/20.
 */

public class Student {
    public static final String TABLE_NAME = "students";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IDENTIFIER = "identifier";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String identifier;
    private String name;
    private String priority;
    private String timestamp;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_IDENTIFIER + " TEXT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_PRIORITY + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Student() {
    }

    public Student(int id, String identifier, String name, String priority, String timestamp) {
        this.id = id;
        this.identifier = identifier;
        this.name = name;
        this.priority = priority;
        this.timestamp = timestamp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
