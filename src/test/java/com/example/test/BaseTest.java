package com.example.test;

import org.junit.jupiter.api.AfterEach;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BaseTest {
    private final Random random = new Random();

    @AfterEach
    public void interruptMainThread() throws InterruptedException {
        Thread.currentThread().join();
    }

    List<String> generateStringList() {
        return generateStringList(20);
    }

    List<String> generateStringList(int size) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            char randomChar = (char) (random.nextInt(26) + 'A');
            int num = 1 + random.nextInt(10);
            list.add(generateRepeatChar(randomChar, num));
        }
        return list;
    }

    private String generateRepeatChar(char c, int repeatCount) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < repeatCount; i++) {
            builder.append(c);
        }
        return builder.toString();
    }
}
