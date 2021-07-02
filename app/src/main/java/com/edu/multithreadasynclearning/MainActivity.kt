package com.edu.multithreadasynclearning

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.edu.multithreadasynclearning.databinding.ActivityMainBinding
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val liveInteger: MutableLiveData<Long> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        attachObserverToLiveData()
        runRandomIntegerObservable()
        runCircleTimeObservable()
    }


    private fun attachObserverToLiveData() {
        liveInteger.observe(this, {
            binding.randomIntegerValue.text = it.toString()
        })
    }

    private fun runCircleTimeObservable() {
        Observable.interval(0, 1, TimeUnit.MILLISECONDS)
            .toFlowable(BackpressureStrategy.DROP)
            .doOnNext {
                MainScope().launch {
                    binding.circle.updateAngle((it * 0.006f) % 360)
                    binding.millisecondsText.text = it.toString()
                }
            }
            .doOnError { Log.d("ObservableToFlowable", "Error in Update circle") }
            .onBackpressureDrop {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "BackpressureStrategy Dropped: $it",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .subscribe()
    }

    private fun runRandomIntegerObservable() {
        testThreads {
            Observable.interval(0, 3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    runOnUiThread {
                        liveInteger.postValue(getRandomInteger(-1000, 1000))
                    }
                }
                .doOnError { runOnUiThread { Log.d("Error", "?") } }
                .subscribe()
        }
    }

    private fun getRandomInteger(intMin: Int, intMax: Int): Long {
        return (Random.nextInt((intMax - intMin) + 1) + intMin).toLong()
    }

    private fun testThreads(runnable: Runnable) {
        Thread(runnable).start()
    }
}