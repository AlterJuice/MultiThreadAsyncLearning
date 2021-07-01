package com.edu.multithreadasynclearning

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.edu.multithreadasynclearning.databinding.ActivityMainBinding
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val liveInteger: MutableLiveData<Long> = MutableLiveData()

    private lateinit var randomDisposable: Disposable

    // private lateinit var circleDisposable: Observable<Long>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        liveInteger.observe(this, {
            binding.randomIntegerValue.text = it.toString()
        })

        // circleDisposable = Observable.interval(0, 1, TimeUnit.MILLISECONDS)
        randomDisposable = Observable.interval(0, 3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .doOnNext {
                liveInteger.postValue(getRandomInteger(0, 20000))
            }
            .doOnError { Log.d("Error", "?") }
            .subscribe()

        // val animation = CircleAnimation(binding.circle, 240)
        // animation.setDuration(1000)
        // binding.circle.startAnimation(animation)


    }
    private fun getRandomInteger(intMin: Int, intMax: Int): Long{
        return (Random.nextInt((intMax - intMin)+1) + intMin).toLong()

    }

    private fun disableButton(view: View) {
        view.setOnClickListener(null)
        view.isEnabled = false
    }

    override fun onStart() {
        super.onStart()
        // testThreads {
        //     Observable.interval(0, 1, TimeUnit.SECONDS).subscribe {
        //         runOnUiThread{
        //             binding.circle.updateAngle(((it % 60)*6f).toLong())
        //         }
        //     }
        // }
        testThreads {
            Observable.interval(0, 1, TimeUnit.MILLISECONDS).subscribe {
                runOnUiThread {
                    binding.circle.updateAngle((it * 0.006f) % 360)
                    binding.millisecondsText.text = it.toString()
                }
            }
        }
    }

    private fun logText(text: Long) {
        Log.d("TestLog", text.toString())
    }

    private fun testThreads(runnable: Runnable) {
        Thread(runnable).start()
    }
}