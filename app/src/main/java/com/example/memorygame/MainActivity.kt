package com.example.memorygame

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import com.example.memorygame.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private var score = 0
    private var result: String = ""
    private var userAnswer: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // init views
        binding.apply {
            panel1.setOnClickListener(this@MainActivity)
            panel2.setOnClickListener(this@MainActivity)
            panel3.setOnClickListener(this@MainActivity)
            panel4.setOnClickListener(this@MainActivity)
            startGame()
        }


    }

    private fun startGame() {
        result = ""
        userAnswer = ""
        disableButtons()
        lifecycleScope.launch {
            val round = (3..5).random()
            repeat(round) {
                delay(400)
                val randomPanel = (1..4).random()
                result += randomPanel
                val panel = when (randomPanel) {
                    1 -> binding.panel1
                    2 -> binding.panel2
                    3 -> binding.panel3
                    else -> binding.panel4
                }
                val drawableYellow =
                    ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_yellow)
                val drawableDefault =
                    ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_state)
                panel.background = drawableYellow
                delay(1000)
                panel.background = drawableDefault
            }
            enableButtons()
        }
    }

    private fun loseAnimation() {
        binding.apply {
            score = 0
            tvScore.text = 0.toString()
            disableButtons()
            val drawableLose = ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_lose)
            val drawableDefault =
                ActivityCompat.getDrawable(this@MainActivity, R.drawable.btn_state)
            lifecycleScope.launch {
                binding.root.forEach { view ->
                    if (view is Button) {
                        view.background = drawableLose
                        delay(300)
                        view.background = drawableDefault
                    }
                }
                delay(1000)
                startGame()
            }
        }
    }

    private fun disableButtons() {
        binding.root.forEach { view ->
            if (view is Button) {
                view.isEnabled = false
            }
        }
    }

    private fun enableButtons() {
        binding.root.forEach { view ->
            if (view is Button) {
                view.isEnabled = true
            }
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            userAnswer += when (it.id) {
                R.id.panel1 -> "1"
                R.id.panel2 -> "2"
                R.id.panel3 -> "3"
                R.id.panel4 -> "4"
                else -> ""
            }
            if (userAnswer == result) {
                Log.d(this.javaClass.simpleName, "WIN :)")
                score++
                binding.tvScore.text = score.toString()
                startGame()
            } else if(userAnswer.length >= result.length) {
                loseAnimation()
            }
        }
    }
}