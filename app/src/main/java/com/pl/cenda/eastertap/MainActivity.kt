package com.pl.cenda.eastertap

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet


class MainActivity : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var timeText: TextView
    private lateinit var tapButton: Button
    private lateinit var background: View
    private var score = 0
    private var gameStarted = false
    private var endWait = false

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var endGameTimer: CountDownTimer
    private var initialCountDown: Long = 10000
    private var endGameWait: Long = 6000
    private var countDownInterval: Long = 1000
    private var timeLeft = 10
    private var waitTime = 5
    private var colorIndex = 0

    private var buttonColors = arrayOf(
        R.color.red,
        R.color.orange,
        R.color.yellow,
        R.color.green,
        R.color.blue,
        R.color.purple,
        R.color.pink
    )

    private var bias = (10..90).toList()

    private val tag = MainActivity::class.java.simpleName

    companion object {
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        Log.d(tag, "onCreate called. Score is: $score")

        scoreText = findViewById(R.id.score_text)
        timeText = findViewById(R.id.time_text)
        tapButton = findViewById(R.id.tap_button)
        background = findViewById(R.id.rView)

        tapButton.setOnClickListener { v ->
            val clickAnimation = AnimationUtils.loadAnimation(this, R.anim.click)
            v.startAnimation(clickAnimation)

            if (!endWait) {
                incremetScore()

                val newButtonColor = buttonColors.random()
                val newBackgroundColor = buttonColors[colorIndex]
                background.setBackgroundColor(getColor(newBackgroundColor))
                tapButton.setBackgroundColor(getColor(newButtonColor))
                colorIndex = (colorIndex + 1) % buttonColors.size

                val cl = background as ConstraintLayout
                val cs = ConstraintSet()
                val randV = bias.random().toFloat() / 100
                val randH = bias.random().toFloat() / 100
                cs.clone(cl)
                cs.setHorizontalBias(R.id.tap_button, randH)
                cs.setVerticalBias(R.id.tap_button, randV)
                cs.applyTo(cl)
            }
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeft = savedInstanceState.getInt(TIME_LEFT_KEY)
            restoreGame()
        }
        else {
            resetGame()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SCORE_KEY, score)
        outState.putInt(TIME_LEFT_KEY, timeLeft)
        countDownTimer.cancel()
        endGameTimer.cancel()

        Log.d(tag, "onSaveInstance called, score: $score, time left: $timeLeft")
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame() {
        endWait = true

        Toast.makeText(this, getString(R.string.game_end_text, score),
                Toast.LENGTH_LONG).show()

        background.setBackgroundColor(getColor(R.color.blue))
        tapButton.setBackgroundColor(getColor(R.color.purple))

        val cl = background as ConstraintLayout
        val cs = ConstraintSet()
        cs.clone(cl)
        cs.setHorizontalBias(R.id.tap_button, 0.5f)
        cs.setVerticalBias(R.id.tap_button, 0.5f)
        cs.applyTo(cl)

        endGameTimer = object : CountDownTimer(endGameWait, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                waitTime = millisUntilFinished.toInt() / 1000
                val waitString = getString(R.string.wait_text, waitTime)
                tapButton.text = waitString
            }

            override fun onFinish() {
                tapButton.text = getString(R.string.tap_button_text)
                endWait = false
                resetGame()
            }
        }

        endGameTimer.start()
    }

    private fun resetGame() {
        score = 0
        val initialScore = getString(R.string.score_text, score)
        scoreText.text = initialScore
        val initialTime = getString(R.string.time_text, 10)
        timeText.text = initialTime

        Log.d(tag, "resetGame called, score: $score, time left: $timeLeft")

        gameStarted = false

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt() / 1000
                val timeLeftString = getString(R.string.time_text, timeLeft)
                timeText.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }
    }

    private fun restoreGame() {
        Log.d(tag, "restoreGame called, score: $score, time left: $timeLeft")

        val restoredScore = getString(R.string.score_text, score)
        scoreText.text = restoredScore

        val restoredTime = getString(R.string.time_text, timeLeft)
        timeText.text = restoredTime

        countDownTimer = object : CountDownTimer((timeLeft * 1000).toLong(), countDownInterval) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished.toInt() / 1000
                val timeLeftString = getString(R.string.time_text, timeLeft)
                timeText.text = timeLeftString
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = true
    }

    private fun incremetScore() {
        if (!gameStarted) {
            startGame()
        }

        score++
        val newScore = getString(R.string.score_text, score)
        scoreText.text = newScore
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart called.")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume called.")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause called.")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop called.")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy called.")
    }
}