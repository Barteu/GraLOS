package com.example.gralos.DB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.example.gralos.playerResult.playerResult

//import com.example.gralos.Modal.Message

class DBHelper(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,
    null, DATABASE_VER) {
    companion object {
        private val DATABASE_VER = 1
        private val DATABASE_NAME = "EDMTDB.db"
        //Table results
        private val TABLE_NAME = "Results"
        private val COL_LOGIN = "Login"
        private val COL_SCORE = "Score"

        //Table accounts
        private val TABLE_NAME_A = "Accounts"
        private val COL_LOGIN_A = "Login"
        private val COL_PASSWORD_A = "Password"


    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_TABLE_QUERY = ("CREATE TABLE $TABLE_NAME ($COL_LOGIN TEXT, $COL_SCORE INTEGER)")
        db!!.execSQL(CREATE_TABLE_QUERY)

        val CREATE_TABLE_QUERY_2 = ("CREATE TABLE $TABLE_NAME_A ($COL_LOGIN_A TEXT PRIMARY KEY, $COL_PASSWORD_A TEXT)")
        db!!.execSQL(CREATE_TABLE_QUERY_2)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_A")
        onCreate(db!!)
    }

    //CRUD
    val allResult:MutableList<playerResult>
        get(){
            val listResult = ArrayList<playerResult>()
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val db = this.writableDatabase
            val cursor =  db.rawQuery(selectQuery, null)
            if(cursor.moveToFirst()){
                do {
                    val result = playerResult()
                    result.login = cursor.getString(cursor.getColumnIndex(COL_LOGIN))
                    result.score = cursor.getString(cursor.getColumnIndex(COL_SCORE))


                    listResult.add(result)
                } while (cursor.moveToNext())
            }
            db.close()
            return listResult
        }

    fun addResult(result:playerResult){
        val db= this.writableDatabase
        val values = ContentValues()
        values.put(COL_LOGIN, result.login)
        values.put(COL_SCORE, result.score)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun testOne(key:String): Boolean {
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_LOGIN = '$key'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()){
            if(key == cursor.getString(cursor.getColumnIndex(COL_LOGIN))) {
                db.close()
                return false
            }
        }
        db.close()
        return true
    }

    fun checkLogIn(login: String, password: String): Boolean {
       val selectQuery = "SELECT * FROM $TABLE_NAME_A WHERE $COL_LOGIN_A = '$login' AND $COL_PASSWORD_A='$password' "
        val db = this.writableDatabase
        var cursor = db.rawQuery(selectQuery, null)
        if(cursor.moveToFirst())
        {
            db.close()
            return true
        }
        db.close()
        return false
    }


    fun checkLogAvailability(login: String): Boolean {
        val selectQuery = "SELECT * FROM $TABLE_NAME_A WHERE $COL_LOGIN_A = '$login'"
        val db = this.writableDatabase
        var cursor = db.rawQuery(selectQuery, null)
        if(cursor.moveToFirst())
        {
            db.close()
            return false
        }
        db.close()
        return true
    }

    fun insertAccount(login: String, password: String){
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_LOGIN_A, login)
        contentValues.put(COL_PASSWORD_A, password)
        db.insert(TABLE_NAME_A, null, contentValues)

        val contentValuesResults = ContentValues()
        contentValuesResults.put(COL_LOGIN, login)
        contentValuesResults.put(COL_SCORE,0)
        db.insert(TABLE_NAME, null, contentValuesResults)

        db.close()
    }

    fun getScore(login : String): Int{

        var score=0
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_LOGIN='$login';"
        val db = this.writableDatabase
        val cursor =  db.rawQuery(selectQuery, null)
        if(cursor.moveToFirst()){
                score = cursor.getInt(cursor.getColumnIndex(COL_SCORE))
        }

        db.close()
        return score
    }

    fun updateScore(login: String,score: Int){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_LOGIN, login)
        values.put(COL_SCORE, score)

         db.update(TABLE_NAME, values, COL_LOGIN + "=?", arrayOf(login))
        db.close()

    }

//    fun delResult(key: String) : Boolean{
//        val selectQuery = "DELETE FROM $TABLE_NAME WHERE $COL_LOGIN = '$key'"
//        val db = this.writableDatabase
//        val cursor = db.rawQuery(selectQuery, null)
//        if (cursor.moveToFirst()){
//            if(key == cursor.getString(cursor.getColumnIndex(COL_LOGIN))) {
//                return false
//            }
//        }
//        return true
//
//    }




}