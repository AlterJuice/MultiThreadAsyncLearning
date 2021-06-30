package com.edu.multithreadasynclearning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.Flow
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Observable.interval(1, 1, TimeUnit.SECONDS).subscribe(::logText)
    }
    private fun logText(text: Long){
        Log.d("TestLog", text.toString())
    }
}