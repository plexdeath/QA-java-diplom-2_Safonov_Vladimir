import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class DataTests {
    private static final String INGREDIENTS_API = "https://stellarburgers.nomoreparties.site/api/ingredients";
    @Step("Добавить пользователя и вернуть accessToken")
    public String registerAndGetAccessToken(CreateUser user) { //здесь мы регистрируемся и получаем accessToken
        Response registerResponse = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(GeneratorsAndSetup.REGISTER_API);
        registerResponse.then().statusCode(200);
                              assertThat("Проверка что уникальный пользователь успешно создался"
                              ,registerResponse.getBody().asString(),
                                     CoreMatchers.containsString(user.getName()));
        return registerResponse.then()
                .extract()
                .path("accessToken");
    }
    @Step("Удалить пользователя по accessToken")
    public void deleteUser(String accessToken) { //Это метод удаления пользователя на вход принимает accessToken
        Response deleteResponse = given()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .delete(GeneratorsAndSetup.USER_API);
        deleteResponse.then().statusCode(202);
        assertThat(deleteResponse.getBody().asString(), containsString("User successfully removed"));
        }
    @Step("Авторизоваться")
    public Response loginUser(CreateUser user) { //Авторизоваться
        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(GeneratorsAndSetup.LOGIN_API);
        loginResponse.then().statusCode(200);
        assertThat("Проверка, что пользователь авторизовался",
                loginResponse.getBody().asString(),
                CoreMatchers.containsString(user.getName()));
        return loginResponse;
    }

        @Step("Получить первые два ингредиента по _id")
        public List<String> getFirstTwoIngredientIds() {
            Response response = given()
                    .header("Content-type", "application/json")
                    .when()
                    .get(INGREDIENTS_API);
            response.then().statusCode(200);
            List<String> allIds = response.jsonPath().getList("data._id", String.class);
            return allIds.subList(0, Math.min(2, allIds.size()));
        }
    }


