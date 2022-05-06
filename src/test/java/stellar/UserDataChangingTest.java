package stellar;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;

public class UserDataChangingTest {

    private final String INITIAL_NAME = "polina";
    private final String INITIAL_EMAIL = "test_polina@test.com";
    private final String INITIAL_PASSWORD = "Test1234";

    private final String NEW_NAME = "newPolina";
    private final String NEW_EMAIL = "new_test_polina@test.com";
    private final String NEW_PASSWORD = "NewTest1234";

    UserClient userClient;
    User user = new User (INITIAL_NAME, INITIAL_EMAIL, INITIAL_PASSWORD);
    User loginWithInitialUser = new User (INITIAL_EMAIL, INITIAL_PASSWORD);
    User newUser = new User (NEW_NAME, NEW_EMAIL, NEW_PASSWORD);
    User loginWithNewUser = new User (NEW_EMAIL, NEW_PASSWORD);

    @Before
    public void setUp() {
        userClient = new UserClient ();
    }

    @Test
    @DisplayName("Check user data can be updated when user is authorized")
    public void updateUserDataWithAuthorization() {
        ValidatableResponse response = userClient.updateDataWithAuth (loginWithInitialUser, newUser);
        response.statusCode (200).and ().assertThat ().body ("success", is (true));
        userClient.updateDataWithAuth (loginWithNewUser, user);
    }

    @Test
    @DisplayName("Check user data cannot be updated when user is not authorized")
    public void updateUserDataWithoutAuthorization() {
        ValidatableResponse response = userClient.updateDataWithoutAuth (newUser);
        response.statusCode (401).and ().assertThat ().body ("message", is ("You should be authorised"));
    }

}