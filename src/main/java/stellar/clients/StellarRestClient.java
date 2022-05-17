package stellar.clients;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import stellar.EndPoints;

import static io.restassured.http.ContentType.JSON;

public class StellarRestClient {

    protected static RequestSpecification getBaseSpec(){
        return new RequestSpecBuilder ()
                .setContentType (JSON)
                .setBaseUri (EndPoints.BASE_URI)
                .build();

    }
}