package com.example.quizy.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizy.databinding.ActivityScoreBinding

class ScoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val totalScore = intent.getIntExtra("total", 0)
        val correct = intent.getIntExtra("score", 0)
        val wrong = totalScore - correct

        // Use the 'correct' and 'wrong' variables as needed
        binding.totalQuestion.text = totalScore.toString()
        binding.rightAnswer.text = correct.toString()
        binding.wrongAnswer.text = wrong.toString()
        binding.btnRetry.setOnClickListener {
            val intent = Intent(this@ScoreActivity, SetsActivity::class.java)
            startActivity(intent)
            finish() // Optional: Finish the current activity if needed
        }
        binding.btnQuit.setOnClickListener{
            finish()
        }
    }
}
