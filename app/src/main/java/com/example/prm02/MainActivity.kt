package com.example.prm02

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.prm02.db.Note
import com.example.prm02.db.NoteDB
import com.example.prm10.PermissionUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.time.LocalDate
import java.util.*
import java.sql.Date
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {



    lateinit var adapter : MyAdapter

    var index = 0

    lateinit var last_file : File

    lateinit var myLoc: MyLoc

    var TEXT_COLOR: Int = Color.WHITE
    var TEXT_SIZE: Float = 14f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //channel notification registration
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("com.example.prm02.channels.mainCh","Main channel",NotificationManager.IMPORTANCE_DEFAULT)
            val notMan = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notMan.createNotificationChannel(channel)
        }

        myLoc = MyLoc(this)

        if(PermissionUtil(this).checkLocationPremissions()){

        }

        openOrCreateDatabase("appDB.db", Context.MODE_PRIVATE,null)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(this)



        refreshAdapter()


        recyclerView.adapter = adapter

        index = adapter.notes.size-1
        readPreferences()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.radius200 -> myLoc.RADIUS = 200f
            R.id.radius500 -> myLoc.RADIUS = 500f
            R.id.radius1000 -> myLoc.RADIUS = 1000f
            R.id.smallBtn -> TEXT_SIZE = 40f
            R.id.mediumBtn -> TEXT_SIZE = 55f
            R.id.largeBtn -> TEXT_SIZE = 70f
            R.id.blackBtn -> TEXT_COLOR = Color.BLACK
            R.id.whiteBtn -> TEXT_COLOR = Color.WHITE
            R.id.blueBtn -> TEXT_COLOR = Color.BLUE
            R.id.greenBtn -> TEXT_COLOR = Color.GREEN
            R.id.redBtn -> TEXT_COLOR = Color.RED
        }
        updatePreferences()
        return super.onOptionsItemSelected(item)
    }

    fun add_clicked(view: View){
        index++
        //camera open
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),0)
            }
        }

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let {
                intent ->

            last_file = this.filesDir.resolve("photo$index.jpg").also { it.createNewFile() }

            val photo_uri = FileProvider.getUriForFile(this,"com.example.prm02.fileProvider",last_file)

            intent.putExtra(MediaStore.EXTRA_OUTPUT,photo_uri)
            startActivityForResult(intent,1)
        }
    }

    fun watch_clicked(id: Int){
        val intent = Intent(this,ItemActivity::class.java).apply {
            putExtra("item_id",id)
            putExtra("mode","edit")
        }





        startActivityForResult(intent,2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //Permission granted for camera
        if(requestCode == 0){

        }

        //camera intent
        if(requestCode == 1 && resultCode ==  Activity.RESULT_OK){
            val current_loc = myLoc.getLastLocation()
            val loc_name = myLoc.getLocationText(current_loc)
            val intent = Intent(this,ItemActivity::class.java).apply {
                putExtra("photo_path",last_file.absolutePath)
                putExtra("mode","add")
                putExtra("location_name",loc_name)
                putExtra("location_longitude",current_loc.longitude)
                putExtra("location_latitude",current_loc.latitude)
            }
            startActivityForResult(intent,2)


        }
        //itemActivity intent
        if(requestCode == 2){
            refreshAdapter()
            Toast.makeText(this,"refreshed", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun refreshAdapter(){
        Room.databaseBuilder(this,NoteDB::class.java,"appDB.db").build().let{
            thread {
                var listFromDB = it.getDAO().getAllNotes()
                runOnUiThread {
                    adapter.refresh(listFromDB) //refreshing
                }
                it.close()

            }

        }
    }

    fun updatePreferences(){
        getSharedPreferences("settings.xml",Context.MODE_PRIVATE).edit().apply{
            putFloat("radius",myLoc.RADIUS)
            putFloat("size",TEXT_SIZE)
            putInt("color",TEXT_COLOR)
        }.apply()

    }

    fun readPreferences(){
        myLoc.RADIUS = getSharedPreferences("settings.xml",Context.MODE_PRIVATE).getFloat("radius",1000f)
        TEXT_SIZE = getSharedPreferences("settings.xml",Context.MODE_PRIVATE).getFloat("size",50f)
        TEXT_COLOR = getSharedPreferences("settings.xml",Context.MODE_PRIVATE).getInt("color",Color.MAGENTA)

    }




}
