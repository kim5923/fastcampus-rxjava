package com.maryang.fastrxjava.util

import io.reactivex.subjects.PublishSubject

object EventBus {
    var bus = PublishSubject.create<Any>()

    fun post(parameter: Any) {
        bus.onNext(parameter)
    }

    fun subscribe(observer: (Any) -> Unit) {
        bus.subscribe {observer.invoke(it)}
    }
}