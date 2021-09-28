package com.example.test;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class RxJavaOperatorTest {
    public static void main(String[] args) throws InterruptedException {
        testDifferentEmitter();
        Thread.currentThread().join();
    }

    private static void testDifferentEmitter() {
        Observable.create(emitter -> {
                    System.out.println("create 0 emitter");
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("emitter 0 sent next");
                    emitter.onNext(0);
                    Observable.create(emitter1 -> {
                        System.out.println("emitter 1 create");
                        emitter1.onNext(1);
                        emitter.onNext(2);
                        emitter1.onNext(3);
                    }).subscribe(s -> {
                        System.out.println("subscribe 1 :" + s);
                    });
                    emitter.onNext(4);
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(s -> System.out.println("subscribe 0 :" + s));
    }

    private static void testSwitchThread() {
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
                .subscribe(s -> System.out.println(s + " thread:" + Thread.currentThread().getName()),
                        throwable -> System.out.println(throwable.getMessage() + " thread:" + Thread.currentThread().getName())
                );
        try {
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void testNestObservable() {
        Observable
                .create(emitter -> {
                    System.out.println("create 1-1");
                    Observable.create(emitter1 -> {
                        System.out.println("create 2-1");
                        TimeUnit.SECONDS.sleep(1);
                        emitter1.onNext("222");
                    }).subscribe(o -> {
                        System.out.println("subscribe 2-1");
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println("subscribe 2-2");
                    });
                    System.out.println("create 1-2");
                    emitter.onNext("haha");
                    emitter.onComplete();
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(o -> {
                    System.out.println("subscribe 1-1");
                });
    }

    private static void testMultiDoOnNext() {
        Observable.just(1, 2, 3)
                .doOnNext(integer -> System.out.println("doOnNext: " + integer))
                .map(integer -> integer + " hhh")
                .doOnNext(s -> System.out.println("doOnNext2:" + s))
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        System.out.println("onSubscribe:");
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        System.out.println("onNext:" + s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("onError:");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete:");
                    }
                });
    }

    private static void testDoOnNext() {
        Observable
                .concat(
                        Observable.just(1, 2, 3).doOnNext(integer -> {
                            System.out.println("doOnNext");
                        }),
                        Observable.just(4, 5, 6)
                )
                .subscribe(new Observer<>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        System.out.println("onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        System.out.println("onNext:" + integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });
    }
}
