package tqs.dropmate.dropmate_backend.integrationTests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
//@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ACPController_IntegrationTest {
    private final static String BASE_URI = "http://localhost:";

    @Autowired
    AssociatedCollectionPointRepository acpRepository;

    @LocalServerPort
    private int randomServerPort;

    private AssociatedCollectionPoint testACP;

    @Container
    public static MySQLContainer container = new MySQLContainer("mysql:latest")
            .withUsername("springuser")
            .withPassword("password")
            .withDatabaseName("DropMate");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.username", container::getUsername);
    }

    @BeforeEach
    public void setUp(){
        testACP = new AssociatedCollectionPoint("PickUpPointOne", "pickupone@mail.pt", "Aveiro", "Fake address 1, Aveiro", "953339994", 10 );
        acpRepository.saveAndFlush(testACP);
    }

    @AfterEach
    public void resetDB(){
        acpRepository.deleteAll();
    }

    @Test
    @Order(1)
    void whenGetACPDelivery_withValidID_thenReturn_StatusOK() throws Exception {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/acp_api/limit?acpID=1")
                .then().statusCode(200)
                .body(Matchers.equalTo("10"));
    }


    @Test
    @Order(2)
    void whenGetACPDelivery_withInvalidID_thenReturn_StatusNotFound() throws Exception {
        RestAssured.given().contentType(ContentType.JSON)
                .when().get(BASE_URI + randomServerPort + "/dropmate/acp_api/limit?acpID=-1")
                .then().statusCode(404);
    }

    @Test
    @Order(3)
    void whenUpdateACPDelivery_withValidID_thenReturn_StatusOK() throws Exception {
        RestAssured.with().contentType("application/json")
                .when().put(BASE_URI + randomServerPort + "/dropmate/acp_api/limit?acpID=3&deliveryLimit=50")
                .then().statusCode(200)
                .body(Matchers.equalTo("50"));
    }

    @Test
    @Order(4)
    void whenUpdateACPDelivery_withInvalidID_thenReturn_StatusNotFound() throws Exception {
        RestAssured.given().contentType(ContentType.JSON)
                .when().put(BASE_URI + randomServerPort + "/dropmate/acp_api/limit?acpID=-1&deliveryLimit=50")
                .then().statusCode(404);
    }
}
