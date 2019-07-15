package com.maryang.fastrxjava.util

import android.util.Log
import com.maryang.fastrxjava.base.BaseApplication.Companion.TAG
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject


object HotObservable {

    fun example() {
        // Observable 은 subscribe 를 한다.
        // subscribe 에는 observer 를 등록한다.
        // subscribe 를 할 때 이벤트를 발행한다.
        // 이벤트를 발행하면 observer 의 onNext(onSuccess)를 실행한다.
        Observable.just(true)
            .subscribe(object:DisposableObserver<Boolean>() {
                override fun onComplete() {

                }

                override fun onNext(t: Boolean) {

                }

                override fun onError(e: Throwable) {

                }
            })

        // subject 는 내부에 List<Observer> 가 있다.
        // subscribe 를 하면 list.add(observer)
        // onNext 를 하면 list.forEach {observer.onNext()}
        val subject = PublishSubject.create<Int>()

        subject.onNext(1)  // observer

        subject.subscribe()   // observerble
    }


    fun logConnectableObservable() {
        var count = 0
        val observable = Observable
            .range(0, 3)
            .timestamp()
            .map { timestamped ->
                Log.d(
                    TAG,
                    "_____________연산__________"
                )
                String.format("[%d] %d", timestamped.value(), timestamped.time())
            }
            .doOnNext { value -> count++ }
            .publish()
            // refCount: 일반적인 Observable 처럼 동작하도록 만들어 준다.
//            .refCount()



        observable.subscribe { value ->
            try {
                Thread.sleep(700)
                Log.d(TAG, "subscriber1 : $value")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        observable.subscribe { value ->
            try {
                Thread.sleep(10)
                Log.d(TAG, "subscriber2 : $value")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        Log.d(TAG, "subscriber finish ")


        observable.connect()

        Thread.sleep(100)

        //connect 로 발행이 끝 다음에 subscribe를 했기 때문에, 이벤트를 못 받음
        observable.subscribe { value ->
            try {
                Thread.sleep(10)
                Log.d(TAG, "subscriber3 : $value")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    fun logAsyncSubject() {
        //onComplete 가 없으면 아무일도 일어나지 않음.
        val subject = AsyncSubject.create<Int>()
        subject.subscribe {
            Log.d(TAG, "AsyncSubject subscriber1 value $it")
        }
        subject.onNext(1)
        subject.subscribe {
            Log.d(TAG, "AsyncSubject subscriber2 value $it")
        }
        subject.onNext(2)
        subject.onNext(3)
        subject.onComplete()

        // subscriber1 3
        // subscriber2 3
    }

    fun logPublishSubject() {
        val subject = PublishSubject.create<Int>()
        // subscribe : observer 등록
        subject.subscribe {
            Log.d(TAG, "PublishSubject subscriber1 value $it")
        }

        // onNext : 이벤트 발행
        subject.onNext(1)
        subject.subscribe {
            Log.d(TAG, "PublishSubject subscriber2 value $it")
        }
        Thread.sleep(100)
        subject.onNext(2)
        subject.onNext(3)

        // subscriber1 1
        // subscriber1 2
        // subscriber2 2
        // subscriber1 3
        // subscriber2 3
    }

    fun logBehaviorSubject() {
        val subject = BehaviorSubject.create<Int>()
        subject.onNext(0)
        subject.subscribe {
            Log.d(TAG, "BehaviorSubject subscriber1 value $it")
        }
        subject.onNext(1)
        subject.subscribe {
            Log.d(TAG, "BehaviorSubject subscriber2 value $it")
        }
        subject.onNext(3)
        subject.onNext(4)

        // subscriber1 0
        // subscriber1 1
        // subscriber2 1
        // subscriber1 3
        // subscriber2 3
        // subscriber1 4
        // subscriber2 4
    }

    fun logReplaySubject() {
        val subject = ReplaySubject.create<Int>()
        subject.onNext(0)
        subject.subscribe {
            Log.d(TAG, "ReplaySubject subscriber1 value $it")
        }
        subject.onNext(1)
        subject.subscribe {
            Log.d(TAG, "ReplaySubject subscriber2 value $it")
        }
        subject.onNext(3)
        subject.onNext(4)

        // subscriber1 0
        // subscriber1 1
        // subscriber2 0
        // subscriber2 1
        // subscriber1 3
        // subscriber2 3
        // subscriber1 4
        // subscriber2 4
    }
}
