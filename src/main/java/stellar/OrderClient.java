package stellar;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends StellarRestClient{

    private static final String ORDER_PATH = "/api/orders/";

    @Step("Create Order with auth")
    public ValidatableResponse createOrderWithAuth(Order order, String token) {

        return given()
                .header("Authorization", token)
                .spec(getBaseSpec())
                .body(order)
                .post(ORDER_PATH)
                .then();
    }

    @Step("Create Order without auth")
    public ValidatableResponse createOrderWithoutAuth(Order order) {

        return given()
                .spec(getBaseSpec())
                .body(order)
                .post(ORDER_PATH)
                .then();
    }

    @Step("Get the user's orders with auth")
    public ValidatableResponse getUserOrdersWithAuth(String token) {

        return given()
                .header("Authorization", token)
                .spec(getBaseSpec())
                .get(ORDER_PATH)
                .then();
    }

    @Step("Get the user's orders without auth")
    public ValidatableResponse getUserOrdersWithoutAuth() {

        return given()
                .spec(getBaseSpec())
                .get(ORDER_PATH)
                .then();
    }
}
