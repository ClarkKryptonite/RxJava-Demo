package com.example.test

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class IntervalTest : BaseTest() {

    @Test
    fun testInterval() {
        var dispose: Disposable? = null
        println("start")
        dispose = Observable.interval(1L, TimeUnit.SECONDS)
                .filter { it >= 3L }
                .subscribe {
                    println("it:$it")
                    dispose?.dispose()
                }
    }

    @Test
    fun test() {
        var dispose: Disposable? = null
        println("start")
        dispose = Observable.timer(3, TimeUnit.SECONDS)
                .subscribe {
                    println("$it has arrived")
                    dispose?.dispose()
                }
    }
}