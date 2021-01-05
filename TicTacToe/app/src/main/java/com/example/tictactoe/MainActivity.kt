package com.example.tictactoe

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    var myEmail:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        var b:Bundle? = intent.extras
        myEmail = b?.getString("email")
        IncomingCalls()
    }

    fun buClick(view: View) {


        val buSelected = view as Button

        var cellId = 0
        when (buSelected.id) {
            R.id.but1 -> cellId = 1
            R.id.but2 -> cellId = 2
            R.id.but3 -> cellId = 3
            R.id.but4 -> cellId = 4
            R.id.but5 -> cellId = 5
            R.id.but6 -> cellId = 6
            R.id.but7 -> cellId = 7
            R.id.but8 -> cellId = 8
            R.id.but9 -> cellId = 9
        }

       myRef.child("playerOnline").child(sessionID!!).child(cellId.toString()).setValue(myEmail)

    }


    var activePlayer = 1

    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()

    fun playGame(cellId: Int, buSelected: Button) {


        if (activePlayer == 1) {
            buSelected.text = "X"
            buSelected.setBackgroundResource(R.color.blue)
            player1.add(cellId)
            activePlayer = 2


        } else {

            buSelected.text = "O"
            buSelected.setBackgroundResource(R.color.darkgreen)
            player2.add(cellId)
            activePlayer = 1

        }

        buSelected.isEnabled = false

        checkWinner()
    }


    fun checkWinner() {

        var winer = -1


        // row 1
        if (player1.contains(1) && player1.contains(2) && player1.contains(3)) {
            winer = 1
        }
        if (player2.contains(1) && player2.contains(2) && player2.contains(3)) {
            winer = 2
        }


        // row 2
        if (player1.contains(4) && player1.contains(5) && player1.contains(6)) {
            winer = 1
        }
        if (player2.contains(4) && player2.contains(5) && player2.contains(6)) {
            winer = 2
        }

        // row 3
        if (player1.contains(7) && player1.contains(8) && player1.contains(9)) {
            winer = 1
        }
        if (player2.contains(7) && player2.contains(8) && player2.contains(9)) {
            winer = 2
        }


        // col 1
        if (player1.contains(1) && player1.contains(4) && player1.contains(7)) {
            winer = 1
        }
        if (player2.contains(1) && player2.contains(4) && player2.contains(7)) {
            winer = 2
        }


        // col 2
        if (player1.contains(2) && player1.contains(5) && player1.contains(8)) {
            winer = 1
        }
        if (player2.contains(2) && player2.contains(5) && player2.contains(8)) {
            winer = 2
        }


        // col 3
        if (player1.contains(3) && player1.contains(6) && player1.contains(9)) {
            winer = 1
        }
        if (player2.contains(3) && player2.contains(6) && player2.contains(9)) {
            winer = 2
        }



        if (winer == 1) {
            player1WinsCounts += 1
            Toast.makeText(this, "Player 1 win the game", Toast.LENGTH_LONG).show()
            restartGame()

        } else if (winer == 2) {
            player2WinsCounts += 1
            Toast.makeText(this, "Player 2 win the game", Toast.LENGTH_LONG).show()
            restartGame()
        }


    }

    fun autoPlay(cellId: Int) {


        var buSelected: Button?
        buSelected = when (cellId) {
            1 -> but1
            2 -> but2
            3 -> but3
            4 -> but4
            5 -> but5
            6 -> but6
            7 -> but7
            8 -> but8
            9 -> but9
            else -> {
                but1
            }

        }

        playGame(cellId, buSelected)

    }


    var player1WinsCounts = 0
    var player2WinsCounts = 0

    fun restartGame() {

        activePlayer = 1
        player1.clear()
        player2.clear()

        for (cellId in 1..9) {

            var buSelected: Button? = when (cellId) {
                1 -> but1
                2 -> but2
                3 -> but3
                4 -> but4
                5 -> but5
                6 -> but6
                7 -> but7
                8 -> but8
                9 -> but9
                else -> {
                    but1
                }

            }
            buSelected!!.text = ""
            buSelected!!.setBackgroundResource(R.color.whitebu)
            buSelected!!.isEnabled = true
        }

        Toast.makeText(
            this,
            "Player1: $player1WinsCounts, Player2: $player2WinsCounts",
            Toast.LENGTH_LONG
        ).show()


    }
    fun bu_Request(view: View){
        var userDemail = email.text.toString()
        myRef.child("Users").child(splitString(userDemail)).child("Request").push().setValue(myEmail)

        playerOnline(splitString(myEmail!!)+splitString(userDemail))
        playerSymbol="x"
    }
    fun bu_Accept(view: View){
        var userDemail = email.text.toString()
        myRef.child("Users").child(splitString(userDemail)).child("Request").push().setValue(myEmail)

        playerOnline(splitString(userDemail)+splitString(myEmail!!))
        playerSymbol="O"
    }

    var sessionID:String?=null
    var playerSymbol:String?=null
    fun playerOnline(sessionID:String){
         this.sessionID=sessionID
        myRef.child("playerOnline").removeValue()
        myRef.child("playerOnline").child(sessionID!!)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        player1.clear()
                        player2.clear()
                        val td = snapshot!!.value as HashMap<String,Any>
                        if (td!=null){
                            var value:String
                            for (key in td.keys){
                                value = td[key] as String
                               if (value != myEmail) {
                                   activePlayer = if (playerSymbol === "X") 1 else 2
                               }else{
                                   activePlayer = if (playerSymbol === "X") 2 else 1
                               }
                                autoPlay(key.toInt())
                            }
                        }

                    }catch (ex:Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    var number=0
    fun IncomingCalls(){
        myRef.child("Users").child(splitString(myEmail!!)).child("Request")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val td = snapshot!!.value as HashMap<String,Any>
                        if (td!=null){
                            var value:String
                            for (key in td.keys){
                                value = td[key] as String
                                email.setText(value)
                                val notifyme = Notifications()
                                notifyme.Notify(applicationContext,value+"request to play tictactoy",number)
                                number++
                                myRef.child("Users").child(splitString(myEmail!!)).child("Request").setValue(true)
                                break
                            }
                        }

                    }catch (ex:Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun splitString(str: String):String{
        var split = str.split("@")
        return split[0]
    }
}