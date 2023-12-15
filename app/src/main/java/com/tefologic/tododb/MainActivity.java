package com.tefologic.tododb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    // DataBase
    private DatabaseHelper dbHelper;
    private LinearLayout containerLayout;
    private Button newButton;
    private Button deleteButton;

    // to save button
    private LinearLayout editTextContainer;
    private ArrayList<EditText> editTextList;
    private static final String PREFS_NAME = "MyPrefsFile";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        editTextContainer = findViewById(R.id.containerLayout);

        // method for save button to store all data

        editTextContainer = findViewById(R.id.containerLayout);
        editTextList = new ArrayList<>();

        // logic for save button

        Button saveButton = findViewById(R.id.save);
//
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEditTextContent();
            }
        });

        loadSavedEditTextContent();

        containerLayout = findViewById(R.id.containerLayout);
        newButton = findViewById(R.id.newbutton);
        deleteButton = findViewById(R.id.delete);

        // Giving onclick method to new button to create new texts

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewEditableTextView();
            }
        });

        // Giving onclick method to delete button to delete created texts

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedTextView();
            }
        });
    }



    // now writing logic to the methods
    public void OnAddButtonClick(View view)
    {addNewEditableTextView();}
    private void addNewEditableTextView() {

//       noteEditText= findViewById(R.id.noteEditText);
        EditText noteEditText = new EditText(this);
        LinearLayout.LayoutParams LayoutParams =new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT );

        LayoutParams.setMargins(0,0,0,16);// Adds margin to the texts between
        noteEditText.setLayoutParams(LayoutParams);
        containerLayout.addView(noteEditText);

    }

    // save button method
    private void saveEditTextContent() {
//        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
//        Set<String> editTextValues = new HashSet<>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Clear existing data
        db.delete("mytable", null, null);

        for (int i = 0; i < editTextContainer.getChildCount(); i++) {
            EditText editText = (EditText) editTextContainer.getChildAt(i);
            ContentValues values = new ContentValues();
            values.put("content", editText.getText().toString());
            db.insert("mytable", null, values);
        }

        db.close();
    }
    private void deleteSelectedTextView() {                   // Delete method
        int childCount = containerLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = containerLayout.getChildAt(i);
            if (childView instanceof EditText) {
                EditText noteEditText = (EditText) childView;
                if (noteEditText.isFocused()) {
                    containerLayout.removeViewAt(i);
                    break; // Stop searching after deleting the first focused TextView
                }
            }
        }
    }
    // separating data base to make responsive
    private void loadSavedEditTextContent() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> editTextValues = prefs.getStringSet("editTextValues", new HashSet<>());

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("mytable", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            @SuppressLint("Range") String storedText = cursor.getString(cursor.getColumnIndex("content"));
            EditText newEditText = new EditText(this);
            newEditText.setText(storedText);
            editTextContainer.addView(newEditText);
        }

        cursor.close();
        db.close();
        // Adding SQL lite Data Base to store all the content
    }
}