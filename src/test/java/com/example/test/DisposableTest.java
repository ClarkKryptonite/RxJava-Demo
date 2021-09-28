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
}
