package stellar;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

public class GetUserOrdersTests {

    private final String EMAIL = "test_polina@test.com";
    private final String PASSWORD = "Test1234";

    User loginForToken = new User (EMAIL, PASSWORD);
    OrderClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient ();
    }

    @Test
    @DisplayName("Check getting orders for authorized user")
    public void getOrdersForAuthorizedUser() {
        UserClient userClient = new UserClient ();
        String token = userClient.login (loginForToken).extract ().path ("accessToken");
        ValidatableResponse response = orderClient.getUserOrdersWithAuth (token);
        response.statusCode (200).and ().assertThat ().body ("success", CoreMatchers.is (true));
    }

    @Test
    @DisplayName("CHeck that there is no option to get orders without authorization")
    public void getOrdersForUnauthorizedUser() {
        ValidatableResponse response = orderClient.getUserOrdersWithoutAuth ();
        response.statusCode (401).and ().assertThat ().body ("message", CoreMatchers.is ("You should be authorised"));
    }

}
