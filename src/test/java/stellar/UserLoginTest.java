package stellar;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;

public class UserLoginTest {

    private final String EMAIL = "test_polina@test.com";
    private final String PASSWORD = "Test1234";

    UserClient userClient;
    User login = new User (EMAIL, PASSWORD);
    User loginWithInvalidPassword = new User (EMAIL, "12345");
    User loginWithInvalidEmail = new User ("1@test.com", PASSWORD);

    @Before
    public void setUp() {
        userClient = new UserClient ();
    }

    @Test
    @DisplayName("Check that user can login with valid credentials")
    public void checkCorrectLogin() {
        ValidatableResponse validatableResponse = userClient.login (login);
        validatableResponse.statusCode (200).and ().assertThat ().body ("success", is (true));
    }

    @Test
    @DisplayName("Check that user has no opportunity to log in with invalid email")
    public void userCannotLoginWithInvalidEmail() {
        ValidatableResponse validatableResponse = userClient.login (loginWithInvalidEmail);
        validatableResponse.statusCode (401).and ().assertThat ().body ("message", is ("email or password are incorrect"));
    }

    @Test
    @DisplayName("Check that user has no opportunity to log in with incorrect password")
    public void userCannotLoginWithInvalidPassword() {
        ValidatableResponse validatableResponse = userClient.login (loginWithInvalidPassword);
        validatableResponse.statusCode (401).and ().assertThat ().body ("message", is ("email or password are incorrect"));
    }

}

