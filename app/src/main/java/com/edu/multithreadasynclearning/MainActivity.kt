package com.edu.multithreadasynclearning

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.edu.multithreadasynclearning.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val liveInteger: MutableLiveData<Int> = MutableLiveData()

    // private val _state = MutableStateFlow(1)
    // val state: StateFlow<Int> = _state
    // val _sharedIntFlow: MutableSharedFlow<Int> = MutableSharedFlow()
    // val sharedIntFlow = _sharedIntFlow.asSharedFlow()

    private var thread: Thread? = null
    private var job: Job? = null

    private var disposableTime: Disposable? = null
    // private var job2: Job? = null

    //  async, sharedFlow, stateFlow, flow operators


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
        binding.recyclerIntList.adapter = Adapter()

        attachLiveDataObserver()
        hideSomeUI()

        startRxFlowable()
        startThread()
        startJobCoroutines()
    }

    private fun hideSomeUI() {
        binding.flowableTime.visibility = View.GONE
        binding.randomIntegerThread.visibility = View.GONE
        binding.randomIntegerCoroutines.visibility = View.GONE
    }

    private fun attachLiveDataObserver() {
        liveInteger.observe(this, {
            binding.randomIntegerThreadValue.text = it.toString()
        })
    }

    private fun startJobCoroutines() {
        binding.randomIntegerCoroutines.visibility = View.VISIBLE
        job = MainScope().launch {
            coroutine().collect {
                withContext(Dispatchers.Main) {
                    binding.randomIntegerCoroutinesValue.text = it.toString()
                    // liveInteger.value = it
                }
            }
        }

        // val job2 = MainScope().launch {
        //     val startDate = Date().time
        //     Log.d("CurrentTime", Date().time.toString())
        //     val testAsync1 = async {
        //         delay(20000)
        //         Log.d   ("Async", "Log_In_testAsync1")
        //         4
        //     }
        //     Log.d   ("Async", "Log_out_of_testAsync1")
        //     testAsync1.await()
        //     val testAsync2 = async {
        //         delay(10000)
        //         Log.d   ("Async", "Async test+${testAsync1.await()}")
        //         5
        //     }
        //     Log.d   ("r2Awaited", "Async test ${testAsync2.await()}")
        //     val endDate = Date().time
        //     Log.d("WorkedFor", ((endDate-startDate)/1000).toString() + "Seconds")
        // }

    }

    private fun startThread() {
        binding.randomIntegerThread.visibility = View.VISIBLE
        thread = createThread()
    }

    private fun startRxFlowable() {
        binding.flowableTime.visibility = View.VISIBLE
        // Main process callbacks are onNext and onError in subscribe
        disposableTime = Flowable.interval(0, 1, TimeUnit.MILLISECONDS)
            .onBackpressureDrop()
            .onErrorReturn { -1 }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.circle.updateAngle((it * 0.006f) % 360)
                binding.millisecondsText.text = getFactTimeFormatted3parts(it / 1000)
                binding.flowableTimeValue.text = it.toString()

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

    private fun coroutine() = flow<Int> {
        repeat(1000) {
            delay(1000)
            emit(getRandomIntAndAddToList(0, 30_000))
        }
    }


    private fun getRandomIntAndAddToList(intMin: Int, intMax: Int): Int {
        val integer = getRandomInteger(intMin, intMax)
        runOnUiThread {
            addToList(integer)
        }
        return integer
    }

    private fun addToList(int: Int) {
        (binding.recyclerIntList.adapter as Adapter).addItem(int.toString())

    }

    override fun onDestroy() {
        super.onDestroy()
        thread?.interrupt()
        disposableTime?.dispose()
        job?.cancel()
        // job2?.cancel()
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