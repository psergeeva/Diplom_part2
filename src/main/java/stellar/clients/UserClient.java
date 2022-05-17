package stellar.clients;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;
import stellar.EndPoints;
import stellar.User;
import stellar.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserClient extends StellarRestClient {

    @Step("User registration")
    public ValidatableResponse register(User user) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .body(user)
                .when()
                .post(EndPoints.REGISTER_USER)
                .then();
    }

    @Step("User login")
    public ValidatableResponse login(UserCredentials credentials) {
        return given()
                .filter(new AllureRestAssured ())
                .spec(getBaseSpec())
                .and()
                .body(credentials)
                .when()
                .post(EndPoints.LOGIN_USER)
                .then();
    }

    @Step("Update data for user with authorization")
    public ValidatableResponse updateUserDataAuth(String authToken, User user) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .headers("Authorization", authToken)
                .body(user)
                .patch(EndPoints.USER)
                .then();
    }

    @Step("Get user data")
    public ValidatableResponse getUserData (String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .headers("Authorization", authToken)
                .get(EndPoints.USER)
                .then();
    }

    @Step("Delete user")
    public ValidatableResponse deleteUser (String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .headers("Authorization", authToken)
                .delete(EndPoints.USER)
                .then();
    }
}