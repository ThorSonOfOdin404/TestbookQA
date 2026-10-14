package in.prathamattri.testbookqa.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BookControllerTest {

    private static final String BASE_PATH = "/api/books";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 5000;
    }

    // GET /api/books

    @Test
    public void shouldReturn200WhenBooksAreRetrieved() {
        given()
                .when()
                .get(BASE_PATH)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", instanceOf(java.util.List.class));
    }

    @Test
    public void shouldReturnBooksAsJsonArray() {
        given()
                .when()
                .get(BASE_PATH)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", instanceOf(java.util.List.class));
    }

    // POST /api/books

    @Test
    public void shouldCreateBookSuccessfully() {
        String requestBody = """
                {
                    "title": "Clean Code",
                    "author": "Robert C. Martin",
                    "price" : 230
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(200)
                .body(equalTo("Book added successfully!"));
    }

    @Test
    public void shouldReturn400WhenRequestBodyIsMissing() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(400);
    }

    @Test
    public void shouldReturn400WhenRequestBodyIsMalformed() {
        String invalidJson = """
                {
                    "title": "Clean Code",
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidJson)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(400);
    }

    @Test
    public void shouldHandleEmptyJsonObject() {
        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(anyOf(
                        is(200),
                        is(400),
                        is(500)
                ));
    }

    @Test
    public void shouldHandleBookWithMissingFields() {
        String requestBody = """
                {
                    "title": "Test Book"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(BASE_PATH)
                .then()
                .statusCode(anyOf(
                        is(200),
                        is(400),
                        is(500)
                ));
    }

    // DELETE /api/books/{id}

    @Test
    public void shouldDeleteExistingBookSuccessfully() {
        // Assumes book ID 1 exists and can be deleted.
        given()
                .pathParam("id", 1)
                .when()
                .delete(BASE_PATH + "/{id}")
                .then()
                .statusCode(200)
                .body(equalTo("Book deleted successfully!"));
    }

    @Test
    public void shouldReturn500WhenBookDoesNotExist() {
        int nonexistentId = 999999;

        given()
                .pathParam("id", nonexistentId)
                .when()
                .delete(BASE_PATH + "/{id}")
                .then()
                .statusCode(500)
                .body(equalTo(
                        "Could not delete the book, try again!"
                ));
    }

    @Test
    public void shouldReturn400WhenBookIdIsNotNumeric() {
        given()
                .when()
                .delete(BASE_PATH + "/abc")
                .then()
                .statusCode(400);
    }

    @Test
    public void shouldHandleNegativeBookId() {
        given()
                .pathParam("id", -1)
                .when()
                .delete(BASE_PATH + "/{id}")
                .then()
                .statusCode(anyOf(
                        is(200),
                        is(400),
                        is(500)
                ));
    }
}
