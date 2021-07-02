package com.edu.multithreadasynclearning

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.edu.multithreadasynclearning.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val liveInteger: MutableLiveData<Int> = MutableLiveData()

    private var thread: Thread? = null
    private var job: Job? = null

    private var disposableTime: Disposable? = null
    private val randomIntList = LinkedList<Int>()


    /*
    * SubscribeOn specify the Scheduler on which an Observable will operate.
    * ObserveOn specify the Scheduler on which an observer will observe this Observable.
    * So basically SubscribeOn is mostly subscribed (executed) on a background thread
    * ( you do not want to block the UI thread while waiting for the observable)
    * and also in ObserveOn you want to observe the result on a main thread...
    * If you are familiar with AsyncTask then SubscribeOn is similar
    *  to doInBackground method and ObserveOn to onPostExecute...
    * */

    // Threads, Observable/Flowable, Coroutines -> three different things.
    // Observable and Flowable are almost the same with one difference -> Flowable supports backpressure
    // Do not mix&use one within another one
    // flow {} (coroutines) (Kotlin extension funcs); after you can use @collect method.
    // Do not use coroutines scope in threads (ya?)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        attachLiveDataObserver()
        startThread()
        startRxFlowable()
        startJobCoroutines()
        binding.buttonShowIntList.setOnClickListener {


        }
    }


    private fun attachLiveDataObserver() {
        liveInteger.observe(this, {
            binding.randomIntegerThreadValue.text = it.toString()
        })
    }

    private fun startJobCoroutines() {
        job = MainScope().launch {
            coroutine().collect {
                withContext(Dispatchers.Main) {
                    randomIntList.add(it.toInt())
                    binding.randomIntegerCoroutinesValue.text = it.toString()
//                    liveInteger.value = it
                }
            }
        }
    }

    private fun startThread() {
        thread = createThread()
    }

    private fun startRxFlowable() {
        // Main process callbacks are onNext and onError in subscribe

        disposableTime = Flowable.interval(0, 1, TimeUnit.MILLISECONDS)
//        disposableTime = Observable.interval(0, 1, TimeUnit.MILLISECONDS)
            .onBackpressureDrop()
            .onErrorReturn { -1 }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.circle.updateAngle((it * 0.006f) % 360)
                binding.millisecondsText.text = getFactTimeFormatted3parts(it / 1000 )
                binding.observableTimeValue.text = it.toString()
                // binding.millisecondsText.text = it.toString()

            }, {
                Log.d("An error occurred: ", it.toString())
            })
    }


    private fun createThread(): Thread {
        val t = Thread {
            while (thread?.isInterrupted != true) {
                liveInteger.postValue(getRandomIntAndAddToList(-1000, 1000))
                Thread.sleep(3000)
            }
        }
        t.start()
        return t

    }
    private fun getRandomIntAndAddToList(intMin: Int, intMax: Int): Int{
        val integer = getRandomInteger(intMin, intMax)
        randomIntList.add(0, integer)
        updateUIList()
        return integer
    }
    private fun updateUIList(){
        binding.listIntegers.text = randomIntList.toString()
    }

    private fun coroutine() = flow<Int> {
        repeat(1000){
            delay(1000)
            emit(getRandomIntAndAddToList(0, 30_000))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        thread?.interrupt()
        disposableTime?.dispose()
        job?.cancel()
        Log.d("OnDestroy", "Thread, disposableTime and job destroyed")

    }


    private fun getFactTimeFormatted3parts(factTime: Long): String {
        val formatted = LocalTime.ofSecondOfDay(factTime).toString()
        return if (formatted.startsWith("00:")) formatted.substring(3) else formatted
    }


    private fun getRandomInteger(intMin: Int, intMax: Int): Int {
        return (Random.nextInt((intMax - intMin) + 1) + intMin)
    }

}