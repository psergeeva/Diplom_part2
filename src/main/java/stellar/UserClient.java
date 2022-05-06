package stellar;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;

public class UserClient extends StellarRestClient {

    private static final String REGISTRATION_PATH = "api/auth/register/";
    private static final String LOGIN_PATH = "api/auth/login/";
    private static final String CHANGE_USER_DATA_PATH = "api/auth/user/";

    @Step("User registration")
    public ValidatableResponse register(User user) {

        return given()
        .spec(getBaseSpec())
        .body(user)
        .post(REGISTRATION_PATH)
        .then();
    }

    @Step("User login")
    public ValidatableResponse login(User user) {

        return given()
                .spec(getBaseSpec())
                .body(user)
                .post(LOGIN_PATH)
                .then();
    }

    @Step("Update data for user with authorization")
    public ValidatableResponse updateDataWithAuth(User loginData, User updateData) {

        String bearerToken = given().
                spec(getBaseSpec()).
                body(loginData).
                post(LOGIN_PATH).
                then().
                extract().
                path("accessToken");

        return given()
                .header("Authorization", bearerToken)
                .spec(getBaseSpec())
                .body(updateData)
                .patch(CHANGE_USER_DATA_PATH)
                .then();
    }

    @Step("Update data for user without authorization")
    public ValidatableResponse updateDataWithoutAuth(User updateData) {

        return given()
                .spec(getBaseSpec())
                .body(updateData)
                .patch(CHANGE_USER_DATA_PATH)
                .then();
    }

    @Step("User removal")
    public ValidatableResponse remove(String bearerToken) {

        return given()
                .header("Authorization", bearerToken)
                .spec(getBaseSpec())
                .delete(CHANGE_USER_DATA_PATH)
                .then();
    }
}