package com.maryang.fastrxjava.util

import com.maryang.fastrxjava.entity.GithubRepo
import com.maryang.fastrxjava.entity.Sample
import com.maryang.fastrxjava.entity.User
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors
import java.util.function.BiConsumer


object Operators {

    fun kotlinOperators() {
        // 블록에 인자로 넘기냐 let also
        // 블록에 리시버로 넘기냐 with, run
        // 리턴값은 마지막에 선언한 것 let with run
        // 리턴값은 자기 자신 let also

        //let
        listOf<User>().let {
            it.forEach {

            }
            1
        }

        //with
        with(listOf<User>()) {
            forEach {

            }
            1
        }

        //run
        listOf<User>().run {
            forEach {

            }
            1
        }

        //apply
        Sample().apply {
            data = 3
        }
        val sample = Sample()
        sample.data = 3

        //also
        Sample().also {
            it.data
        }.run {

        }

        listOf<User>().map {
            it.name
        }
        // flatmap 자기 자신의 타입으로 반환해야함
        // rxjava에서 많이 사용해야함
        // 리스트를 flat 하게 펼침
        listOf<User>().flatMap {
            listOf(it.name)
        }

        //filter
        listOf<GithubRepo>().filter {
            it.star
        }

        //find
        listOf<GithubRepo>().find {
            it.star
        }

        //reduce: 인자값이 없기 때문에, acc는 객체의 제네릭 타입

        // first
        // number = 1
        // acc = 0
        // acc + number = 1

        // second
        // number = 2
        // acc = 1
        // acc + number = 3

        // third
        // number = 3
        // acc = 3
        // acc + number = 6
        listOf(1, 2, 3).reduce { acc, number ->
            acc + number
        }


        // fold 반환 타입은 initial 타입
        //
        // first
        // initial = 10
        // number = 1
        // acc = 0
        // initial + acc + number = 11

        // second
        // number = 2
        // acc = 11
        // acc + number = 13

        // third
        // number = 3
        // acc = 13
        // acc + number = 16

        listOf(1, 2, 3).fold(10f) { acc, number ->
            acc + number
        }

        // flatten

        listOf(listOf(1, 2, 3)).flatten().run {
            // 1, 2, 3
        }

        // all: 순회하면서 조건에 맞는것을 검사 (element 가 하나롣 부합하지않으면 false)
        listOf(1, 2, 3).all {
            it == 1
        }

        // any : any의 반대 element 하나라도 true이면 true 반환


        //distinct : 중복 요소 제거
        listOf(1, 1, 2, 2, 3, 4, 5).distinct()


        // groupBy 조건, 키값에 따라 그룹핑
        listOf(1, 1, 2, 2, 3, 4, 5).groupBy {
            it == 1
        }.run {
            // map(true) = listOf(1,1)
            // map(false) = listOf(2, 2, 3, 4, 5)
        }
    }

    fun rxJavaExample() {
        // map
        Single.just(true)
            //여기 까지는 true를 반환하는 Single
            .map { false }
        // 여기부터는 false를 반환하는 Single

        // flatmap - 비동기의 비동기를 처리하기 위한 operator
        Single.just(true)
            .flatMap { Single.just(false) }

        // debounce: Single에서는 사용을 못하고 Observable이나 Floawable에서 사용
        // 지속적인 이벤트를 받은 경우에 유용
        // thread의 체인지가 있음

        // concat: 복수개의 stream을 순서대로 하나로 묶는다.
        Single.concat(
            Single.just(listOf(1, 1, 1)),
            Single.just(listOf(2, 2))
        ).subscribe {
            //1, 1, 1, 2, 2
        }

        // merge: 복수개의 stream을 발행 순서대로 받는다.
        Single.merge(
            Single.just(listOf(1, 1, 1)),
            Single.just(listOf(2, 2))
        ).subscribe {
            //1, 1, 1, 2, 2
        }

        // zip
        Single.zip(
            Single.just(true),
            Single.just(1),
            BiFunction { first, second ->
                ""
            }
        ).subscribe ({
            //1, 1, 1, 2, 2
        }, {

        })

        // combineLatest: zip은 1:1 합치지만, combineLatest는 최근에 발행된 아이템들끼리의 merge
        Observable.combineLatest(
            Observable.just(true),
            Observable.just(1),
            BiFunction { t1, t2 ->

            }
        )

        // compose
    }

    fun thread() {
        Executors.newCachedThreadPool()

        // cached 휴식중이 thread
        // fixed 쓰레드 개수가 고정
        // single 쓰레드를 하나로 사용
    }

    fun rxJavaTherad() {
        // computation: 계산하기에 좋은 쓰레드 풀
        // io: 입출력 쓰기에 좋은 쓰레드풀(비동기성 보장)
        // trampoline: 자신을 실행시킨 쓰레드에서 실행
        // from: 기존에 사용하였던 Executors를 사용하기 위함
        Single.just(true)
            .subscribeOn(Schedulers.io())
    }

    fun <T> applySchedulers(observable: Single<T>): Single<T> {
        return observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> applySchedulers(): SingleTransformer<T, T> {
        return SingleTransformer {
            it.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    val schedulersTransformer = SingleTransformer<Any, Any> {
        it.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> applySchedulersRecycle(): SingleTransformer<T, T> {
        return schedulersTransformer as SingleTransformer<T, T>
    }
}

fun <T> Single<T>.applySchedulersExtension(): Single<T> =
    subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
