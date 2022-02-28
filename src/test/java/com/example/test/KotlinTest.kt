package com.example.test

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.jupiter.api.Test

class KotlinTest : BaseTest() {
    /**
     * ```console
    doOnNext thread:RxCachedThreadScheduler-1 value:1
    doOnNext thread:RxCachedThreadScheduler-1 value:2
    onNext thread:RxSingleScheduler-1 value:1
    doOnNext thread:RxCachedThreadScheduler-1 value:3
    onNext thread:RxSingleScheduler-1 value:2
    onNext thread:RxSingleScheduler-1 value:3
    doOnNext thread:RxCachedThreadScheduler-1 value:4
    onNext thread:RxSingleScheduler-1 value:4
     * ```
     */
    @Test
    fun testDoOnNext() {
        Observable.just(1, 2, 3, 4)
            .doOnNext { println("doOnNext thread:${Thread.currentThread().name} value:$it") }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.single())
            .subscribe { println("onNext thread:${Thread.currentThread().name} value:$it") }
    }

    /**
     * ```console
     * num:4
     * onNext it:4
     * else if num
     * println text num == 4
     * BUILD SUCCESSFUL in 13s
     * ```
     */
    @Test
    fun test() {
        val num = 4
        println("num:$num")
        if (num < 4) {
            Observable.range(0,3)
        } else if (num == 4){
            Observable.zip(
                Observable.just(4),
                Observable.range(1, 4)
            ) { num1, _ ->
                return@zip num1
            }
        } else {
            Observable.range(5, 5)
        }
        .subscribe {
            println("onNext it:$it")
        }

        if (num < 4) {
            println("if num")
            "text 3"
        } else if (num <= 5){
            println("else if num")
            "text num == 4"
        } else {
            println("else num")
            "text num > 4"
        }.let {
            println("println $it")
        }
    }
}