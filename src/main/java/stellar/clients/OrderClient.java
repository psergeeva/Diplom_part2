package stellar.clients;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;
import stellar.EndPoints;
import stellar.Ingredients;

import static io.restassured.RestAssured.given;

public class OrderClient extends StellarRestClient {

    @Step("Create order for authorized user")
    public ValidatableResponse createOrderWithAuth(String authToken, Ingredients ingredients) {
        return given()
                .filter(new AllureRestAssured ())
                .spec(getBaseSpec())
                .and()
                .headers("Authorization", authToken)
                .body(ingredients)
                .post(EndPoints.ORDER)
                .then();
    }

    @Step("Create order for unauthorized user")
    public ValidatableResponse createOrderWithNoAuth(Ingredients ingredients) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .body(ingredients)
                .post(EndPoints.ORDER)
                .then();
    }

    @Step("Create order without ingredients for unauthorized user")
    public ValidatableResponse createOrderWithoutIngredientsNoAuth() {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .post(EndPoints.ORDER)
                .then();
    }

    @Step("Get user orders (user is authorized)")
    public ValidatableResponse getUserOrdersAuth(String authToken) {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .headers("Authorization", authToken)
                .get(EndPoints.ORDER)
                .then();
    }

    @Step("Get user orders (user is not authorized)")
    public ValidatableResponse getUserOrdersNoAuth() {
        return given()
                .filter(new AllureRestAssured())
                .spec(getBaseSpec())
                .and()
                .get(EndPoints.ORDER)
                .then();
    }

    @Step("Get ingredients")
    public static ValidatableResponse getIngredientsData() {
        return given()
                .filter(new AllureRestAssured ())
                .spec(getBaseSpec())
                .get(EndPoints.INGREDIENTS_DATA)
                .then();
    }
}
