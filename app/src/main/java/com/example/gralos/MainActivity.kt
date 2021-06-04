package com.example.gralos

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gralos.DB.DBHelper
import com.example.gralos.playerResult.playerResultAdapter
import kotlin.random.Random

private lateinit var playerResultAdapter: playerResultAdapter

class MainActivity: AppCompatActivity() {

    private var score: Int = 0
    private var shotsNum: Int = 0
    private var randomNum: Int = 0
    private var login:String = ""

    internal lateinit var db : DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        login = if( intent.getStringExtra("EXTRA_LOGIN")!=null) intent.getStringExtra("EXTRA_LOGIN").toString() else "error"


        val bt_newGame = findViewById<Button>(R.id.button_new_game)
        val editText_num = findViewById<EditText>(R.id.editTextLiczba)
        val bt_shot = findViewById<Button>(R.id.button_shot)
        val bt_scoreboard = findViewById<Button>(R.id.button_scoreboard)
        val text_score = findViewById<TextView>(R.id.textViewScore)
        val text_shots = findViewById<TextView>(R.id.textViewShots)
        val text_shot = findViewById<TextView>(R.id.textViewShot)
        val bt_logOut = findViewById<Button>(R.id.buttonLogOut)
        val text_nick = findViewById<TextView>(R.id.textViewNick)
        text_nick.text = login

        db = DBHelper(this)

        Thread(){
            run{
                score=db.getScore(login)
                text_score.text="Twój aktualny wynik: $score"
            }
        }.start()


        bt_newGame.setOnClickListener(){
            text_shot.text=""
            start_game(text_shots)
        }

        bt_shot.setOnClickListener(){
            if(editText_num.length()>0){
                var number = editText_num.text.toString().toInt()
                if(number<=20 && number>=0)
                {
                    shot(number,text_shot,text_score,text_shots)
                }
                else
                {
                    dialogShow("Błąd","Podaj liczbę z zakresu 0-20")
                }

                editText_num.text.clear();
            }
            else{
                dialogShow("Błąd","Podaj liczbę")
            }
        }

        bt_scoreboard.setOnClickListener(){
            Thread(){
                runOnUiThread(){
                    val intent = Intent(this, RankingActivity::class.java)
                    startActivity(intent)
                }
            }.start()
            this.onPause()
        }

        bt_logOut.setOnClickListener()
        {
            val thread = Thread() {
                runOnUiThread() {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("EXTRA_LAST_ACTIVITY","main")
                    startActivity(intent)
                }
            }
            thread.start()
            Thread(){
                run{
                    Thread.sleep(2000)
                }
                runOnUiThread(){
                    this.onDestroy()
                }
            }.start()

        }

        start_game(text_shots)
    }

    private fun start_game(text_shots: TextView){
        randomNum = Random.nextInt(0,20)
        shotsNum = 0
        text_shots.text="Liczba strzałów: 0"
        Toast.makeText(applicationContext,"Rozpoczęto nową grę",Toast.LENGTH_SHORT).show()
    }

    private fun shot(num: Int,text_shot: TextView,text_score: TextView,text_shots: TextView){
        shotsNum++
        text_shots.text="Liczba strzałów: $shotsNum"
        if(num==randomNum)
        {
            update_points()
            text_score.text="Twój aktualny wynik: $score"
            text_shot.text=""
            start_game(text_shots)
            return
        }
        else if(num<randomNum){
            text_shot.text="Strzelono $num, za mało"
        }
        else {
            text_shot.text="Strzelono $num, za dużo"
        }

        if(shotsNum==10)
        {
            dialogShow("PRZEGRANA!","Porażka! Oddano za dużo strzałów!")
            reset_game(text_score,text_shot,text_shots)
        }

    }

    private fun reset_game(text_score: TextView,text_shot: TextView,text_shots: TextView) {
        score=0

        Thread(){
            run{
                db.updateScore(login,score)
            }
        }.start()

        text_score.text="Twój aktualny wynik: $score"
        text_shot.text=""
        start_game(text_shots)
    }

    fun dialogShow(title: String, message: String)
    {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK"){ dialogInterface: DialogInterface, i: Int ->}
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun update_points()
    {
        var points = 0
        when(shotsNum){
            1 -> points = 5
            in 2..4 -> points = 3
            in 5..6 -> points = 2
            in 7..10 -> points = 1
        }
        score += points

        Thread(){
            run{
                db.updateScore(login,score)
            }
        }.start()

        dialogShow("TRAFIENIE!","Trafiono za $shotsNum razem.\nZdobywasz $points pkt!")
    }


}

