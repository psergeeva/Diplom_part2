package stellar;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellar.clients.UserClient;

import static org.hamcrest.CoreMatchers.is;

public class LoginUserTest {

    private UserClient userClient;
    private User user;
    private String authToken;

    @Before
    @Step("setUp")
    public void setUp() {
        userClient = new UserClient ();
        user = User.getRandom();
        ValidatableResponse responseRegisterUser = userClient.register (user);
        authToken = responseRegisterUser.extract().path("accessToken");
        RestAssured.filters(new RequestLoggingFilter (), new ResponseLoggingFilter ());
    }

    @After
    @Step("Delete user")
    public void tearDown() {
        if (authToken != null) {
            userClient.deleteUser (authToken);
        }
    }

    @Test
    @DisplayName("Check that user can login with valid credentials")
    public void checkCorrectLogin() {
        ValidatableResponse responseLoginUser = userClient.login(UserCredentials.getUserCredentials(user));
        responseLoginUser.statusCode (200).and ().assertThat ().body ("success", is (true));
    }

    @Test
    @DisplayName("Check that user has no opportunity to log in with invalid email")
    public void userCannotLoginWithInvalidEmail() {
        String wrongEmail = "wrongemail@test.com";
        ValidatableResponse responseLoginUser = userClient.login(new UserCredentials(wrongEmail, user.password));
        responseLoginUser.statusCode (401).and ().assertThat ().body ("message", is ("email or password are incorrect"));
    }

    @Test
    @DisplayName("Check that user has no opportunity to log in with incorrect password")
    public void userCannotLoginWithInvalidPassword() {
        String wrongPassword = "12345";
        ValidatableResponse responseLoginUser = userClient.login(new UserCredentials(user.email, wrongPassword));
        responseLoginUser.statusCode (401).and ().assertThat ().body ("message", is ("email or password are incorrect"));
    }
}
