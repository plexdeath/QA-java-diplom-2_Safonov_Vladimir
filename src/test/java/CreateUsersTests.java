import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateUsersTests {
    private final String password = "N@GorshkeSidelK@rol" + java.util.UUID.randomUUID(); //генерируем рандомного курьера
    private final String email = java.util.UUID.randomUUID()+"@yandex.ru";//генерируем пароль
    private final String name = email;;//генерируем имя пользователя
    private final String api = "/api/auth/register";//апи
    CreateUser createUser = new CreateUser(email, password, name);

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Создание уникального пользователя") // имя теста
    @Description("Выполнили пост запрос на создание уникального пользователя") // описание теста
    @Step("Создать уникального пользователя")
    public void createUserUniqueTest() {
        Response createUserUnique = //Позитивный сценарий
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(createUser)
                        .when()
                        .post(api);
                      createUserUnique.then()
                     .assertThat()
                     .statusCode(200);
                      assertThat("Проверка что уникальный пользователь успешно создался"
                              ,createUserUnique.getBody().asString(),
                                     CoreMatchers.containsString("success"));

    }





}
