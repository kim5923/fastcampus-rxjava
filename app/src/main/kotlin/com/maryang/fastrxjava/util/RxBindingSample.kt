package com.maryang.fastrxjava.util

import android.widget.TextView
import io.reactivex.Observable

object RxBindingSample {
    fun click(textView: TextView) =
        Observable.create<Unit> { emitter ->
            textView.setOnClickListener {
                emitter.onNext(Unit)
            }
        }
}