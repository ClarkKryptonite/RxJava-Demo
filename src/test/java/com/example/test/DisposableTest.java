package com.example.test;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class DisposableTest extends BaseTest {

    /**
     * <pre>
     * onNext:1
     * onNext:2
     * onNext:3
     * Disposable isDisposable?true
     * </pre>
     */
    @Test
    public void testSubscribeDisposable() throws InterruptedException {
        Disposable disposable = Observable.just(1, 2, 3)
                .subscribeOn(Schedulers.io())
                .subscribe(i -> System.out.println("onNext:" + i));

        TimeUnit.SECONDS.sleep(1);

        System.out.println("Disposable isDisposable?" + disposable.isDisposed());
    }

    /**
     * <pre>
     * {@code
     * Start....
     * onNext:String 0---0
     * onNext:String 1---1
     * Dispose end
     * }
     * </pre>
     */
    @Test
    public void testZipDisposable() throws InterruptedException {
        Disposable disposable = Observable.zip(
                        Observable.<Integer>create(emitter -> {
                            emitter.onNext(0);
                            emitter.onNext(1);
                            TimeUnit.SECONDS.sleep(3);
                            emitter.onNext(2);
                        }).subscribeOn(Schedulers.io()),
                        Observable.<String>create(emitter -> {
                            emitter.onNext("String 0");
                            emitter.onNext("String 1");
                            TimeUnit.SECONDS.sleep(2);
                            emitter.onNext("String 2");
                        }).subscribeOn(Schedulers.io()),
                        (i, str) -> str + "---" + i
                )
                .subscribeOn(Schedulers.io())
                .subscribe(
                        str -> System.out.println("onNext:" + str),
                        throwable -> System.err.println("onError:" + throwable.getMessage())
                );
        System.out.println("Start....");
        TimeUnit.SECONDS.sleep(1);
        disposable.dispose();
        System.out.println("Dispose end");
    }
}
