package com.example.knacker.myapplication

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.example.knacker.myapplication.R
import kotlinx.android.synthetic.main.activity_note.*
import com.example.knacker.myapplication.db.NoteDbHelper

class NoteActivity : AppCompatActivity() {
    private var mHelper: NoteDbHelper = NoteDbHelper(this)
    private var id: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        var title = intent.extras.getString("TITLE")
        var text = intent.extras.getString("TEXT")
        id = intent.extras.getInt("ID")
        notetitle.text=title
        var note= note as EditText
        if (text!=null) {
            note?.setText(text)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save_note -> {
                val note= note as EditText
                val db = mHelper.writableDatabase

                val cv = ContentValues()
                cv.put("title",notetitle.text as String)
                val note_text= note.text.toString()
                if (note.text!=null) {
                    cv?.put("text", note_text)
                }

                db.update("notes",cv, "_id="+id,null)
                db.close()

                finish()
                return true
            }
            R.id.action_delete_note -> {
                val taskEditText = EditText(this)
                val dialog = AlertDialog.Builder(this)
                        .setTitle("Delete this note?")
                        .setPositiveButton("Yes") {
                            dialog,i ->
                            val db = mHelper.writableDatabase

                            db.delete("notes","_id="+id,null)
                            db.close()

                            finish()
                        }
                        .setNegativeButton("Cancel",null)
                        .create()
                dialog.show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}
