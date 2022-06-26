package com.geekbrains.lesson4;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

public class ShoppingListTest extends LogMain{

    static RequestSpecification requestSpecification = null;
    static ResponseSpecification responseSpecification = null;
    static String id1;
    static String id2;
    private static final String urlGetShoppingList = properties.getProperty("basUrl")
            + "/mealplanner/"
            + properties.getProperty("userName")
            + "/shopping-list";

    private final String urlAddToShoppingList = properties.getProperty("basUrl")
            + "/mealplanner/"
            + properties.getProperty("userName")
            + "/shopping-list/items/";

    @BeforeEach
    void beforeTest(){
        requestSpecification = new RequestSpecBuilder()
                .addQueryParam("apiKey", properties.getProperty("apiKey"))
                .addQueryParam("hash", properties.getProperty("hash"))
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectResponseTime(Matchers.lessThan(6000L))
                //.expectHeader("Content-Type", "application/json;charset=utf-8")
                .expectHeader("Connection", "keep-alive")
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .build();
    }

    @Test
    void addItem1(){
        id1 = given()
                .spec(requestSpecification)
                .body("{\n"
                        + " \"item\": \""+ (properties.getProperty("item1")) + "\",\n"
                        + " \"aisle\": \"Baking\",\n"
                        + " \"parse\": true\n"
                        + "}")
                .when()
                .post(urlAddToShoppingList)
                //.prettyPeek()
                .then()
                .extract()
                .jsonPath()
                .get("id")
                .toString();
    }

    @Test
    void addItem2(){
        id2 = given()
                .spec(requestSpecification)
                .body("{\n"
                        + " \"item\": \""+ (properties.getProperty("item2")) + "\",\n"
                        + " \"aisle\": \"Baking\",\n"
                        + " \"parse\": true\n"
                        + "}")
                .when()
                .post(urlAddToShoppingList)
                //.prettyPeek()
                .then()
                .extract()
                .jsonPath()
                .get("id")
                .toString();
    }

    @AfterEach
    void getShoppingList(){
        given()
                .spec(requestSpecification)
                .expect()
                .when()
                .get(urlGetShoppingList)
                //.prettyPeek()
                .then()
                .spec(responseSpecification);
    }

    @AfterAll
    static void deleteAllIds() {
        given()
                .spec(requestSpecification)
                .expect()
                //.body(containsStringsInA(id1))
                .body(containsString(id1))
                .body(containsString(id2))
                .when()
                .get(urlGetShoppingList)
                .prettyPeek()
                .then()
                .spec(responseSpecification);

        given()
                .spec(requestSpecification)
                .expect()
                .body("status", equalTo("success"))
                .when()
                .delete(properties.getProperty("basUrl")
                        + "/mealplanner/"
                        + properties.getProperty("userName")
                        + "/shopping-list/items/"
                        + id1)
                //.prettyPeek()
                .then()
                .statusCode(200);

        given()
                .spec(requestSpecification)
                .expect()
                .body("status", equalTo("success"))
                .when()
                .delete(properties.getProperty("basUrl")
                        + "/mealplanner/"
                        + properties.getProperty("userName")
                        + "/shopping-list/items/"
                        + id2)
                //.prettyPeek()
                .then()
                .statusCode(200);
    }
    @AfterAll
    static void end() {
        System.out.println("I'l be back!");
    }

}
