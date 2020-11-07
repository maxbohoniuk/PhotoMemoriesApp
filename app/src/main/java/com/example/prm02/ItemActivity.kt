package com.example.prm02

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.room.Room
import com.example.prm02.db.Note
import com.example.prm02.db.NoteDB
import kotlinx.android.synthetic.main.activity_item.*
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import kotlin.concurrent.thread

class ItemActivity : AppCompatActivity() {


    lateinit var note: Note
    lateinit var photo_path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        if(intent.getStringExtra("mode").equals("add")) {
            photo_path = intent.getStringExtra("photo_path")
            imageView.setImageBitmap(BitmapFactory.decodeFile(photo_path))
            deleteBtn.isEnabled = false
        }
        else{

            Room.databaseBuilder(this,NoteDB::class.java,"appDB.db").build().let{
                thread {
                    this.note = it.getDAO().getAllNotes().get(intent.getIntExtra("item_id", 1))
                    runOnUiThread {
                        titleText.setText(note.title)
                        noteText.setText(note.content)
                        this.photo_path = note.photo_path
                        imageView.setImageBitmap(BitmapFactory.decodeFile(note.photo_path))
                    }
                    it.close()
                }

            }


        }











    }

    fun save_clicked(view: View){

        var title = titleText.text.toString()
        var note_content = noteText.text.toString()
        var loc_name = intent.getStringExtra("location_name")
        var loc_longitude = intent.getDoubleExtra("location_longitude",0.0)
        var loc_latitude = intent.getDoubleExtra("location_latitude",0.0)





        //adding text to bitmap
        var bitmap = BitmapFactory.decodeFile(photo_path).copy(Bitmap.Config.ARGB_8888,true)

        var paint = Paint()
        paint.apply {
            style = Paint.Style.FILL
            color = MyLoc.instance.activity.TEXT_COLOR
            textSize = MyLoc.instance.activity.TEXT_SIZE
        }
        var canvas = Canvas(bitmap)
        canvas.drawText("$loc_name , ${LocalDate.now().toString()}",0f,(bitmap.height-10).toFloat(),paint)

        var file = File(photo_path)
        var os = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,os)
        os.close()

        imageView.setImageBitmap(bitmap)

        //adding/updating note to db
        var temp_note = Note(title = title, date = LocalDate.now().toString(),id = 0,content = note_content,photo_path = this.photo_path,latitude = loc_latitude,longitude = loc_longitude)
        Room.databaseBuilder(this,NoteDB::class.java,"appDB.db").build().let{
            thread {
                if(intent.getStringExtra("mode").equals("add")) {
                    it.getDAO().insert(temp_note)

                    //reg geofence
                    var temp_loc = Location("").apply {
                        latitude = loc_latitude
                        longitude = loc_longitude
                    }
                    MyLoc.instance.registerGeofence(temp_loc,"${MyLoc.instance.activity.index+1}")

                }else{
                    var old_note = it.getDAO().getAllNotes().get(intent.getIntExtra("item_id",1))
                    old_note.title = temp_note.title
                    old_note.content = temp_note.content
                    it.getDAO().update(old_note)
                }
                it.close()


            }

        }
        setResult(2)
        finish()

    }

    fun del_clicked(view: View){
        Room.databaseBuilder(this,NoteDB::class.java,"appDB.db").build().let{
            thread {
                it.getDAO().delete(note)
                it.close()


            }

        }
        setResult(2)
        finish()
    }
}
