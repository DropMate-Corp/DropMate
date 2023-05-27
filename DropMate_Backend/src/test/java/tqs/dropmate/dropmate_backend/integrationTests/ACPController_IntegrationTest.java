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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
//@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ACPController_IntegrationTest {
    private final static String BASE_URI = "http://localhost:";

    @Autowired
    AssociatedCollectionPointRepository acpRepository;
    @Autowired
    ParcelRepository parcelRepository;
    @Autowired
    StoreRepository storeRepository;

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

        Store testStore = new Store("PickUpPointTwo", "pickuptwo@mail.pt", "Porto", "Fake address 2, Porto", "935264901");
        storeRepository.saveAndFlush(testStore);


        parcelRepository.saveAndFlush(new Parcel("DEL123", "PCK123", 1.5, null, null, Status.IN_DELIVERY, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL456", "PCK456", 3.2, null, null, Status.IN_DELIVERY, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL790", "PCK356", 1.5, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL367", "PCK803", 2.2, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL000", "PCK257", 1.5, new Date(2023, 5, 22), new Date(2023, 5, 28), Status.DELIVERED, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL843", "PCK497", 1.6, new Date(2023, 5, 22), new Date(2023, 5, 29), Status.DELIVERED, testACP, testStore));
    }

    @AfterEach
    public void resetDB(){
        acpRepository.deleteAll();
        parcelRepository.deleteAll();
    }

    @Test
    @Order(1)
    void whenGetAllAParcelsWaitDelivery_thenReturn_statusOK() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/acp_api/parcel/all/delivery?acpID=1")
                .then().statusCode(200)
                .body("size()", is(2)).and()
                .body("parcelStatus", hasItems(Status.IN_DELIVERY.toString())).and()
                .body("deliveryCode", hasItems("DEL123", "DEL456"));
    }

    @Test
    @Order(2)
    void whenGetAllAParcelsWaitPickup_thenReturn_statusOK() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/acp_api/parcel/all/pickup?acpID=2")
                .then().statusCode(200)
                .body("size()", is(2)).and()
                .body("parcelStatus", hasItems(Status.WAITING_FOR_PICKUP.toString())).and()
                .body("pickupCode", hasItems("PCK356", "PCK803"));
    }

    @Test
    @Order(3)
    void whenGetAllAParcelsDelivered_thenReturn_statusOK() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/acp_api/parcel/all/delivered?acpID=3")
                .then().statusCode(200)
                .body("size()", is(2)).and()
                .body("parcelStatus", hasItems(Status.DELIVERED.toString())).and()
                .body("pickupCode", hasItems("PCK257", "PCK497"));
    }

    @Test
    @Order(4)
    void whenGetACPDelivery_withValidID_thenReturn_StatusOK() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/acp_api/limit?acpID=4")
                .then().statusCode(200)
                .body(Matchers.equalTo("10"));
    }


    @Test
    @Order(5)
    void whenGetACPDelivery_withInvalidID_thenReturn_StatusNotFound() {
        RestAssured.given().contentType(ContentType.JSON)
                .when().get(BASE_URI + randomServerPort + "/dropmate/acp_api/limit?acpID=-1")
                .then().statusCode(404);
    }

    @Test
    @Order(6)
    void whenUpdateACPDelivery_withValidID_thenReturn_StatusOK() {
        RestAssured.with().contentType("application/json")
                .when().put(BASE_URI + randomServerPort + "/dropmate/acp_api/limit?acpID=6&deliveryLimit=50")
                .then().statusCode(200)
                .body(Matchers.equalTo("50"));
    }

    @Test
    @Order(7)
    void whenUpdateACPDelivery_withInvalidID_thenReturn_StatusNotFound() {
        RestAssured.given().contentType(ContentType.JSON)
                .when().put(BASE_URI + randomServerPort + "/dropmate/acp_api/limit?acpID=-1&deliveryLimit=50")
                .then().statusCode(404);
    }

    @Test
    @Order(8)
    void whenGetParcelsWaitingDelivery_atSpecificACP_withInvalidID_thenReturn_statusNotFound() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/acp_api/parcel/all/pickup?acpID=1")
                .then().statusCode(404);
    }

    @Test
    @Order(9)
    void whenGetParcelsWaitingPickup_atSpecificACP_withInvalidID_thenReturn_statusNotFound() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/acp_api/parcel/all/delivery?acpID=1")
                .then().statusCode(404);
    }

    @Test
    @Order(10)
    void whenGetParcelsDelivered_atSpecificACP_withInvalidID_thenReturn_statusNotFound() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/acp_api/parcel/all/delivered?acpID=1")
                .then().statusCode(404);
    }

    @Test
    @Order(11)
    void whenDoingCheckIn_existingParcel_validDeliveryCode_thenReturn_statusOK() {
        RestAssured.with().contentType("application/json")
                .when().put(BASE_URI + randomServerPort + "/dropmate/acp_api/parcel/61/checkin?deliveryCode=DEL123")
                .then().statusCode(200)
                .body("deliveryCode", is("DEL123")).and()
                .body("parcelStatus", is(Status.WAITING_FOR_PICKUP.toString())).and()
                .body("deliveryDate", is(Date.valueOf(LocalDate.now()).toString()));
    }

    @Test
    @Order(12)
    void whenDoingCheckIn_existingParcel_invalidDeliveryCode_thenReturn_statusNotFound() throws Exception {
        RestAssured.with().contentType("application/json")
                .when().put(BASE_URI + randomServerPort + "/dropmate/acp_api/parcel/67/checkin?deliveryCode=WRONGCODE")
                .then().statusCode(401);
    }

    @Test
    @Order(13)
    void whenDoingCheckIn_nonExistingParcel_thenReturn_statusNotFound() throws Exception {
        RestAssured.with().contentType("application/json")
                .when().put(BASE_URI + randomServerPort + "/dropmate/acp_api/parcel/-534/checkin?deliveryCode=DEL123")
                .then().statusCode(404);
    }
}
