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

public class CreateUsersTests {
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
    @DisplayName("Создание уникального пользователя") // имя теста
    @Description("Выполнили пост запрос на создание уникального пользователя") // описание теста
    @Step("Создать уникального пользователя")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void createUserUniqueTest() {
        shouldDeleteUser = false;
        shouldGenerateTestUser = false;//запрещаем создавать уникального пользователя
        testUser = GeneratorsAndSetup.createRandomUser();
        accessToken = dataTests.registerAndGetAccessToken(testUser);
    }

    @Test
    @DisplayName("Создание пользователя который уже зарегистрирован") // имя теста
    @Description("Здесь мы сначала создаем обычного пользователя а потом пытаемся создать его повторно") // описание теста
    @Step("Создать пользователя, который уже зарегистрирован")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void createUserRegisteredTest() {

        Response createUserRegistered =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(testUser)
                        .when()
                        .post(GeneratorsAndSetup.REGISTER_API);
        createUserRegistered.then()
                .assertThat()
                .statusCode(403);
        assertThat("Проверка что повторно нельзя создать пользователя который уже зарегистрирован"
                ,createUserRegistered.getBody().asString(),
                CoreMatchers.containsString("User already exists")); //Здесь проверили сообщение что пользователь уже был зарегестрирован
    }

    @Test
    @DisplayName("Создание пользователя без заполнения поля пароль") // имя теста
    @Description("Здесь мы создаем пользователя без обязательного поля пароль") // описание теста
    @Step("Создать пользователя без обязательного поля пароль")
    @Owner("Vladimir Safonov_QA_Automation_Engeener_Group45_Yandex_Praktikum")
    public void createUserWithoutPasswordTest() {
        shouldGenerateTestUser = false;//запрещаем создавать уникального пользователя
        shouldDeleteUser = false; //запрещаем удалять уникального пользователя
        CreateUser createUserWithoutPassword = GeneratorsAndSetup.createUserWithoutPassword();
        Response createUserWithoutPass =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(createUserWithoutPassword)
                        .when()
                        .post(GeneratorsAndSetup.REGISTER_API);
        createUserWithoutPass.then()
                .assertThat()
                .statusCode(403);
        assertThat("Проверка если создать пользователя без обязательного поля пароль"
                ,createUserWithoutPass.getBody().asString(),
                CoreMatchers.containsString("Email, password and name are required fields")); //Здесь проверили сообщение что отсутствует обязательное поле
    }
}
