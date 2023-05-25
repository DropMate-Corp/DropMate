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
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.datamodel.Status;
import tqs.dropmate.dropmate_backend.datamodel.Store;
import tqs.dropmate.dropmate_backend.repositories.AssociatedCollectionPointRepository;
import tqs.dropmate.dropmate_backend.repositories.ParcelRepository;
import tqs.dropmate.dropmate_backend.repositories.StoreRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;


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
    @Autowired
    ParcelRepository parcelRepository;

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
        AssociatedCollectionPoint pickupPointThree = new AssociatedCollectionPoint();
        pickupPointThree.setName("Pickup Three");
        pickupPointThree.setCity("Viseu");
        pickupPointThree.setAddress("Fake address 3, Viseu");
        pickupPointThree.setEmail("pickupthree@mail.pt");
        pickupPointThree.setDeliveryLimit(12);
        pickupPointThree.setTelephoneNumber("900000000");

        // Under Limit
        Map<String, Integer> statsMapOne = new HashMap<>();
        statsMapOne.put("total_parcels", 10);
        statsMapOne.put("parcels_in_delivery", 5);
        statsMapOne.put("parcels_waiting_pickup", 3);
        testACP.setOperationalStatistics(statsMapOne);

        // Over Limit
        Map<String, Integer> statsMapTwo = new HashMap<>();
        statsMapTwo.put("total_parcels", 30);
        statsMapTwo.put("parcels_in_delivery", 12);
        statsMapTwo.put("parcels_waiting_pickup", 3);
        testACP2.setOperationalStatistics(statsMapTwo);

        // Under Limit
        Map<String, Integer> statsMapThree = new HashMap<>();
        statsMapThree.put("total_parcels", 30);
        statsMapThree.put("parcels_in_delivery", 2);
        statsMapThree.put("parcels_waiting_pickup", 1);
        pickupPointThree.setOperationalStatistics(statsMapThree);

        acpRepository.saveAndFlush(testACP);
        acpRepository.saveAndFlush(testACP2);
        acpRepository.saveAndFlush(pickupPointThree);

        testStore = new Store("PickUpPointTwo", "pickuptwo@mail.pt", "Porto", "Fake address 2, Porto", "935264901");
        storeRepository.saveAndFlush(testStore);

        // Parcel
        Parcel testParcel = new Parcel(1, "DELT1463", "PCKD3674", 5.0, Date.valueOf(LocalDate.now().plusDays(5)),
                Date.valueOf(LocalDate.now().plusDays(15)), Status.DELIVERED, testACP, testStore);
        parcelRepository.saveAndFlush(testParcel);
    }

    @AfterEach
    public void resetDB(){
        acpRepository.deleteAll();
        storeRepository.deleteAll();
        parcelRepository.deleteAll();
    }

    @Test
    @Order(1)
    void whenGettingAvailableACP_withValidParameters_thenReturnOnlyACPSUnderLimit_statusOK() throws Exception {
        RestAssured.given().contentType(ContentType.JSON)
                .when().get(BASE_URI + randomServerPort + "/dropmate/estore_api/acp?storeID=" + "1")
                .then().statusCode(200)
                .body("size()", is(2)).and()
                .body("city", hasItems("Aveiro", "Viseu")).and()
                .body("[1].email", is("pickupthree@mail.pt"));
    }

    @Test
    @Order(2)
    void whenGettingAvailableACP__withInvalidStoreID_statusNotFound() throws Exception {
        RestAssured.given().contentType(ContentType.JSON)
                .when().get(BASE_URI + randomServerPort + "/dropmate/estore_api/acp?storeID=" + "1")
                .then().statusCode(404);
    }

    @Test
    @Order(3)
    void whenCreatingOrder_withValidParameters_thenReturn_statusOK() {
        RestAssured.given().contentType(ContentType.JSON)
                .when().post(BASE_URI + randomServerPort + "/dropmate/estore_api/parcel?acpID=" + "7"
                        + "&storeID=" + "3")
                .then().statusCode(200)
                .body("delivery_date", Matchers.notNullValue()).and()
                .body("pickup_code", Matchers.notNullValue()).and()
                .body("status", is(Status.IN_DELIVERY.toString()));
    }

    @Test
    @Order(4)
    void whenCreatingOrder_withInvalidStoreID_thenReturn_statusNotFound() throws Exception {
        RestAssured.given().contentType(ContentType.JSON)
                .when().post(BASE_URI + randomServerPort + "/dropmate/estore_api/parcel?acpID=" + "10"
                        + "&storeID=" + "-1")
                .then().statusCode(404);
    }

    @Test
    @Order(5)
    void whenCreatingOrder_withInvalidACPID_thenReturn_statusNotFound() throws Exception {
        RestAssured.given().contentType(ContentType.JSON)
                .when().post(BASE_URI + randomServerPort + "/dropmate/estore_api/parcel?acpID=" + "-1"
                        + "&storeID=" + "4")
                .then().statusCode(404);
    }

    @Test
    @Order(6)
    void whenGetParcelStatus_withValidPickupCode_thenReturnStatusOK() throws Exception {
        RestAssured.given().contentType(ContentType.JSON)
                .when().get(BASE_URI + randomServerPort + "/dropmate/estore_api/parcel/PCKD3674")
                .then().statusCode(200)
                .body("status", is(Status.DELIVERED.toString())).and()
                .body("delivery_date", is(Date.valueOf(LocalDate.now().plusDays(5)).toString())).and()
                .body("pickup_date", is(Date.valueOf(LocalDate.now().plusDays(15)).toString()));
    }

    @Test
    @Order(7)
    void whenGetParcelStatus_withInvalidPickupCode_statusNotFound() throws Exception {
        RestAssured.given().contentType(ContentType.JSON)
                .when().get(BASE_URI + randomServerPort + "/dropmate/estore_api/parcel/NOCODE")
                .then().statusCode(404);
    }

    @Test
    @Order(8)
    void whenGetACPDetails_withValidID_thenReturn_statusOK() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/estore_api/acp/22")
                .then().statusCode(200)
                .body("city", is("Aveiro")).and()
                .body("address", is("Fake address 1, Aveiro")).and()
                .body("deliveryLimit", is(10));
    }

    @Test
    @Order(9)
    void whenGetACPDetails_withInvalidID_thenReturn_statusNotFound() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/estore_api/acp/-21")
                .then().statusCode(404);
    }
}
