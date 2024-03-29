package com.example.test;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class DifferentEmitter extends BaseTest {
    /**
     * <pre> result:
     * {@code
     * create 0 emitter
     * emitter 0 sent next
     * subscribe 0 :0
     * emitter 1 create
     * subscribe 1 :1
     * subscribe 1 :3
     * subscribe 0 :2
     * subscribe 0 :4
     * }
     * </pre>
     */
    @Test
    public void testDifferentEmitter() {
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
                    }).subscribe(s -> System.out.println("subscribe 1 :" + s));
                    emitter.onNext(4);
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(s -> System.out.println("subscribe 0 :" + s));
    }

    /**
     * <pre> result:
     * {@code
     * create 1-1
     * create 2-1
     * subscribe 2-1
     * subscribe 2-2
     * create 1-2
     * subscribe 1-1
     * }
     * </pre>
     */
    @Test
    public void testNestedObservable() {
        Observable.create(emitter -> {
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
                .subscribe(o -> System.out.println("subscribe 1-1"));
    }

    /**
     * <pre>result:
     * {@code
     * Observable 0 emitter
     * Observable 1 emitter
     * subscribe 1 onNext:100
     * subscribe 0 onNext:100
     * subscribe 1 onNext:200
     * subscribe 0 onNext:200
     * Observable 1 emitter error
     * subscribe 1 onError:Observable 1 error
     * subscribe 0 onError:Observable 1 error
     * Observable 1 emitter onComplete
     * }
     * </pre>
     */
    @Test
    public void testDifferentEmitterSubscribe() {
        Observable
                .<String>create(emitter -> {
                    System.out.println("Observable 0 emitter");
                    Observable
                            .<Integer>create(emitter1 -> {
                                System.out.println("Observable 1 emitter");
                                emitter1.onNext(100);
                                emitter1.onNext(200);
                                System.out.println("Observable 1 emitter error");
                                emitter1.onError(new Throwable("Observable 1 error"));
                                System.out.println("Observable 1 emitter onComplete");
                                emitter1.onComplete();
                            })
                            .subscribe(
                                    integer -> {
                                        System.out.println("subscribe 1 onNext:" + integer);
                                        emitter.onNext(integer.toString());
                                    },
                                    throwable -> {
                                        System.out.println("subscribe 1 onError:" + throwable.getMessage());
                                        emitter.onError(throwable);
                                    },
                                    () -> System.out.println("subscribe 1 onComplete")
                            );
                })
                .subscribe(
                        string -> System.out.println("subscribe 0 onNext:" + string),
                        throwable -> System.out.println("subscribe 0 onError:" + throwable.getMessage()),
                        () -> System.out.println("subscribe 0 onComplete")
                );
    }

    /**
     * <pre>result:
     * {@code
     * emitter1 onNext:0
     * emitter 0 onNext:String 0
     * emitter 0 onNext:String 1
     * emitter is Disposed ? false
     * emitter1 onNext:1
     * emitter 0 onNext:0
     * emitter is Disposed ? false
     * emitter1 onNext:2
     * emitter is Disposed ? false
     * emitter 0 onNext:1
     * emitter 0 onNext:2
     * subscribe 1 onComplete
     * emitter is Disposed ? false
     * emitter 0 onComplete
     * }
     * </pre>
     */
    @Test
    public void testDifferentEmitterIsDispose() {
        Observable
                .<String>create(emitter -> {
                    emitter.onNext("String 0");
                    emitter.onNext("String 1");
                    Observable
                            .<Integer>create(emitter1 -> {
                                emitter1.onNext(0);
                                emitter1.onNext(1);
                                emitter1.onNext(2);
                                emitter1.onComplete();
                            })
                            .subscribe(
                                    integer -> {
                                        System.out.println("emitter1 onNext:" + integer);
                                        System.out.println("emitter is Disposed ? " + emitter.isDisposed());
                                        emitter.onNext(integer.toString());
                                    },
                                    throwable -> {
                                        System.out.println("subscribe 1 onError:" + throwable.getMessage());
                                        emitter.onError(throwable);
                                    },
                                    () -> {
                                        System.out.println("subscribe 1 onComplete");
                                        System.out.println("emitter is Disposed ? " + emitter.isDisposed());
                                        emitter.onComplete();
                                    }
                            );
                    emitter.onNext("String 2");
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(
                        string -> System.out.println("emitter 0 onNext:" + string),
                        throwable -> System.out.println("emitter 0 onError:" + throwable.getMessage()),
                        () -> System.out.println("emitter 0 onComplete")
                );
    }

    /**
     * <pre>
     * {@code
     * onNext:0
     * io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException
     * }
     * </pre>
     */
    @Test
    public void testNestObservableErrorNoHandle() {
        Observable
                .<Integer>create(emitter -> {
                    emitter.onNext(0);
                    Observable
                            .<String>error(new Throwable("Nest error"))
                            .subscribe(str -> System.out.println("nest onNext:"+str));
                })
                .subscribeOn(Schedulers.io())
                .subscribe(
                        integer -> System.out.println("onNext:" + integer),
                        throwable -> System.out.println("onError:" + throwable.getMessage())
                );
    }
}
