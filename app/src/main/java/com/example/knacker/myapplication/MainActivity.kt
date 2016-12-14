package com.example.knacker.myapplication

import android.R.id.input
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.knacker.myapplication.NoteActivity
import com.example.knacker.myapplication.R
import com.example.knacker.myapplication.db.NoteDbHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_note.view.*
import kotlinx.android.synthetic.main.item_note.*
import java.util.*
import kotlin.jvm.javaClass

class MainActivity : AppCompatActivity() {
    private var TAG: String = "MainActivity";
    private var mHelper: NoteDbHelper = NoteDbHelper(this)
    private var mNoteListView: ListView? = null
    private var mAdapter: ArrayAdapter<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        //Log.d(TAG, "\nCreate\n")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = mHelper.readableDatabase
        val cursor = db.query("notes", arrayOf("_id","title"),null,null,null,null,null)
        while (cursor.moveToNext()) {
            val idx = cursor.getColumnIndex("title")
            Log.d(TAG, "Note: " + cursor.getString(idx))
        }
        cursor.close()
        db.close()
    }

    override fun onStart() {
        super.onStart()
        mNoteListView =  activity_main.list_notes
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_note -> {
                Log.d(TAG, "Add a new note")

                val noteEditText = EditText(this)
                val dialog = AlertDialog.Builder(this)
                        .setTitle("Add a new note")
                        .setView(noteEditText)
                        .setPositiveButton("Add") {
                            dialog, which ->
                            val note = noteEditText.text.toString()

                            val db: SQLiteDatabase = mHelper.writableDatabase
                            val values = ContentValues()

                            values.put("title",note)
                            values.put("text","Write yout text here!")
                            db.insertWithOnConflict("notes",
                                    null,
                                    values,
                                    SQLiteDatabase.CONFLICT_REPLACE)
                            db.close()
                            updateUI()
                        }
                        .setNegativeButton("Cancel",null)
                        .create()
                dialog.show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI() {
        Log.d(TAG, "Updating UI")
        val noteList = ArrayList<String>()
        val textList = ArrayList<String>()
        val idList = ArrayList<Int>()
        val db = mHelper.readableDatabase
        val cursor = db.query("notes",
                arrayOf("_id","title","text"),null,null,null,null,null)
        while (cursor.moveToNext()) {
            // Log.d(TAG, "Cursooooor!")
            var idx = cursor.getColumnIndex("title")
            noteList.add(cursor.getString(idx))

            idx=cursor.getColumnIndex("text")
            textList.add(cursor.getString(idx))

            idx=cursor.getColumnIndex("_id")
            idList.add(cursor.getInt(idx))
        }

        if (mAdapter == null) {
            //Log.d(TAG, "Here we are")
            mAdapter = ArrayAdapter<String>(this,
                    R.layout.item_note,
                    R.id.note_title,
                    noteList)
            mNoteListView?.adapter=mAdapter
        } else {
            //Log.d(TAG, "Nope, here")
            mAdapter?.clear()
            mAdapter?.addAll(noteList)
            mAdapter?.notifyDataSetChanged()
        }

        cursor.close()
        db.close()

        var list= list_notes
        list.setOnItemClickListener { mAdapter, view, i, l ->
            val db=mHelper.readableDatabase

            val text = textList[i]
            val title = noteList[i]
            val intent = Intent(this, NoteActivity::class.java)
            intent.putExtra("ID",idList[i])
            intent.putExtra("TITLE",title)
            intent.putExtra("TEXT",text)
            startActivity(intent)
        }
    }
}
