package com.example.gralos

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.gralos.DB.DBHelper
import com.example.gralos.playerResult.playerResultAdapter


private lateinit var playerResultAdapter: playerResultAdapter

class LoginActivity : AppCompatActivity() {

    internal lateinit var db : DBHelper
    private var loginSaved : String = ""
    private var passwordSaved : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val rankingButton = findViewById<Button>(R.id.buttonRankingList)
        val logRegButton = findViewById<Button>(R.id.buttonLogReg)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val layoutRepPass = findViewById<LinearLayout>(R.id.linearLayoutRepPass)

        val editLogin = findViewById<EditText>(R.id.editTextLogin)
        val editPassword = findViewById<EditText>(R.id.editTextPassword)
        val editRepPassword= findViewById<EditText>(R.id.editTextRepPassword)


        db = DBHelper(this)

        // sprawdzam czy login Acvitivy został otworzony po fullscreenie aby automatycznie zalogowac sie poprzednimi danymi:
        if(  intent.getStringExtra("EXTRA_LAST_ACTIVITY").equals("fullScreen"))
        {
            getRecord()
            if(loginSaved.length>1)
            {
                logIn(loginSaved,passwordSaved)
            }
        }
        else{
            setRecord("","")
        }


        logRegButton.setOnClickListener(){
            val radio: RadioButton = findViewById( radioGroup.checkedRadioButtonId)
            if( resources.getResourceEntryName(( radio.getId()))=="radioButtonL")   //logowanie
            {
                logIn(editLogin.text.toString(),editPassword.text.toString())
            }
            else //rejestracja
            {
                register(editLogin,editPassword,editRepPassword)
            }
        }

        radioGroup.setOnCheckedChangeListener(
                RadioGroup.OnCheckedChangeListener { group, checkedId ->
                    val radio: RadioButton = findViewById(checkedId)
                    if( resources.getResourceEntryName(( radio.getId()))=="radioButtonL")
                    {
                        layoutRepPass.setVisibility(View.INVISIBLE)
                        logRegButton.text="Zaloguj się"
                    }
                    else
                    {
                        layoutRepPass.setVisibility(View.VISIBLE)
                        logRegButton.text="Zarejestruj się"
                    }
                } )


        rankingButton.setOnClickListener(){
            Thread(){
                runOnUiThread(){
                    val intent = Intent(this, RankingActivity::class.java)
                    startActivity(intent)
                }
            }.start()
            this.onPause()
        }

        }

    override fun onResume() {
        super.onResume()
        if(  intent.getStringExtra("EXTRA_LAST_ACTIVITY").equals("main"))
        {
            setRecord("","")
        }
    }



    private fun register(editLogin: EditText, editPassword: EditText, editRepPassword: EditText) {
        if(editLogin.length()>3)
        {
            if(editLogin.length()<=20)
            {
                if(editPassword.length()>3)
                {
                    if(editPassword.length()<=20)
                    {
                        if (editRepPassword.text.toString() == editPassword.text.toString()) {
                            if (db.checkLogAvailability(editLogin.text.toString())) {
                                db.insertAccount(editLogin.text.toString(), editPassword.text.toString())
                                dialogShow("Zarejestrowano", "Rejestracja udała się")
                                editPassword.text.clear()
                                editRepPassword.text.clear()

                            } else {
                                dialogShow("Nieudana rejestracja", "Konto o podanym loginie już istnieje")
                            }
                        } else {
                            dialogShow("Błąd", "Hasła nie są jednakowe")
                        }
                    }
                    else
                    {
                        dialogShow("Błąd","Hasło jest za długie. Maksymalna długość to 20 znaków")
                    }
                }
                else
                {
                    dialogShow("Błąd","Hasło jest za krótkie")
                }
            }
            else
            {
                dialogShow("Błąd","Login jest za długi. Maksymalna długość to 20 znaków")
            }
        }
        else
        {
            dialogShow("Błąd","Login jest za krótki")
        }

    }

    fun logIn(login:String,password:String){
        if(login.length>3)
        {
            if(password.length>3)
            {
                if(db.checkLogIn(login,password))
                {
                    Thread(){
                        run{
                            setRecord(login,password)
                        }
                        runOnUiThread() {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("EXTRA_LOGIN",login)
                            startActivity(intent)
                        }
                    }.start()
                    Thread(){
                        run{
                            Thread.sleep(1000)
                        }
                        runOnUiThread(){
                            this.onDestroy()
                        }
                    }.start()

                }
                else
                {
                    dialogShow("Nieudane logowanie","Login lub hasło są niepoprawne")
                }
            }
            else
            {
                dialogShow("Błąd","Hasło jest za krótkie")
            }
        }
        else
        {
            dialogShow("Błąd","Login jest za krótki")
        }
    }

    fun dialogShow(title: String, message: String)
    {
        val builder = AlertDialog.Builder(this@LoginActivity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK"){ dialogInterface: DialogInterface, i: Int ->}
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun setRecord(login: String, password: String){
        val sharedData = this.getSharedPreferences("com.example.gralos.shared",0)
        val edit = sharedData.edit()
        edit.putString("login",login)
        edit.putString("password",password)
        edit.apply()
    }

    fun getRecord(){
        val sharedData = this.getSharedPreferences("com.example.gralos.shared",0)

         loginSaved = sharedData.getString("login","").toString()
         passwordSaved = sharedData.getString("password","").toString()

    }
}






