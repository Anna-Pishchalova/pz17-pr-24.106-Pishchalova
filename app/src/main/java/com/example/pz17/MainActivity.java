package com.example.pz17;

import static com.example.pz17.R.*;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "myLogs";
    private EditText etAnimal, etName, etSize, etWeight;
    private Button btnAdd, btnRead, btnClear;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etAnimal = findViewById(R.id.animal);
        etName = findViewById(R.id.name);
        etSize = findViewById(R.id.size);
        etWeight = findViewById(R.id.weight);

        btnAdd = findViewById(R.id.add);
        btnRead = findViewById(R.id.read);
        btnClear = findViewById(R.id.clear);

        btnAdd.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        dbHelper = new DBHelper(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        String animal = etAnimal.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String sizeStr = etSize.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        switch (v.getId()) {
            case R.id.add:
                if (animal.isEmpty() || name.isEmpty() || sizeStr.isEmpty() || weightStr.isEmpty()) {
                    Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                    break;
                }

                try {
                    float size = Float.parseFloat(sizeStr);
                    float weight = Float.parseFloat(weightStr);

                    cv.put("animal", animal);
                    cv.put("name", name);
                    cv.put("size", size);
                    cv.put("weight", weight);

                    long rowID = db.insert("pets", null, cv);
                    Log.d(LOG_TAG, "Добавлен питомец, ID = " + rowID);
                    Toast.makeText(this, "Питомец добавлен!", Toast.LENGTH_SHORT).show();

                    etAnimal.setText("");
                    etName.setText("");
                    etSize.setText("");
                    etWeight.setText("");

                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Размер и вес должны быть числами", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.read:
                Log.d(LOG_TAG, "--- Список питомцев ---");

                Cursor c = db.query("pets", null, null, null, null, null, null);
                if (c.moveToFirst()) {
                    int idCol = c.getColumnIndex("id");
                    int animalCol = c.getColumnIndex("animal");
                    int nameCol = c.getColumnIndex("name");
                    int sizeCol = c.getColumnIndex("size");
                    int weightCol = c.getColumnIndex("weight");

                    StringBuilder sb = new StringBuilder("Питомцы:\n");
                    do {
                        String logMsg = "ID = " + c.getInt(idCol) +
                                ", animal = " + c.getString(animalCol) +
                                ", name = " + c.getString(nameCol) +
                                ", size = " + c.getFloat(sizeCol) +
                                ", weight = " + c.getFloat(weightCol);

                        Log.d(LOG_TAG, logMsg);

                        sb.append(c.getString(animalCol))
                                .append(" - ")
                                .append(c.getString(nameCol))
                                .append("\n");

                    } while (c.moveToNext());

                    Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Log.d(LOG_TAG, "Таблица пуста");
                    Toast.makeText(this, "Пока нет питомцев", Toast.LENGTH_SHORT).show();
                }
                c.close();
                break;

            case R.id.clear:
                Log.d(LOG_TAG, "Очистка таблицы");
                int clearCount = db.delete("pets", null, null);
                Log.d(LOG_TAG, "Удалено записей: " + clearCount);
                Toast.makeText(this, "Все записи удалены", Toast.LENGTH_SHORT).show();
                break;
        }

        dbHelper.close();
    }
    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "petsDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "Создание базы данных");
            db.execSQL("create table pets ("
                    + "id integer primary key autoincrement,"
                    + "animal text,"
                    + "name text,"
                    + "size real,"
                    + "weight real" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}