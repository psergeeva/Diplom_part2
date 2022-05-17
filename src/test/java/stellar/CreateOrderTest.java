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

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class CreateOrderTest {

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
    @DisplayName("Check order creation for authorized user")
    public void checkOrderCreationForAuthorizedUser() {
        User user = User.getRandom();
        userClient.register(user);
        ValidatableResponse responseLoginUser = userClient.login(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        ValidatableResponse responseGetIngredientsData = orderClient.getIngredientsData();
        List<String> ingredients = responseGetIngredientsData.extract().body().jsonPath().get("data._id");
        List<String> ingredientsList = new ArrayList<> ();
        ingredientsList.add(ingredients.get(0));
        ingredientsList.add(ingredients.get(1));

        ValidatableResponse createOrder = orderClient.createOrderWithAuth(authToken, new Ingredients(ingredientsList));
        assertThat(createOrder.extract().statusCode(), equalTo(SC_OK));
        Assert.assertNotNull(createOrder.extract().path("order._id"));
        Assert.assertNotNull(createOrder.extract().path("order.number"));
    }

    @Test
    @DisplayName("Check order creation for unauthorized user")
    public void checkOrderCreationForUnauthorizedUser() {
        ValidatableResponse responseGetIngredientsData = orderClient.getIngredientsData();
        List<String> ingredients = responseGetIngredientsData.extract().body().jsonPath().get("data._id");

        List<String> ingredientsList = new ArrayList<>();
        ingredientsList.add(ingredients.get(0));
        ingredientsList.add(ingredients.get(1));

        ValidatableResponse response = orderClient.createOrderWithNoAuth(new Ingredients(ingredientsList));
        assertThat(response.extract().statusCode(), equalTo(SC_OK));
        assertTrue(response.extract().path("success"));
        Assert.assertNotNull(response.extract().path("name"));
        Assert.assertNotNull(response.extract().path("order.number"));
    }

    @Test
    @DisplayName("Check order creation for authorized user with empty ingredient")
    public void checkOrderCreationWithEmptyIngredient() {
        ValidatableResponse response = orderClient.createOrderWithoutIngredientsNoAuth();
        assertThat(response.extract().statusCode(), equalTo(SC_BAD_REQUEST));
        assertFalse(response.extract().path("success"));
        assertEquals("Ingredient ids must be provided", response.extract().path("message"));
    }

    @Test
    @DisplayName("Check order creation for authorized user with invalid ingredient")
    public void checkOrderCreationWithInvalidIngredient() {
        User user = User.getRandom();
        userClient.register(user);
        ValidatableResponse responseLoginUser = userClient.login(UserCredentials.getUserCredentials(user));
        authToken = responseLoginUser.extract().path("accessToken");

        List<String> ingredientsList = new ArrayList<>();
        ingredientsList.add("99c1c0071d1f82001bda0000");

        ValidatableResponse response = orderClient.createOrderWithAuth(authToken, new Ingredients(ingredientsList));
        assertThat(response.extract().statusCode(), equalTo(SC_BAD_REQUEST));
        assertFalse(response.extract().path("success"));
        assertEquals("One or more ids provided are incorrect",
                response.extract().path("message"));
    }
}