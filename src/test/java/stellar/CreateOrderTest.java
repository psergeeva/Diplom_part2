package stellar;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class CreateOrderTest {

    private final String EMAIL = "test_polina@test.com";
    private final String PASSWORD = "Test1234";

    private final String[] INGREDIENT = {"61c0c5a71d1f82001bdaaa6d"};
    private final String[] EMPTY_INGREDIENT = {};
    private final String[] INVALID_INGREDIENT = {"6"};

    User loginForToken = new User (EMAIL, PASSWORD);
    Order order = new Order (INGREDIENT);
    Order emptyOrder = new Order (EMPTY_INGREDIENT);
    Order invalidOrder = new Order (INVALID_INGREDIENT);
    OrderClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient ();
    }

    @Test
    @DisplayName("Check order creation for authorized user")
    public void checkOrderCreationForAuthorizedUser() {
        UserClient userClient = new UserClient ();
        String token = userClient.login (loginForToken).extract ().path ("accessToken");
        ValidatableResponse response = orderClient.createOrderWithAuth (order, token);
        response.statusCode (200).and ().assertThat ().body ("success", is (true));
    }

    @Test
    @DisplayName("Check order creation for unauthorized user")
    public void checkOrderCreationForUnauthorizedUser() {
        ValidatableResponse response = orderClient.createOrderWithoutAuth (order);
        response.statusCode (200).assertThat ().body ("success", is (true));
    }

    @Test
    @DisplayName("Check order creation for authorized user with empty ingredient")
    public void checkOrderCreationWithEmptyIngredient() {
        UserClient userClient = new UserClient ();
        String token = userClient.login (loginForToken).extract ().path ("accessToken");
        ValidatableResponse response = orderClient.createOrderWithAuth (emptyOrder, token);
        response.statusCode (400).and ().assertThat ().body ("message", is ("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Check order creation for authorized user with invalid ingredient")
    public void checkOrderCreationWithInvalidIngredient() {
        UserClient userClient = new UserClient ();
        String token = userClient.login (loginForToken).extract ().path ("accessToken");
        ValidatableResponse response = orderClient.createOrderWithAuth (invalidOrder, token);
        response.statusCode (500);
    }

}
