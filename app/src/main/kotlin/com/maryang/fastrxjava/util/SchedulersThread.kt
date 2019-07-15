package com.maryang.fastrxjava.util

import android.util.Log
import com.maryang.fastrxjava.base.BaseApplication
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.Executors

object SchedulersThread {

    fun problem1() {
        Log.d(BaseApplication.TAG, "start thread: ${Thread.currentThread().name}")

        val subject = BehaviorSubject.createDefault(Unit)

        subject
            // subscribe 했을 때 실행되는 쓰레드에서 구독이 발생
            .subscribeOn(Schedulers.computation())
            // subscribe, onNext 가 발생했을 때 실행되는 쓰레드를 명시적으로 할 수 있음
            .observeOn(Schedulers.computation())
            .subscribe {
                Log.d(BaseApplication.TAG, "subscribe thread: ${Thread.currentThread().name}")
            }

        Thread.sleep(100L)

        // 현재 쓰레드에서 구독이 발생
        subject.onNext(Unit)

        Thread.sleep(100L)


        // main thread
        // computation
        // main thread
    }

    fun logSchedulersThread() {
        Log.d(BaseApplication.TAG, "current thread: ${Thread.currentThread().name}")

        Single.just(true)
            .doOnSuccess {
                Log.d(BaseApplication.TAG, "just io thread: ${Thread.currentThread().name}")
            }
            .subscribeOn(Schedulers.io())
            .subscribe()

        Single.just(true)
            .doOnSuccess {
                Log.d(
                    BaseApplication.TAG,
                    "just computation thread: ${Thread.currentThread().name}"
                )
            }
            .subscribeOn(Schedulers.computation())
            .subscribe()

        Single.just(true)
            .doOnSuccess {
                Log.d(BaseApplication.TAG, "just trampoline thread: ${Thread.currentThread().name}")
            }
            .subscribeOn(Schedulers.trampoline())
            .subscribe()

        Single.just(true)
            .doOnSuccess {
                Log.d(BaseApplication.TAG, "just newThread: ${Thread.currentThread().name}")
            }
            .subscribeOn(Schedulers.newThread())
            .subscribe()

        Single.just(true)
            .doOnSuccess {
                Log.d(BaseApplication.TAG, "just newThread2: ${Thread.currentThread().name}")
            }
            .subscribeOn(Schedulers.newThread())
            .subscribe()

        val thread = Executors.newSingleThreadExecutor()
        Single.just(true)
            .doOnSuccess {
                Log.d(BaseApplication.TAG, "just from: ${Thread.currentThread().name}")
            }
            .subscribeOn(Schedulers.from(thread))
            .subscribe()

        Single.just(true)
            .doOnSuccess {
                Log.d(BaseApplication.TAG, "just from2: ${Thread.currentThread().name}")
            }
            .subscribeOn(Schedulers.from(thread))
            .subscribe()
    }
}