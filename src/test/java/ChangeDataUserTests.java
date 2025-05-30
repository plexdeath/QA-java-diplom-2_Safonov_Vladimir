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
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
public class ChangeDataUserTests {
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
    @DisplayName("Изменение данных пользователя с авторизацией") // имя теста
    @Description("Изменяем любые пользовательские поля с авторизацией ") // описание теста
    @Step("Изменить данные пользователя с авторизацией")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void changeUserWithAutorizationTest() {
        Response updateResponse =
                given()
                        .header("Content-type", "application/json")
                        .header("authorization", accessToken)
                        .body(GeneratorsAndSetup.createUpdatedUserData()) //передаем коллекцию
                        .when()
                        .patch(GeneratorsAndSetup.USER_API);
        updateResponse.then()
                .assertThat()
                .statusCode(200);
        assertThat("Проверить что запрос на изменение данных выполнился успешно"
                ,updateResponse.getBody().asString(),
                CoreMatchers.containsString(GeneratorsAndSetup.generatedEmail)); //проверяем что ответ содержит наш новый сгенерированный email
    }
    @Test
    @DisplayName("Изменение данных пользователя без авторизации") // имя теста
    @Description("Изменяем любые пользовательские поля без авторизации ") // описание теста
    @Step("Изменить данные пользователя без авторизации")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void changeUserWithoutAutorizationTest() {
        Response updateResponse =
                given()
                        .header("Content-type", "application/json")
                        .body(GeneratorsAndSetup.createUpdatedUserData()) //передаем коллекцию
                        .when()
                        .patch(GeneratorsAndSetup.USER_API);
        updateResponse.then()
                .assertThat()
                .statusCode(401);
        assertThat("Проверить что без авторизации не изменить данные"
                ,updateResponse.getBody().asString(),
                CoreMatchers.containsString("You should be authorised")); //Проверить что без авторизации не изменить данные
    }
}
