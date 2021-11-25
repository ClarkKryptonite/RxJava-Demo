package com.example.test;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class SwitchThread extends BaseTest {

    /**
     * <pre>
     * {@code
     * create thread:RxCachedThreadScheduler-2
     * map thread:RxCachedThreadScheduler-2
     * map thread:RxCachedThreadScheduler-2
     * map thread:RxCachedThreadScheduler-2
     * map 2 thread:RxSingleScheduler-1
     * map 3 thread:RxSingleScheduler-1
     * map 2 thread:RxSingleScheduler-1
     * map 3 thread:RxSingleScheduler-1
     * map 2 thread:RxSingleScheduler-1
     * map 3 thread:RxSingleScheduler-1
     * Map2 --  aaa 1 thread:RxSingleScheduler-1
     * Map2 --  aaa 2 thread:RxSingleScheduler-1
     * Map2 --  aaa 3 thread:RxSingleScheduler-1
     * }
     * </pre>
     */
    @Test
    public void testSwitchThread() {
        Observable.create(emitter -> {
                    System.out.println("create thread:" + Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(1);
                    emitter.onNext(1);
                    emitter.onNext(2);
                    emitter.onNext(3);
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .map(i -> {
                    System.out.println("map thread:" + Thread.currentThread().getName());
                    return "" + i;
                })
                .observeOn(Schedulers.single())
                .map(i -> {
                    System.out.println("map 2 thread:" + Thread.currentThread().getName());
                    return " aaa " + i;
                })
                .subscribeOn(Schedulers.io())
                .map(s -> {
                    System.out.println("map 3 thread:" + Thread.currentThread().getName());
                    return "Map2 -- " + s;
                })
                .observeOn(Schedulers.single())
                .subscribe(
                        s -> System.out.println(s + " thread:" + Thread.currentThread().getName()),
                        throwable -> System.out.println(throwable.getMessage() + " thread:" + Thread.currentThread().getName())
                );
    }

    /**
     * <pre>
     *     {@code
     * zip thread:RxCachedThreadScheduler-3
     * observable1 thread:RxCachedThreadScheduler-2
     * zip thread:RxCachedThreadScheduler-3
     * zip thread:RxCachedThreadScheduler-3
     * observable2 thread:RxCachedThreadScheduler-3
     * onNext:5 thread:RxSingleScheduler-1
     * onNext:7 thread:RxSingleScheduler-1
     * onNext:9 thread:RxSingleScheduler-1
     *     }
     * </pre>
     */
    @Test
    public void testZipThread() {
        Observable<Integer> observable1 = Observable
                .<Integer>create(emitter -> {
                    emitter.onNext(1);
                    emitter.onNext(2);
                    emitter.onNext(3);
                    System.out.println("observable1 thread:" + Thread.currentThread().getName());
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io());

        Observable<Integer> observable2 = Observable
                .<Integer>create(emitter -> {
                    emitter.onNext(4);
                    emitter.onNext(5);
                    emitter.onNext(6);
                    System.out.println("observable2 thread:" + Thread.currentThread().getName());
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io());

        Observable
                .zip(observable1, observable2, (result1, result2) -> {
                    System.out.println("zip thread:" + Thread.currentThread().getName());
                    return result1 + result2;
                })
                .observeOn(Schedulers.single())
                .subscribe(result -> System.out.println("onNext:" + result + " thread:" + Thread.currentThread().getName()));
    }

    /**
     * <pre> result:
     * {@code
     * doOnNext1 thread:RxSingleScheduler-1
     * doOnNext2 thread:RxCachedThreadScheduler-2
     * doOnNext3 thread:RxSingleScheduler-1
     * onNext: 1
     * }
     * </pre>
     */
    @Test
    public void testFlatThread() {
        testFlatMethod1()
                .doOnNext(integer -> System.out.println("doOnNext1 thread:" + Thread.currentThread().getName()))
                .observeOn(Schedulers.io())
                .doOnNext(integer -> System.out.println("doOnNext2 thread:" + Thread.currentThread().getName()))
                .flatMap(this::testFlatMethod2)
                .doOnNext(s -> System.out.println("doOnNext3 thread:" + Thread.currentThread().getName()))
                .subscribe(result -> System.out.println("onNext: " + result));
    }

    private Observable<Integer> testFlatMethod1() {
        return Observable.just(1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single());
    }

    private Observable<String> testFlatMethod2(int i) {
        return Observable.just(Integer.toString(i))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single());
    }
}
