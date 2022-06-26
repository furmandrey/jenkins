package com.geekbrains.lesson4;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class LogMain {

    static Properties properties = new Properties();
    private static FileInputStream inputStream;

    static {
        try {
            inputStream = new FileInputStream("src/main/resources/my.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    @BeforeAll
    static void setUp() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        properties.load(inputStream);

    }
}
