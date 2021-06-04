package com.example.gralos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gralos.DB.DBHelper
import com.example.gralos.playerResult.playerResult
import com.example.gralos.playerResult.playerResultAdapter
import java.util.*
import kotlin.random.Random


private lateinit var playerResultAdapter: playerResultAdapter


class RankingActivity : AppCompatActivity() {

    internal lateinit var db : DBHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_ranking)

        supportActionBar?.hide()

        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener(){
            this.finish()
        }



        db = DBHelper(this)


         val listResult = db.allResult  //zapisanie wszystkich danych z bazy do tego
        var sortedList = listResult.sortedWith(compareByDescending({ it.score.toInt() }))
        var listLen = if (sortedList.size<10) sortedList.size else 10

         if(listLen>0)
         {
             playerResultAdapter = playerResultAdapter(sortedList.slice(0..listLen-1).toMutableList())    // tworzymy adapter
         }
         else
         {
             playerResultAdapter = playerResultAdapter(mutableListOf())
         }



        val resultList = findViewById<RecyclerView>(R.id.resultList)  // zmienna z RecyclerView
        resultList.adapter = playerResultAdapter   // nadpisujemy adapter z Recyclera
        resultList.layoutManager = LinearLayoutManager(this)
//
//
//


//
//



    }


}
