package stellar;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import stellar.clients.OrderClient;
import stellar.clients.UserClient;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GetUserOrdersTests {

    private OrderClient orderClient;
    private UserClient userClient;
    private String authToken;

    @Before
    @Step("setUp")
    public void setUp () {
        orderClient = new OrderClient ();
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
    @DisplayName("Check that authorized user can get orders")
    public void getOrdersForAuthorizedUser() {
        User user = User.getRandom();
        userClient.register(user);
        ValidatableResponse responseLoginUser = userClient.login(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        ValidatableResponse responseGetIngredientsData = OrderClient.getIngredientsData();
        List<String> ingredients = responseGetIngredientsData.extract().body().jsonPath().get("data._id");

        List<String> ingredientsList1 = new ArrayList<> ();
        ingredientsList1.add(ingredients.get(0));
        List<String> ingredientsList2 = new ArrayList<>();
        ingredientsList2.add(ingredients.get(1));
        List<String> ingredientsList3 = new ArrayList<>();
        ingredientsList3.add(ingredients.get(2));

        orderClient.createOrderWithAuth (authToken, new Ingredients(ingredientsList1));
        orderClient.createOrderWithAuth (authToken, new Ingredients(ingredientsList2));
        orderClient.createOrderWithAuth (authToken, new Ingredients(ingredientsList3));

        ValidatableResponse response = orderClient.getUserOrdersAuth(authToken);

        assertThat(response.extract().statusCode(), equalTo(SC_OK));
        Assert.assertNotNull(response.extract().path("total"));
        Assert.assertNotNull(response.extract().path("totalToday"));
    }

    @Test
    @DisplayName("Check that there is no opportunity to get orders without authorization")
    public void getOrdersForUnauthorizedUser() {
        User user = User.getRandom();
        userClient.register(user);
        ValidatableResponse responseLoginUser = userClient.login(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        ValidatableResponse responseGetIngredientsData = orderClient.getIngredientsData();
        List<String> ingredients = responseGetIngredientsData.extract().body().jsonPath().get("data._id");

        List<String> ingredientsList1 = new ArrayList<>();
        ingredientsList1.add(ingredients.get(0));
        List<String> ingredientsList2 = new ArrayList<>();
        ingredientsList2.add(ingredients.get(1));

        orderClient.createOrderWithAuth (authToken, new Ingredients(ingredientsList1));
        orderClient.createOrderWithAuth (authToken, new Ingredients(ingredientsList2));

        ValidatableResponse response = orderClient.getUserOrdersNoAuth();

        assertThat(response.extract().statusCode(), equalTo(SC_UNAUTHORIZED));
        assertFalse(response.extract().path("success"));
        assertEquals("You should be authorised", response.extract().path("message"));
    }
}