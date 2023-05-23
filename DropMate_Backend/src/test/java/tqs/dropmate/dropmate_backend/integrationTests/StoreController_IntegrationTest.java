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
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.datamodel.Store;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.repositories.StoreRepository;

import static org.hamcrest.CoreMatchers.is;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
//@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoreController_IntegrationTest {
    private final static String BASE_URI = "http://localhost:";

    @Autowired
    StoreRepository storeRepository;
    @Autowired
    AssociatedCollectionPointRepository acpRepository;

    @LocalServerPort
    private int randomServerPort;

    private AssociatedCollectionPoint testACP;
    private Store testStore;

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
        AssociatedCollectionPoint testACP2 = new AssociatedCollectionPoint("PickUpPointTwo", "pickuptwo@mail.pt", "Porto", "Fake address 2, Porto", "935264901", 15 );

        acpRepository.saveAndFlush(testACP);
        acpRepository.saveAndFlush(testACP2);

        testStore = new Store("PickUpPointTwo", "pickuptwo@mail.pt", "Porto", "Fake address 2, Porto", "935264901");
        storeRepository.saveAndFlush(testStore);
    }

    @AfterEach
    public void resetDB(){
        acpRepository.deleteAll();
        storeRepository.deleteAll();
    }

    @Test
    @Order(1)
    void whenCreatingOrder_withValidParameters_thenReturn_statusOK() {
        RestAssured.given().log().all().contentType(ContentType.JSON)
                .when().post(BASE_URI + randomServerPort + "/dropmate/estore_api/parcel?acpID=" + "1"
                        + "&storeID=" + "1")
                .then().statusCode(200)
                .body("deliveryCode", Matchers.notNullValue()).and()
                .body("pickupCode", Matchers.notNullValue()).and()
                .body("parcelStatus", is(Status.IN_DELIVERY.toString()));
    }

    @Test
    @Order(2)
    void whenCreatingOrder_withInvalidStoreID_thenReturn_statusNotFound() throws Exception {
        RestAssured.given().log().all().contentType(ContentType.JSON)
                .when().post(BASE_URI + randomServerPort + "/dropmate/estore_api/parcel?acpID=" + "1"
                        + "&storeID=" + "-1")
                .then().statusCode(404);
    }

    @Test
    @Order(3)
    void whenCreatingOrder_withInvalidACPID_thenReturn_statusNotFound() throws Exception {
        RestAssured.given().log().all().contentType(ContentType.JSON)
                .when().post(BASE_URI + randomServerPort + "/dropmate/estore_api/parcel?acpID=" + "-1"
                        + "&storeID=" + "1")
                .then().statusCode(404);
    }
}
