package stellar;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;

public class UserCreationTest {

    private final String NAME = RandomStringUtils.randomAlphabetic (10);
    private final String EMAIL = RandomStringUtils.randomAlphabetic (10) + "@test.com";
    private final String PASSWORD = RandomStringUtils.randomAlphabetic (10);

    UserClient userClient;
    User register = new User (NAME, EMAIL, PASSWORD);
    User registerWithoutName = new User ("", EMAIL, PASSWORD);
    User registerWithoutEmail = new User (NAME, "", PASSWORD);
    User registerWithoutPassword = new User (NAME, EMAIL, "");

    @Before
    public void setUp() {
        userClient = new UserClient ();
    }

    @Test
    @DisplayName("User registration with valid data")
    public void testCorrectRegistration() {
        ValidatableResponse response = userClient.register (register);
        response.statusCode (200).and ().assertThat ().body ("success", is (true));
        String token = response.extract ().path ("accessToken");
        userClient.remove (token);
    }

    @Test
    @DisplayName("Check that only unique user can be registered")
    public void testRegistrationNonUniqueUser() {
        userClient.register (register);
        ValidatableResponse response = userClient.register (register);
        response.statusCode (403).and ().assertThat ().body ("message", is ("User already exists"));
    }

    @Test
    @DisplayName("Check user registration without name")
    public void checkRegistrationOfUserWithoutName() {
        ValidatableResponse response = userClient.register (registerWithoutName);
        response.statusCode (403).and ().assertThat ().body ("message", is ("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check user registration without email")
    public void checkRegistrationOfUserWithoutEmail() {
        ValidatableResponse response = userClient.register (registerWithoutEmail);
        response.statusCode (403).and ().assertThat ().body ("message", is ("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check user registration without password")
    public void checkRegistrationOfUserWithoutPassword() {
        ValidatableResponse response = userClient.register (registerWithoutPassword);
        response.statusCode (403).and ().assertThat ().body ("message", is ("Email, password and name are required fields"));
    }

}
