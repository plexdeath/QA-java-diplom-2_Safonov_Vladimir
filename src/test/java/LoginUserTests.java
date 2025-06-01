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

public class LoginUserTests  {
    private String accessToken;
    private CreateUser testUser;
    private boolean shouldDeleteUser = true;
    private boolean shouldGenerateTestUser = true;
    DataTests dataTests = new DataTests();

    @Before
    public void setUp() {
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
    @DisplayName("логин под существующим пользователем") // имя теста
    @Description("Залогинится существующим пользователем") // описание теста
    @Step("логин под существующим пользователем")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void loginUniqueUser() {
        dataTests.loginUser(testUser);
    }

    @Test
    @DisplayName("логин с неверным логином и паролем") // имя теста
    @Description("Залогинится с неверным логином и паролем") // описание теста
    @Step("Залогинится с неверным логином и паролем")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void loginWrongEmailSAndPassword() {
        testUser = GeneratorsAndSetup.createRandomUser();
        Response loginWrongEmailSAndPassword =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(testUser)
                        .when()
                        .post(GeneratorsAndSetup.LOGIN_API);
        loginWrongEmailSAndPassword.then()
                .assertThat()
                .statusCode(401);
        assertThat("Проверка что email и пароль не правильные"
                ,loginWrongEmailSAndPassword.getBody().asString(),
                CoreMatchers.containsString("email or password are incorrect"));
    }
}
