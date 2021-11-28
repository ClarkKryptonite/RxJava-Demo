package com.example.test

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.jupiter.api.Test

/**
 * @author kun
 * @since 11/28/21
 */
class SourceCodeRead {
    /**
     * 1. Observable.create -> 创建ObservableOnSubscribe(1)，ObservableCreate(0)
     * 2. (Observable).subscribe -> 创建onNext(7),并转化为LambdaObserver
     * 3. 调用ObservableCreate(0)中的subscribeActual
     * 4. 创建CreateEmitter，调用(7)中的onSubscribe
     * 5. 执行(2-5)的方法，对应执行(8)后的内容
     */
    @Test
    fun readCreate() {
        Observable.create( //0
                object : ObservableOnSubscribe<Int> { //1
                    override fun subscribe(emitter: ObservableEmitter<Int>) { //2
                        emitter.onNext(0) //3
                        emitter.onNext(1) //4
                        emitter.onComplete() //5
                    }
                }
        ).subscribe( //6
                object : Consumer<Int> { //7
                    override fun accept(t: Int) {
                        println("onNext:$t") //8
                    }
                }
        )
    }

    /**
     * 1. Observable.create->创建ObservableOnSubscribe(1),ObservableCreate(0)
     * 2. (Observable).map->创建Function(4),ObservableMap(3)
     * 3. (Observable).subscribe->创建Observer(7), 和Observer(3)
     * 4. ObservableMap(3)调用subscribeActual，并记录下流(7)，然后上流ObservableCreate(0)调用subscribeActual,记录下流(3)
     * 5. 执行(2方法)，并调用observer.onNext即5方法，最后到8
     */
    @Test
    fun readMap() {
        Observable.create( //0
                object : ObservableOnSubscribe<Int> { //1
                    override fun subscribe(emitter: ObservableEmitter<Int>) { //2
                        emitter.onNext(0)
                        emitter.onNext(1)
                        emitter.onComplete()
                    }
                }
        ).map( // 3
                object : Function<Int, String> { //4
                    override fun apply(t: Int): String { //5
                        return t.toString()
                    }
                }
        ).subscribe(//6
                object : Consumer<String> { //7
                    override fun accept(t: String) {//8
                        println("onNext:$t")
                    }
                }
        )
    }

    @Test
    fun readSubscribeOn() {
        Observable.create( //0
                object : ObservableOnSubscribe<Int> { //1
                    override fun subscribe(emitter: ObservableEmitter<Int>) { //2
                        emitter.onNext(0)
                        emitter.onNext(1)
                        emitter.onComplete()
                    }
                }
        ).subscribeOn( // 3
                Schedulers.io()
        ).observeOn(
                Schedulers.single()
        ).subscribe(
                object : Consumer<Int> {
                    override fun accept(t: Int) {
                        println("onNext:$t")
                    }
                }
        )
    }
}