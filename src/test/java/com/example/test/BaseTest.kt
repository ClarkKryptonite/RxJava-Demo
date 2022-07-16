package com.example.test

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import org.junit.jupiter.api.AfterEach
import java.util.*
import java.util.concurrent.TimeUnit

open class BaseTest {
    private val random = Random()

    @AfterEach
    @Throws(InterruptedException::class)
    fun interruptMainThread() {
        TimeUnit.SECONDS.sleep(10)
    }

    @JvmOverloads
    fun generateStringList(size: Int = 20): List<String> {
        val list: MutableList<String> = ArrayList()
        for (i in 0 until size) {
            val randomChar = (random.nextInt(26) + 'A'.code).toChar()
            val num = 1 + random.nextInt(10)
            list.add(generateRepeatChar(randomChar, num))
        }
        return list
    }

    private fun generateRepeatChar(c: Char, repeatCount: Int): String {
        return c.toString().repeat(Math.max(0, repeatCount))
    }

    fun generateIntList(size: Int): List<Int> {
        val list: MutableList<Int> = ArrayList()
        for (i in 0 until size) {
            val randomInt = random.nextInt(100)
            list.add(randomInt)
        }
        return list
    }

    fun <T> Observable<T>.subscribeNext(): Disposable {
        return subscribe { next: T -> println("onNext:$next") }
    }

    fun <T> Observable<T>.subscribeNextAndError(): Disposable {
        return subscribe(
                { next: T -> println("onNext:$next") }
        ) { throwable: Throwable -> System.err.println("onError:" + throwable.message) }
    }

    fun <T> Observable<T>.subscribeAll(): Disposable {
        return subscribe(
                { next: T -> println("onNext:$next") },
                { throwable: Throwable -> System.err.println("onError:" + throwable.message) },
                { println("onComplete") }
        )
    }
}