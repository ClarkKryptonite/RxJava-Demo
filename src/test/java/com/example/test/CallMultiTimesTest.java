package com.example.test;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CallMultiTimesTest extends BaseTest {
    @Test
    public void testCallMultiTime() {
        List<String> list1 = generateStringList(20);
        System.out.println("list1:" + list1);
        sampleObservable(list1, "list1");

        List<String> list2 = generateStringList(20);
        System.out.println("list2:" + list2);
        sampleObservable(list2, "list2");
    }

    private void sampleObservable(List<String> list, String tag) {
        Observable
                .fromIterable(list)
                .subscribe(
                        string -> System.out.println(tag + " onNext:" + string),
                        throwable -> System.out.println("onError:" + throwable.getMessage()),
                        () -> System.out.println(tag + " Complete")
                );
    }
}
