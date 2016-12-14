package com.example.knacker.myapplication.db

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor
import android.util.Log
import android.support.v7.app.AppCompatActivity

class NoteDbHelper(context: Context) : SQLiteOpenHelper(
        context, "notes", null, 3) {

    val TAG = "dbhelper"
    val TABLE = "notes"

    companion object {
        public val ID: String = "_id"
        public val TITLE: String = "title"
        public val TEXT: String = "text"
    }

    val DATABASE_CREATE=
            "CREATE TABLE ${TABLE} (" +
                    "${ID} integer PRIMARY KEY autoincrement, " +
                    "${TITLE} TEXT NOT NULL, "+
                    "${TEXT} TEXT"+
                    ");"

    fun putTask(text: String) {
        val values = ContentValues()
        values.put(TITLE, text)
        getWritableDatabase().insert(TABLE,null,values)
    }

    fun getTask() :Cursor {
        return getReadableDatabase()
                .query(TABLE, arrayOf(ID, TITLE, TEXT),null,null,null,null,null)
    }

    override fun onCreate(p0: SQLiteDatabase) {
        Log.d(TAG,"\n\nCreating!!\n\n" + DATABASE_CREATE)
        p0.execSQL(DATABASE_CREATE)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        p0.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(p0)
    }

}