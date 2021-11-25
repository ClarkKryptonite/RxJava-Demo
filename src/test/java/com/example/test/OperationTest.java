package com.example.test;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OperationTest extends BaseTest {

    /**
     * <pre> result:
     *     {@code
     * onSubscribe
     * doOnNext
     * onNext:1
     * doOnNext
     * onNext:2
     * doOnNext
     * onNext:3
     * onNext:4
     * onNext:5
     * onNext:6
     * onComplete
     *     }
     * </pre>
     */
    @Test
    public void testDoOnNext() {
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

    /**
     * <pre> result:
     * {@code
     * onSubscribe:
     * doOnNext: 1
     * doOnNext2:1 hhh
     * onNext:1 hhh
     * doOnNext: 2
     * doOnNext2:2 hhh
     * onNext:2 hhh
     * doOnNext: 3
     * doOnNext2:3 hhh
     * onNext:3 hhh
     * onComplete:
     * }
     * </pre>
     */
    @Test
    public void testMultiDoOnNext() {
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

    /**
     * <pre> result:
     * {@code
     * onNext:map:0
     * onNext:map:1
     * onNext:map:2
     * doOnComplete 0000
     * doOnComplete 111
     * onComplete
     * }
     * </pre>
     */
    @Test
    public void testDoOnComplete() {
        Observable.create(emitter -> {
                    emitter.onNext(0);
                    emitter.onNext(1);
                    emitter.onNext(2);
                    emitter.onComplete();
                    emitter.onNext(3);
                })
                .doOnComplete(() -> System.out.println("doOnComplete 0000"))
                .map(i -> "map:" + i)
                .doOnComplete(() -> System.out.println("doOnComplete 111"))
                .subscribe(o -> System.out.println("onNext:" + o),
                        t -> System.out.println(t.getMessage()),
                        () -> System.out.println("onComplete"));
    }

    /**
     * <pre> result:
     * {@code
     * concat subscribe: Integer0
     * concat subscribe: Integer1
     * concat subscribe: Integer2
     * concat subscribe: String 0
     * concat subscribe: String 1
     * concat subscribe: String 2
     * concat complete
     * }
     * </pre>
     */
    @Test
    public void testConcat() {
        Observable<String> integerObservable = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(0);
            emitter.onNext(1);
            TimeUnit.SECONDS.sleep(1);
            emitter.onNext(2);
            emitter.onComplete(); // 需要加onComplete才会接着执行
        }).map(integer -> "Integer" + integer).subscribeOn(Schedulers.io());

        Observable<String> stringObservable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            emitter.onNext("String 0");
            emitter.onNext("String 1");
            emitter.onNext("String 2");
            emitter.onComplete();
        }).subscribeOn(Schedulers.io());

        Observable.concat(integerObservable, stringObservable)
                .subscribe(
                        s -> System.out.println("concat subscribe: " + s),
                        t -> {
                        },
                        () -> System.out.println("concat complete")
                );
    }

    /**
     * <pre> result:
     * {@code
     * integerObservable thread:RxCachedThreadScheduler-1
     * stringObservable thread:RxCachedThreadScheduler-2
     * merge onNext: String 0
     * merge onNext: Integer0
     * merge onNext: Integer1
     * merge onNext: String 1
     * merge onNext: String 2
     * merge onNext: Integer2
     * error!!!!
     * }
     * </pre>
     * 注意：
     * 调用{@code onError()}后如果stringObservable处于TimeUnit.MILLISECONDS.sleep();中会报
     * <blockquote><pre>
     *  io.reactivex.rxjava3.exceptions.UndeliverableException
     * </pre></blockquote>
     */
    @Test
    public void testMerge() {
        Observable<String> integerObservable = Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            System.out.println("integerObservable thread:" + Thread.currentThread().getName());
            emitter.onNext(0);
            TimeUnit.MILLISECONDS.sleep(10);
            emitter.onNext(1);
            TimeUnit.MILLISECONDS.sleep(10);
            emitter.onNext(2);
            emitter.onError(new Throwable("error!!!!"));
        }).map(integer -> "Integer" + integer).subscribeOn(Schedulers.io());

        Observable<String> stringObservable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
            System.out.println("stringObservable thread:" + Thread.currentThread().getName());
            emitter.onNext("String 0");
            TimeUnit.MILLISECONDS.sleep(15);
            emitter.onNext("String 1");
            TimeUnit.MILLISECONDS.sleep(10);
            emitter.onNext("String 2");
        }).subscribeOn(Schedulers.io());

        Observable.merge(integerObservable, stringObservable)
                .subscribe(
                        s -> System.out.println("merge onNext: " + s),
                        t -> System.out.println(t.getMessage()),
                        () -> System.out.println("merge complete")
                );
    }

    /**
     * <pre> result:
     * {@code
     * 1,3,5,7 thread:RxCachedThreadScheduler-1
     * 2,4,6 thread:RxCachedThreadScheduler-2
     * onNext:3
     * onNext:7
     * onNext:11
     * onComplete thread:RxCachedThreadScheduler-2
     * }
     * </pre>
     */
    @Test
    public void testZip() {
        Observable.zip(
                Observable.just(1, 3, 5, 7)
                        .doOnSubscribe(integer -> System.out.println("1,3,5,7 thread:" + Thread.currentThread().getName()))
                        .subscribeOn(Schedulers.io()),
                Observable.just(2, 4, 6)
                        .doOnSubscribe(integer -> System.out.println("2,4,6 thread:" + Thread.currentThread().getName()))
                        .subscribeOn(Schedulers.io()),
                Integer::sum
        ).subscribe(
                integer -> System.out.println("onNext:" + integer),
                throwable -> System.out.println("onError:" + throwable),
                () -> System.out.println("onComplete thread:" + Thread.currentThread().getName())
        );
    }


    /**
     * <pre>result:
     * {@code
     * onNext:5
     * onNext:7
     * onError:java.lang.Throwable: test error
     * }
     * </pre>
     */
    @Test
    public void testZipWhenError() {
        Observable.zip(
                Observable.just(1, 2, 3),
                subZipErrorObservable(),
                Integer::sum
        ).subscribe(
                result -> System.out.println("onNext:" + result),
                throwable -> System.err.println("onError:" + throwable),
                () -> System.out.println("onComplete")
        );
    }

    private Observable<Integer> subZipErrorObservable() {
        return Observable.create(emitter -> {
            emitter.onNext(4);
            emitter.onNext(5);
            emitter.onError(new Throwable("test error"));
        });
    }

    /**
     * <pre> result:
     * {@code
     * integer: 5 ## integer2: 2
     * Next:7
     * integer: 5 ## integer2: 4
     * Next:9
     * integer: 5 ## integer2: 6
     * Next:11
     * Complete
     * }
     * </pre>
     */
    @Test
    public void testCombineLatest() {
        Observable.combineLatest(
                Observable.just(1, 3, 5),
                Observable.just(2, 4, 6),
                (integer, integer2) -> {
                    System.out.println("integer: " + integer + " ## integer2: " + integer2);
                    return integer + integer2;
                }
        ).subscribe(
                integer -> System.out.println("Next:" + integer),
                throwable -> System.out.println("Error:" + throwable),
                () -> System.out.println("Complete")
        );
    }

    /**
     * <pre> result:
     * {@code
     * Next: 1:4
     * Next: 2:4
     * Next: 3:4
     * Next: 1:5
     * Next: 2:5
     * Next: 3:5
     * Next: 1:6
     * Next: 2:6
     * Next: 3:6
     * onComplete
     * }
     * </pre>
     */
    @Test
    public void testJoin() {
        Observable<Integer> o1 = Observable.just(1, 2, 3);
        Observable<Integer> o2 = Observable.just(4, 5, 6);

        o1
                .join(
                        o2,
                        integer -> Observable.just(String.valueOf(integer)).delay(200, TimeUnit.MILLISECONDS),
                        integer -> Observable.just(String.valueOf(integer)).delay(200, TimeUnit.MILLISECONDS),
                        (integer, integer2) -> integer + ":" + integer2
                )
                .subscribe(
                        s -> System.out.println("Next: " + s),
                        t -> System.out.println(t.getMessage()),
                        () -> System.out.println("onComplete")
                );
    }

    /**
     * <pre> result:
     * {@code
     * onNext:hello, rxjava
     * onNext:hello, java
     * onNext:hello, kotlin
     * onComplete
     * }
     * </pre>
     */
    @Test
    public void testStartWith() {
        Observable.just("hello, java", "hello, kotlin")
                .startWith((SingleSource<String>) observer -> observer.onSuccess("hello, rxjava"))
                .subscribe(
                        s -> System.out.println("onNext:" + s),
                        throwable -> System.out.println(throwable.getMessage()),
                        () -> System.out.println("onComplete")
                );
    }

    /**
     * <pre> result:
     * {@code
     * onNext1:0->time:14:30:25
     * onNext1:1->time:14:30:26
     * onNext1:2->time:14:30:27
     * onNext2:2->time:14:30:27
     * onNext1:3->time:14:30:28
     * onNext2:3->time:14:30:28
     * onNext1:4->time:14:30:29
     * onNext2:4->time:14:30:29
     * onNext1:5->time:14:30:30
     * onNext2:5->time:14:30:30
     * onComplete1
     * onComplete2
     * }
     * </pre>
     */
    @Test
    public void testConnect() {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS).take(6);

        ConnectableObservable<Long> connectableObservable = observable.publish();

        connectableObservable.subscribe(new Observer<>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(@NotNull Long aLong) {
                System.out.println("onNext1:" + aLong + "->time:" + format.format(new Date()));
            }

            @Override
            public void onError(@NotNull Throwable e) {
                System.out.println("e1:" + e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete1");
            }
        });

        connectableObservable.delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Long aLong) {
                        System.out.println("onNext2:" + aLong + "->time:" + format.format(new Date()));
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        System.out.println("e2:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete2");
                    }
                });

        connectableObservable.connect();
    }

    /**
     * <pre> result:
     * {@code
     * Next#1: 0->time:14:34:29
     * Next#3: 0->time:14:34:29
     * Next#1: 1->time:14:34:30
     * Next#3: 1->time:14:34:30
     * Next#1: 2->time:14:34:31
     * Next#3: 2->time:14:34:31
     * Next#1: 3->time:14:34:32
     * Next#3: 3->time:14:34:32
     * Next#2: 0->time:14:34:32
     * Next#4: 3->time:14:34:32
     * Next#1: 4->time:14:34:33
     * Next#2: 1->time:14:34:33
     * Next#3: 4->time:14:34:33
     * Next#4: 4->time:14:34:33
     * Next#1: 5->time:14:34:34
     * Complete#1.
     * Next#2: 2->time:14:34:34
     * Next#3: 5->time:14:34:34
     * Next#4: 5->time:14:34:34
     * Complete#3.
     * Complete#4.
     * Next#2: 3->time:14:34:35
     * Next#2: 4->time:14:34:36
     * Next#2: 5->time:14:34:37
     * Complete#2.
     * }
     * </pre>
     */
    @Test
    public void testRefCount() {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Observable<Long> obs = Observable.interval(1, TimeUnit.SECONDS).take(6);

        ConnectableObservable<Long> connectableObservable = obs.publish();

        Observable<Long> obsRefCount = connectableObservable.refCount();

        obs.subscribe(new Observer<>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {

            }

            @Override
            public void onNext(@NotNull Long aLong) {
                System.out.println("Next#1: " + aLong + "->time:" + format.format(new Date()));
            }

            @Override
            public void onError(@NotNull Throwable e) {
                System.out.println("Error: " + e);
            }

            @Override
            public void onComplete() {
                System.out.println("Complete#1.");
            }
        });

        obs.delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Long aLong) {
                        System.out.println("Next#2: " + aLong + "->time:" + format.format(new Date()));
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        System.out.println("Error: " + e);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Complete#2.");
                    }
                });

        obsRefCount.subscribe(new Observer<>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {

            }

            @Override
            public void onNext(@NotNull Long aLong) {
                System.out.println("Next#3: " + aLong + "->time:" + format.format(new Date()));
            }

            @Override
            public void onError(@NotNull Throwable e) {
                System.out.println("Error: " + e);
            }

            @Override
            public void onComplete() {
                System.out.println("Complete#3.");
            }
        });

        obsRefCount.delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Long aLong) {
                        System.out.println("Next#4: " + aLong + "->time:" + format.format(new Date()));
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        System.out.println("Error: " + e);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Complete#4.");
                    }
                });
    }

    /**
     * <pre> result:
     * {@code
     * Next#1: 0->time:14:36:04
     * Next#1: 1->time:14:36:05
     * Next#1: 2->time:14:36:06
     * Next#2: 0->time:14:36:06
     * Next#2: 1->time:14:36:06
     * Next#2: 2->time:14:36:06
     * Next#1: 3->time:14:36:07
     * Next#2: 3->time:14:36:07
     * Next#1: 4->time:14:36:08
     * Next#2: 4->time:14:36:08
     * Next#1: 5->time:14:36:09
     * Next#2: 5->time:14:36:09
     * Complete#1.
     * Complete#2.
     * }
     * </pre>
     */
    @Test
    public void testReply() {
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        Observable<Long> obs = Observable.interval(1, TimeUnit.SECONDS).take(6);

        ConnectableObservable<Long> connectableObservable = obs.replay();

        connectableObservable.connect();

        connectableObservable.subscribe(new Observer<>() {
            @Override
            public void onSubscribe(@NotNull Disposable d) {

            }

            @Override
            public void onNext(@NotNull Long aLong) {
                System.out.println("Next#1: " + aLong + "->time:" + format.format(new Date()));
            }

            @Override
            public void onError(@NotNull Throwable e) {
                System.out.println("Error: " + e);
            }

            @Override
            public void onComplete() {
                System.out.println("Complete#1.");
            }
        });

        connectableObservable.delaySubscription(3, TimeUnit.SECONDS)
                .subscribe(new Observer<>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NotNull Long aLong) {
                        System.out.println("Next#2: " + aLong + "->time:" + format.format(new Date()));
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        System.out.println("Error: " + e);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Complete#2.");
                    }
                });
    }

    /**
     * <pre> result:
     * {@code
     * doOnNext 00 -AAAAA
     * doOnNext 00 -BBBBB
     * doOnNext 00 -CCCCC
     * doOnNext 00 -DDDDD
     * doOnNext 00 -EEEEE
     * doOnNext 00 -FFFFF
     * doOnNext 00 -GGGGG
     * doOnNext 00 -HHHHH
     * doOnNext 00 -IIIII
     * doOnNext 00 -JJJJJ
     * doOnNext 00 -KKKKK
     * doOnNext 00 -LLLLL
     * doOnNext 00 -MMMMM
     * doOnNext 00 -NNNNN
     * doOnNext 00 -OOOOO
     * doOnNext 00 -PPPPP
     * doOnNext 00 -QQQQQ
     * doOnNext 00 -RRRRR
     * doOnNext 00 -SSSSS
     * doOnNext 00 -TTTTT
     * doOnNext 11 -TTTTT
     * doOnNext 00 -UUUUU
     * doOnNext 11 -UUUUU
     * doOnNext 00 -VVVVV
     * doOnNext 11 -VVVVV
     * doOnNext 00 -WWWWW
     * doOnNext 11 -WWWWW
     * doOnNext 00 -XXXXX
     * doOnNext 11 -XXXXX
     * doOnNext 00 -YYYYY
     * doOnNext 11 -YYYYY
     * doOnNext 00 -ZZZZZ
     * doOnNext 11 -ZZZZZ
     * Subscribe
     * TTTTT
     * UUUUU
     * VVVVV
     * WWWWW
     * XXXXX
     * YYYYY
     * ZZZZZ
     * }
     * </pre>
     */
    @Test
    public void testDoOnNextInIterable() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            char c = (char) (65 + i);
            StringBuilder builder = new StringBuilder();
            builder.append(String.valueOf(c).repeat(5));
            list.add(builder.toString());
        }
        Observable.fromIterable(list)
                .doOnNext(s -> System.out.println("doOnNext 00 -" + s))
                .filter(s -> ((int) s.charAt(0)) - 83 > 0)
                .doOnNext(s -> System.out.println("doOnNext 11 -" + s))
                .toList()
                .toObservable()
                .subscribe(strings -> {
                    System.out.println("Subscribe");
                    strings.forEach(System.out::println);
                });
    }

    /**
     * <pre>
     * {@code
     * testDoOnNextThread:Test worker
     * doOnNext thread:RxCachedThreadScheduler-1
     * 1
     * doOnNext thread:RxCachedThreadScheduler-1
     * 2
     * doOnNext thread:RxCachedThreadScheduler-1
     * 3
     * doOnNext thread:RxCachedThreadScheduler-1
     * next thread:RxComputationThreadPool-1
     * 4
     * 1
     * next thread:RxComputationThreadPool-1
     * 2
     * next thread:RxComputationThreadPool-1
     * 3
     * next thread:RxComputationThreadPool-1
     * 4
     * }
     * </pre>
     */
    @Test
    public void testDoOnNextThread() {
        System.out.println("testDoOnNextThread:" + Thread.currentThread().getName());
        Observable.just(1)
                .doOnNext(i -> {
                    System.out.println("doOnNext thread:" + Thread.currentThread().getName() + " value:" + i);
                    testDoOnNextThreadSubMethod();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(i -> System.out.println("next thread:" + Thread.currentThread().getName() + " value:" + i));
    }

    private void testDoOnNextThreadSubMethod() {
        Observable.just(4, 5, 6)
                .doOnNext(i -> System.out.println("test2 doOnNext thread:" + Thread.currentThread().getName() + " value:" + i))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(i -> System.out.println("test2 next thread:" + Thread.currentThread().getName() + " value:" + i));
    }

    /**
     * <pre>result:
     * {@code
     * onErrorReturn---java.lang.NumberFormatException: For input string: "2A"
     * 0
     * }
     * </pre>
     */
    @Test
    public void testOnErrorReturn() {
        Single.just("2A")
                .map(v -> Integer.parseInt(v, 10))
                .onErrorReturn(error -> {
                    System.err.println("onErrorReturn---" + error);
                    if (error instanceof NumberFormatException) return 0;
                    else throw new IllegalArgumentException();
                })
                .subscribe(
                        System.out::println,
                        error -> System.err.println("onError should not be printed!")
                );
    }

    /**
     * <pre>result:
     * {@code
     * 0
     * }
     * </pre>
     */
    @Test
    public void testOnErrorReturnItem() {
        Single.just("2A")
                .map(v -> Integer.parseInt(v, 10))
                .onErrorReturnItem(0)
                .subscribe(
                        System.out::println,
                        error -> System.err.println("onError should not be printed!")
                );
    }

    /**
     * <pre>result:
     * {@code
     * emitter onNext:[NNNNNNNNNN, VVVVVVV, WWWWWWWWWW, P, WW]
     * flatMapIterable
     * map:NNNNNNNNNN
     * map:VVVVVVV
     * map:WWWWWWWWWW
     * map:P
     * map:WW
     * emitter onError
     * flatMapIterable
     * map:ABCDE
     * next:[10, 7, 10, 1, 2, 5]
     * </pre>
     */
    @Test
    public void testOnErrorReturnItemDownStreamExecute() {
        Observable
                .<List<String>>create(emitter -> {
                    List<String> list = generateStringList(5);
                    System.out.println("emitter onNext:" + list);
                    emitter.onNext(list);
                    System.out.println("emitter onError");
                    emitter.onError(new Throwable("error coming"));
                })
                .onErrorReturn(throwable -> {
                    List<String> list = new ArrayList<>();
                    list.add("ABCDE");
                    return list;
                })
                .flatMapIterable(list -> {
                    System.out.println("flatMapIterable");
                    return list;
                })
                .map(s -> {
                    System.out.println("map:" + s);
                    return s.length();
                })
                .toList().toObservable()
                .subscribe(
                        integer -> System.out.println("next:" + integer),
                        throwable -> System.out.println("error:" + throwable)
                );
    }

    /**
     * <pre>result:
     * {@code
     * Subscribe0 onNext:1
     * Subscribe0 onNext:2
     * Subscribe0 onNext:3
     * Subscribe0 onNext:4
     * Subscribe1 onNext:1
     * Subscribe1 onNext:2
     * Subscribe1 onNext:3
     * Subscribe1 onNext:4
     * }
     * </pre>
     */
    @Test
    public void testDifferentSubscriber() throws InterruptedException {
        Observable<Integer> observable = Observable.just(1, 2, 3, 4);

        observable.subscribe(integer -> System.out.println("Subscribe0 onNext:" + integer));
        TimeUnit.SECONDS.sleep(1);
        observable.subscribe(integer -> System.out.println("Subscribe1 onNext:" + integer));
    }
}
