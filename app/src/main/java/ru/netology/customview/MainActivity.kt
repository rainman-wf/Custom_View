package ru.netology.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.customview.ui.StateView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val stateView = findViewById<StateView>(R.id.stateView)

        stateView.maxValue = 3000F

        stateView.data = listOf(
            500F,
            500F,
            500F,
            500F
        )

    }
}