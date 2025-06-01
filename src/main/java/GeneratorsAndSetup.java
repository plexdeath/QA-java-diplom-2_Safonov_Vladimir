import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class GeneratorsAndSetup {

    // Базовые настройки API
    public static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    public static final String REGISTER_API = "/api/auth/register";
    public static final String LOGIN_API = "/api/auth/login";
    public static final String USER_API = "/api/auth/user";
    public static final String ORDER_API = "/api/orders";

    // Генерация уникальных данных для регистрации
    public static final String BASE_PASSWORD = "N@GorshkeSidelK@rol";
    public static final String EMAIL_DOMAIN = "@yandex.ru";
    public static String generatedEmail;

    public static String generatePassword() {
        return BASE_PASSWORD + UUID.randomUUID();
    }

    public static String generateEmail() {
        return UUID.randomUUID().toString() + EMAIL_DOMAIN;
    }

    public static CreateUser createRandomUser() {
        String email = generateEmail();
        String password = generatePassword();
        return new CreateUser(email, password, email);
    }

    public static CreateUser createUserWithoutPassword() {
        String email = generateEmail();
        String name = email;
        return new CreateUser(email, null, name);
    }

    public static Map<String, Object> createUpdatedUserData() {
        Map<String, Object> updatedUserData = new HashMap<>();
        generatedEmail = generateEmail();
        updatedUserData.put("name", generatedEmail);
        updatedUserData.put("email", generatedEmail);
        return updatedUserData;
    }





}

