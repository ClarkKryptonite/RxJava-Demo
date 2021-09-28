package com.example.test

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.jupiter.api.Test
import java.lang.ref.WeakReference

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
}