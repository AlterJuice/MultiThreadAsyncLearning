package com.edu.multithreadasynclearning

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.edu.multithreadasynclearning.databinding.ActivityMainBinding
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // val animation = CircleAnimation(binding.circle, 240)
       // animation.setDuration(1000)
       // binding.circle.startAnimation(animation)

        Observable.create { e: ObservableEmitter<Any?> ->
            e.setCancellable { disableButton(binding.buttonPause) }
            binding.buttonPause.setOnClickListener { v -> e.onNext(v) }
        }

    }

    private fun disableButton(view: View){
        view.setOnClickListener(null)
        view.isEnabled = false
    }

    override fun onStart() {
        super.onStart()
        testThreads {
            Observable.interval(0, 1, TimeUnit.SECONDS).subscribe {
                runOnUiThread{binding.circle.updateAngle((((it*10) % 60)*6f).toLong())}
                binding.secondsText.text = it.toString()
            }
        }
        testThreads {
            Observable.interval(0, 1, TimeUnit.MILLISECONDS).subscribe {
                // runOnUiThread{binding.circle.updateAngle((((it/ 100) % 60000) * 0.6f).toLong())}
                binding.millisecondsText.text = it.toString()
            }
        }
    }
    private fun logText(text: Long){
        Log.d("TestLog", text.toString())
    }

    private fun testThreads(runnable: Runnable){
        Thread(runnable).start()
    }
}