package stellar;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import stellar.clients.UserClient;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;

public class ChangeUserDataTest {

    private UserClient userClient;
    private String authToken;

    @Before
    @Step("setUp")
    public void setUp() {
        userClient = new UserClient ();
        RestAssured.filters (new RequestLoggingFilter (), new ResponseLoggingFilter ());
    }

    @Test
    @DisplayName("Check user data can be updated when user is authorized")
    public void updateUserDataWithAuthorization() {
        User user = User.getRandom ();
        userClient.register (user);
        ValidatableResponse responseLoginUser = userClient.login (UserCredentials.getUserCredentials (user));
        authToken = responseLoginUser.extract ().path ("accessToken");

        userClient.updateUserDataAuth (authToken, User.getRandom ());
        ValidatableResponse responseUserData = userClient.getUserData (authToken);

        assertThat (responseUserData.extract ().statusCode (), equalTo (SC_OK));
        assertTrue (responseUserData.extract ().path ("success"));
        assertNotEquals (user.email, responseUserData.extract ().path ("user.email"));
        assertNotEquals (user.name, responseUserData.extract ().path ("user.name"));
    }

    @Test
    @DisplayName("Check user data cannot be updated when user is not authorized")
    public void updateUserDataWithoutAuthorization() {
        String wrongAuthToken = "1";
        ValidatableResponse responseChangeUserData = userClient.updateUserDataAuth (wrongAuthToken, User.getRandom ());

        assertThat (responseChangeUserData.extract ().statusCode (), equalTo (SC_UNAUTHORIZED));
        assertFalse (responseChangeUserData.extract ().path ("success"));
        assertEquals ("You should be authorised", responseChangeUserData.extract ().path ("message"));
    }
}