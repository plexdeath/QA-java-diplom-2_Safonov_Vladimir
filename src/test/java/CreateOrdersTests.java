import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateOrdersTests {
    private String accessToken;
    private CreateUser testUser;
    private boolean shouldDeleteUser = true;
    private boolean shouldGenerateTestUser = true;
    DataTests dataTests = new DataTests();

    @Before
    public void setUpAndTests() {
        RestAssured.baseURI = GeneratorsAndSetup.BASE_URI;
        if (shouldGenerateTestUser) {
            testUser = GeneratorsAndSetup.createRandomUser();
            accessToken = dataTests.registerAndGetAccessToken(testUser);
        }
    }

    @After
    public void cleanData() {
        if (shouldDeleteUser) {
            dataTests.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией") // имя теста
    @Description("Создаем заказ с авторизацией ") // описание теста
    @Step("Создаем заказ с пустым телом с авторизацией")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void createOrderWithAutorization() {
        shouldGenerateTestUser = true;
        shouldDeleteUser = true;
        dataTests.loginUser(testUser);
        Response createOrderWithAutorization =
                given()
                        .header("Content-type", "application/json")
                        .header("authorization", accessToken)
                        .post(GeneratorsAndSetup.ORDER_API);
        createOrderWithAutorization.then()
                .assertThat()
                .statusCode(400);
        assertThat("Проверить что запрос на изменение данных выполнился успешно"
                ,createOrderWithAutorization.getBody().asString(),
                CoreMatchers.containsString("Ingredient ids must be provided")); //проверяем ошибку о добавлении ингредиентов
    }

    @Test
    @DisplayName("Создание заказа без авторизации") // имя теста
    @Description("Создаем заказ без авторизации ") // описание теста
    @Step("Создать заказ без авторизации")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void createOrderWithoutAutorization() {
        shouldGenerateTestUser = true;
        shouldDeleteUser = true;
        Response createOrderWithoutAutorization =
                given()
                        .header("Content-type", "application/json")
                        .post(GeneratorsAndSetup.ORDER_API);
        createOrderWithoutAutorization.then()
                .assertThat()
                .statusCode(400);
        assertThat("Проверить что запрос на изменение данных выполнился успешно"
                ,createOrderWithoutAutorization.getBody().asString(),
                CoreMatchers.containsString("Ingredient ids must be provided")); //проверяем ошибку о добавлении ингредиентов
    }
    @Test
    @DisplayName("Создание заказа c ингредиентами") // имя теста
    @Description("Создаем заказ с ингредиентами") // описание теста
    @Step("Создать заказ с ингредиентами")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void createOrderWithIngredients() {
        shouldGenerateTestUser = true;
        shouldDeleteUser = true;
        List<String> firstTwoIds = dataTests.getFirstTwoIngredientIds();
        CreateOrder createOrderWithIngredient = new CreateOrder(firstTwoIds);
        Response createOrderWithIngredients =
                given()
                        .header("Content-type", "application/json")
                        .header("authorization", accessToken)
                        .body(createOrderWithIngredient)
                        .post(GeneratorsAndSetup.ORDER_API);
        createOrderWithIngredients.then()
               .assertThat()
              .statusCode(200);
        createOrderWithIngredients.then().body("order.ingredients.size()", equalTo(2)); //Проверяем что в тело вернулось 2 id ингредиента который мы передали
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов") // имя теста
    @Description("Создаем заказ без ингредиентов ") // описание теста
    @Step("Создаем заказ без ингредиентов")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void createOrderWithoutIngredients() {
        shouldGenerateTestUser = true;
        shouldDeleteUser = true;
        dataTests.loginUser(testUser);
        Response createOrderWithoutIngredients =
                given()
                        .header("Content-type", "application/json")
                        .header("authorization", accessToken)
                        .post(GeneratorsAndSetup.ORDER_API);
        createOrderWithoutIngredients.then()
                .assertThat()
                .statusCode(400);
        assertThat("Проверить что запрос на изменение данных выполнился успешно"
                ,createOrderWithoutIngredients.getBody().asString(),
                CoreMatchers.containsString("Ingredient ids must be provided")); //проверяем ошибку о добавлении ингредиентов
    }

    @Test
    @DisplayName("Создание заказа c неверным хешем ингредиентов") // имя теста
    @Description("Создаем заказ с неверным хешем ингредиентов") // описание теста
    @Step("Создать заказ с неверным хешем ингредиентов")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void createOrderWithWrongHashIngredients() {
        shouldGenerateTestUser = true;
        shouldDeleteUser = true;
        List<String> ingredientIds = Arrays.asList("999999999999999999999999", "777777777777777777777777"); // неправильные id
        CreateOrder createOrderWithWrongHashIngredients = new CreateOrder(ingredientIds);
        Response createOrderWrongHash =
                given()
                        .header("Content-type", "application/json")
                        .header("authorization", accessToken)
                        .body(createOrderWithWrongHashIngredients)
                        .post(GeneratorsAndSetup.ORDER_API);
        createOrderWrongHash.then()
                .assertThat()
                .statusCode(400);
        assertThat("Проверяем ошибку после добавления неверных хэшей"
                ,createOrderWrongHash.getBody().asString(),
                CoreMatchers.containsString("One or more ids provided are incorrect")); //проверяем ошибку после добавления неверных хэшей
    }

    @Test
    @DisplayName("Получить список заказов конкретного пользователя который авторизован") // имя теста
    @Description("Получаем список заказов конкретного пользователя который авторизован") // описание теста
    @Step("Получить список заказов конкретного пользователя который авторизован")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void getOrderUserAuthorize() {
        shouldGenerateTestUser = true;
        shouldDeleteUser = true;
        dataTests.loginUser(testUser);
        List<String> firstTwoIds = dataTests.getFirstTwoIngredientIds();
        CreateOrder getOrderUserAuthorize = new CreateOrder(firstTwoIds);
                given()
                        .header("Content-type", "application/json")
                        .header("authorization", accessToken)
                        .body(getOrderUserAuthorize)
                        .post(GeneratorsAndSetup.ORDER_API);
        Response getOrderUserAuthorizeResponse =
        given()
                .header("Content-type", "application/json")
                .header("authorization", accessToken)
                .get(GeneratorsAndSetup.ORDER_API);
        getOrderUserAuthorizeResponse.then()
                .assertThat()
                .statusCode(200)
                .body("orders._id", everyItem(notNullValue())); // проверяем что наши _id не пустые

    }
    @Test
    @DisplayName("Получить список заказов конкретного пользователя который не авторизован") // имя теста
    @Description("Получаем список заказов конкретного пользователя который не авторизован") // описание теста
    @Step("Получить список заказов конкретного пользователя который не авторизован")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void getOrderUserNoAuthorize() {
        shouldGenerateTestUser = true;
        shouldDeleteUser = true;
        List<String> firstTwoIds = dataTests.getFirstTwoIngredientIds();
        CreateOrder getOrderUserNoAuthorize = new CreateOrder(firstTwoIds);
        given()
                .header("Content-type", "application/json")
                .header("authorization", accessToken)
                .body(getOrderUserNoAuthorize)
                .post(GeneratorsAndSetup.ORDER_API);
        Response getOrderUserNoAuthorizeResponse =
                given()
                        .header("Content-type", "application/json")
                        .get(GeneratorsAndSetup.ORDER_API);
        getOrderUserNoAuthorizeResponse.then()
                .assertThat()
                .statusCode(401);
        assertThat("Проверка что email и пароль не правильные"
                ,getOrderUserNoAuthorizeResponse.getBody().asString(),
                CoreMatchers.containsString("You should be authorised"));//Проверяем сообщение что нельзя получить заказ без авторизации
    }
}
