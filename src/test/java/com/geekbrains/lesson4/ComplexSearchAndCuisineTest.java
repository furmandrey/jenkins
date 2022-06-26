package com.geekbrains.lesson4;


import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ComplexSearchAndCuisineTest extends LogMain {

    ResponseSpecification responseSpecificationGet = null;
    ResponseSpecification responseSpecificationPost = null;
    RequestSpecification requestSpecificationGet = null;
    RequestSpecification requestSpecificationPost = null;
    static RequestSpecification requestSpecificationAdd = null;

    private final String urlComplexSearch = properties.getProperty("basUrl") + "/recipes/complexSearch";
    private final String urlrecipesCuisine = properties.getProperty("basUrl") + "/recipes/cuisine";

    private static final String urlAddItemsToMealPlanner = properties.getProperty("basUrl")
            + "/mealplanner/"
            + (properties.getProperty("userName"))
            + "/items";

    private static String id1;
    private static String id2;


    @BeforeEach
    void beforeTest(){
        //requests
        requestSpecificationGet = new RequestSpecBuilder()
                .addQueryParam("apiKey", properties.getProperty("apiKey"))
                .build();

        requestSpecificationPost = new RequestSpecBuilder()
                .addQueryParam("apiKey", properties.getProperty("apiKey"))
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        requestSpecificationAdd = new RequestSpecBuilder()
                .addQueryParam("apiKey", properties.getProperty("apiKey"))
                .addQueryParam("hash", properties.getProperty("hash"))
                .build();
        //requests/
        //responses
        responseSpecificationGet = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectResponseTime(Matchers.lessThan(6000L))
                .expectHeader("Content-Type", "application/json")
                .expectHeader("Connection", "keep-alive")
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .build();

        responseSpecificationPost = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectResponseTime(Matchers.lessThan(6000L))
                .expectHeader("Content-Type", "application/json;charset=utf-8")
                .expectHeader("Connection", "keep-alive")
                .expectStatusLine("HTTP/1.1 200 OK")
                .expectContentType(ContentType.JSON)
                .build();
        //responses/
    }

    @Test
    void getVegetarianBurger() {

        given()
                .spec(requestSpecificationGet)
                .queryParam("query", "burger")
                .queryParam("diet", "vegetarian")
                .expect()
                .body("totalResults", equalTo(3))
                .body("results[0].title", either(containsString(properties.getProperty("burger1")))
                        .or(containsString(properties.getProperty("burger2"))).or(containsString(properties.getProperty("burger3"))))
                .body("results[1].title", either(containsString(properties.getProperty("burger1"))).or(containsString(properties.getProperty("burger2")))
                        .or(containsString(properties.getProperty("burger3"))))
                .body("results[2].title", either(containsString(properties.getProperty("burger1")))
                        .or(containsString(properties.getProperty("burger2"))).or(containsString(properties.getProperty("burger3"))))
                .when()
                .get(urlComplexSearch)
                .then()
                .spec(responseSpecificationGet);


    }

    @Test
    void getGlutenFreeBurger() {

        given()

                .spec(requestSpecificationGet)
                .queryParam("query", "burger")
                .queryParam("diet", "Gluten Free")
                .expect()
                .body("totalResults", equalTo(3))
                .body("results[0].title", either(containsString(properties.getProperty("burger4")))
                        .or(containsString(properties.getProperty("burger5"))).or(containsString(properties.getProperty("burger6"))))
                .body("results[1].title", either(containsString(properties.getProperty("burger4"))).or(containsString(properties.getProperty("burger5")))
                        .or(containsString(properties.getProperty("burger6"))))
                .body("results[2].title", either(containsString(properties.getProperty("burger4")))
                        .or(containsString(properties.getProperty("burger5"))).or(containsString(properties.getProperty("burger6"))))
                .when()
                .get(urlComplexSearch)
                //.prettyPeek()
                .then()
                .spec(responseSpecificationGet);
    }

    @Test
    void getNegativeIfVegetarianContainsFish() {
        given()

                .spec(requestSpecificationGet)
                .queryParam("includeIngredients", "fish")
                .queryParam("diet", "vegetarian")
                .expect()
                .body("totalResults", equalTo(0))
                .body("offset", equalTo(0))
                .body("results", hasSize(0))
                .when()
                .get(urlComplexSearch)
                .then()
                .spec(responseSpecificationGet);
    }

    @Test
    void getDrinkMilkWormwood() {

        //String drink1 = "Cookinghow Penne Alla Vodka";
        //String drink2 = "Spiced Lassi";

        given()
                //.log()
                //.all()
                .spec(requestSpecificationGet)
                .queryParam("type", "drink")
                .queryParam("includeIngredients", "milk, wormwood")
                .expect()
                .body("totalResults", equalTo(1))
                .body("results[0].title", equalTo("Milky Watermelon Drink"))
//                .body("results[0].title", either(containsString(drink1))
//                        .or(containsString(drink2)))
//                .body("results[1].title", either(containsString(drink1))
//                        .or(containsString(drink2)))
                .when()
                .get(urlComplexSearch)
                //.prettyPeek()
                .then()
                .spec(responseSpecificationGet);
    }

    @Test
    void getVegetarianWithMinProtein50FillIngredients() {
        given()

                .spec(requestSpecificationGet)
                .queryParam("minProtein", 51)
                .queryParam("diet", "vegetarian")
                .queryParam("fillIngredients", "true")
                .expect()
                .body("totalResults", equalTo(1))
                .body("results[0].title", equalTo("Chia Yogurt Apricot Bowl"))
                .body("results[0].missedIngredients[0].amount", equalTo(0.25F))
                .when()
                .get(urlComplexSearch)
                .then()
                .spec(responseSpecificationGet);
    }

    @Test
    void postFalafelBurger(){
        given()
                .spec(requestSpecificationPost)
                .formParam("title", "Falafel Burger")
                .expect()
                .body("cuisine", equalTo("Middle Eastern"))
                .body("confidence", equalTo(0.85F))
                .header("Content-Type", "application/json")
                .when()
                .post(urlrecipesCuisine)
                //.prettyPeek()
                .then()
                .spec(responseSpecificationGet);

    }

    @Test
    void postThaiPastaSalad(){

        given()
                .spec(requestSpecificationPost)
                .formParam("title", "Thai Pasta Salad")
                .expect()
                .body( "cuisines", hasItems(properties.getProperty("cuisine1"), properties.getProperty("cuisine2") ))
                .body("confidence", equalTo(0.85F))
                .header("Content-Type", "application/json")
                .when()
                .post(urlrecipesCuisine)
                //.prettyPeek()
                .then()
                .spec(responseSpecificationGet);

    }

    @Test
    void postJensSwedishMeatballs(){

        given()
                .spec(requestSpecificationPost)
                .formParam("title", "Jen's Swedish Meatballs")
                .expect()
                .body( "cuisines", hasItems(properties.getProperty("cuisine3"),
                        properties.getProperty("cuisine4"), properties.getProperty("cuisine5") ))
                .body("confidence", equalTo(0.85F))
                //.body("results[0].missedIngredients[0].amount", equalTo(0.25F))
                .header("Content-Type", "application/json")
                .when()
                .post(urlrecipesCuisine)
                //.prettyPeek()
                .then()
                .spec(responseSpecificationGet);
    }

    @Test
    void postMangoFriedRice(){

        given()
                .spec(requestSpecificationPost)
                .formParam("title", "Mango Fried Rice")
                .expect()
                .body( "cuisines", hasItems(properties.getProperty("cuisine1"), properties.getProperty("cuisine6") ))
                .body("confidence", equalTo(0.85F))
                .header("Content-Type", "application/json")
                .when()
                .post(urlrecipesCuisine)
                //.prettyPeek()
                .then()
                .spec(responseSpecificationGet);
    }

    @Test
    void postAfricanChickenPeanutStew(){
        given()
                .spec(requestSpecificationPost)
                .formParam("title", "African Chicken Peanut Stew")
                .expect()
                .body("cuisine", equalTo("African"))
                .body("confidence", equalTo(0.85F))
                .when()
                .post(urlrecipesCuisine)
                //.prettyPeek()
                .then()
                .spec(responseSpecificationGet);

    }
    @Test
    void addItem1ToMealPlan(){
        id1 = given()
                .spec(requestSpecificationAdd)
                .body("{\n"
                        + " \"date\": 20220514,\n"
                        + " \"slot\": 1,\n"
                        + " \"position\": 0,\n"
                        + " \"type\": \"INGREDIENTS\",\n"
                        + " \"value\": {\n"
                        + " \"ingredients\": [\n"
                        + " {\n"
                        + " \"name\": \"1 banana\",\n"
                        + " \"name\": \"1 potato\"\n"
                        + " }\n"
                        + " ]\n"
                        + " }\n"
                        + "}")
                .when()
                .post(urlAddItemsToMealPlanner)
                //prettyPeek()
                .then()
                .spec(responseSpecificationPost)
                //.statusCode(200)
                .extract()
                .jsonPath()
                .get("id")
                .toString();
    }

    @Test
    void addItem2ToMealPlan(){
        id2 = given()
                .spec(requestSpecificationAdd)
                .body("{\n"
                        + " \"date\": 20220514,\n"
                        + " \"slot\": 1,\n"
                        + " \"position\": 0,\n"
                        + " \"type\": \"INGREDIENTS\",\n"
                        + " \"value\": {\n"
                        + " \"ingredients\": [\n"
                        + " {\n"
                        + " \"name\": \"1 potato\"\n"
                        + " }\n"
                        + " ]\n"
                        + " }\n"
                        + "}")
                .when()
                .post(urlAddItemsToMealPlanner)
                //.prettyPeek()
                .then()
                .spec(responseSpecificationPost)
                .extract()
                .jsonPath()
                .get("id")
                .toString();
    }

    @AfterAll
    static void tearDown1() {
        given()
                .spec(requestSpecificationAdd)
                .body(
                        "{\n"
                                + " \"username\":" + (properties.getProperty("userName")) + ",\n"
                                + " \"id\":" + id1 + ",\n"
                                + " \"hash\":" + properties.getProperty("hash") + ",\n"
                                + "}"
                )
                .delete(urlAddItemsToMealPlanner + "/" + id1)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
    @AfterAll
    static void tearDown2() {
        given()
                .spec(requestSpecificationAdd)
                .body(
                        "{\n"
                                + " \"username\":" + (properties.getProperty("userName")) + ",\n"
                                + " \"id\":" + id2 + ",\n"
                                + " \"hash\":" + properties.getProperty("hash") + ",\n"
                                + "}"
                )
                .delete(urlAddItemsToMealPlanner + "/" + id2)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
    @AfterAll
    static void end() {
        System.out.println("I'l be back!");
    }

}
