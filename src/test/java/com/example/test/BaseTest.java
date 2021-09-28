package com.example.test;

import org.junit.jupiter.api.AfterEach;

public class BaseTest {
    @AfterEach
    public void interruptMainThread() throws InterruptedException {
        Thread.currentThread().join();
    }
}
