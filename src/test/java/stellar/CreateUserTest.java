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

public class CreateUserTest {

    private UserClient userClient;
    private String authToken;

    @Before
    @Step("setUp")
    public void setUp() {
        userClient = new UserClient ();
        RestAssured.filters(new RequestLoggingFilter (), new ResponseLoggingFilter ());
    }

    @After
    @Step("Delete user")
    public void tearDown() {
        if (authToken != null) {
            userClient.deleteUser(authToken);
        }
    }

    @Test
    @DisplayName("User registration with valid data")
    public void testCorrectRegistration() {
        User user = User.getRandom();
        ValidatableResponse response = userClient.register(user);
        response.statusCode (200).and ().assertThat ().body ("success", is (true));
        String token = response.extract ().path ("accessToken");
    }

    @Test
    @DisplayName("Check that only unique user can be registered")
    public void testRegistrationNonUniqueUser() {
        User user = User.getRandom();
        userClient.register (user);
        ValidatableResponse response = userClient.register (user);
        response.statusCode (403).and ().assertThat ().body ("message", is ("User already exists"));
    }

    @Test
    @DisplayName("Check user registration without name")
    public void checkRegistrationOfUserWithoutName() {
        User user = User.getRandomWithoutName();
        ValidatableResponse response = userClient.register (user);
        response.statusCode (403).and ().assertThat ().body ("message", is ("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check user registration without email")
    public void checkRegistrationOfUserWithoutEmail() {
        User user = User.getRandomWithoutEmail ();
        ValidatableResponse response = userClient.register (user);
        response.statusCode (403).and ().assertThat ().body ("message", is ("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check user registration without password")
    public void checkRegistrationOfUserWithoutPassword() {
        User user = User.getRandomWithoutPassword ();
        ValidatableResponse response = userClient.register (user);
        response.statusCode (403).and ().assertThat ().body ("message", is ("Email, password and name are required fields"));
    }
}
