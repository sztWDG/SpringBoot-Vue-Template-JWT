package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MyProjectBackendApplicationTests {

    @Test
    void contextLoads() {

        SubClass subClass = new SubClass();
        subClass.superClassMethod();

    }

    class SuperClass {
        static void superClassMethod() {
            System.out.println("This is a static method in the superclass.");
        }
    }

    class SubClass extends SuperClass {
        void subClassMethod() {
            // 直接调用超类的类方法
            superClassMethod();
        }
    }
}
