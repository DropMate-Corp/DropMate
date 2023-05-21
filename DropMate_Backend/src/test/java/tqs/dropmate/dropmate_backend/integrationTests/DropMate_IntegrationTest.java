package tqs.dropmate.dropmate_backend.integrationTests;

import io.restassured.RestAssured;
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
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create")
//@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DropMate_IntegrationTest {
    private final static String BASE_URI = "http://localhost:";

    @LocalServerPort
    private int randomServerPort;

    @Autowired
    private AssociatedCollectionPointRepository acpRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private StoreRepository storeRepository;

    private AssociatedCollectionPoint testACP;
    private AssociatedCollectionPoint testACP2;
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
        testACP2 = new AssociatedCollectionPoint("PickUpPointTwo", "pickuptwo@mail.pt", "Porto", "Fake address 2, Porto", "935264901", 15 );

        Map<String, Integer> statsMap = new HashMap<>();

        statsMap.put("total_parcels", 10);
        statsMap.put("parcels_in_delivery", 5);
        statsMap.put("parcels_waiting_pickup", 3);

        testACP.setOperationalStatistics(statsMap);
        testACP2.setOperationalStatistics(statsMap);

        acpRepository.saveAndFlush(testACP);
        acpRepository.saveAndFlush(testACP2);

        testStore = new Store("PickUpPointTwo", "pickuptwo@mail.pt", "Porto", "Fake address 2, Porto", "935264901");
        storeRepository.saveAndFlush(testStore);
    }

    @AfterEach
    public void resetDB(){
        acpRepository.deleteAll();
        parcelRepository.deleteAll();
        storeRepository.deleteAll();
    }

    @Test
    @Order(1)
    public void whenGetSpecificOperationStatistics_withValidID_thenReturn_statusOK() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/acp/1/statistics")
                .then().statusCode(200)
                .body("total_parcels", is(10)).and()
                .body("parcels_waiting_pickup", is(3)).and()
                .body("parcels_in_delivery", is(5)).and()
                .body("deliveryLimit", is(10));
    }

    @Test
    @Order(2)
    public void whenGetSpecificOperationStatistics_withInvalidID_thenReturn_statusNotFound() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/acp/-21/statistics")
                .then().statusCode(404);
    }

    @Test
    @Order(3)
    public void whenGetSpecificACPDetails_withValidID_thenReturn_statusOK() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/acp/5")
                .then().statusCode(200)
                .body("city", is("Aveiro")).and()
                .body("address", is("Fake address 1, Aveiro")).and()
                .body("deliveryLimit", is(10));
    }

    @Test
    @Order(4)
    public void whenGetSpecificACPDetails_withInvalidID_thenReturn_statusNotFound() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/acp/-21")
                .then().statusCode(404);
    }

    @Test
    @Order(5)
    public void whenGetAllACP_thenReturn_statusOK() throws Exception {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/acp")
                .then().statusCode(200)
                .body("size()", is(2)).and()
                .body("city", hasItems("Aveiro", "Porto")).and()
                .body("[1].email", is("pickuptwo@mail.pt"));

    }

    @Test
    @Order(6)
    public void whenGetAllAParcelsWaitDelivery_thenReturn_statusOK() throws Exception {
        parcelRepository.saveAndFlush(new Parcel("DEL123", "PCK123", 1.5, null, null, Status.IN_DELIVERY, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL456", "PCK456", 3.2, null, null, Status.IN_DELIVERY, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL790", "PCK356", 1.5, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL367", "PCK803", 2.2, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL000", "PCK257", 1.5, new Date(2023, 5, 22), new Date(2023, 5, 28), Status.DELIVERED, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL843", "PCK497", 1.6, new Date(2023, 5, 22), new Date(2023, 5, 29), Status.DELIVERED, testACP, testStore));

        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/parcels/all/delivery")
                .then().statusCode(200)
                .body("size()", is(2)).and()
                .body("parcelStatus", hasItems(Status.IN_DELIVERY.toString())).and()
                .body("deliveryCode", hasItems("DEL123", "DEL456"));
    }

    @Test
    @Order(7)
    public void whenGetAllAParcelsWaitPickup_thenReturn_statusOK() throws Exception {
        parcelRepository.saveAndFlush(new Parcel("DEL123", "PCK123", 1.5, null, null, Status.IN_DELIVERY, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL456", "PCK456", 3.2, null, null, Status.IN_DELIVERY, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL790", "PCK356", 1.5, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL367", "PCK803", 2.2, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL000", "PCK257", 1.5, new Date(2023, 5, 22), new Date(2023, 5, 28), Status.DELIVERED, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL843", "PCK497", 1.6, new Date(2023, 5, 22), new Date(2023, 5, 29), Status.DELIVERED, testACP, testStore));

        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/parcels/all/pickup")
                .then().statusCode(200)
                .body("size()", is(2)).and()
                .body("parcelStatus", hasItems(Status.WAITING_FOR_PICKUP.toString())).and()
                .body("pickupCode", hasItems("PCK356", "PCK803"));
    }

    @Test
    @Order(8)
    public void whenGetParcelsWaitingDelivery_atSpecificACP_withValidID_thenReturn_StatusOK() throws Exception {
        parcelRepository.saveAndFlush(new Parcel("DEL123", "PCK123", 1.5, null, null, Status.IN_DELIVERY, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL456", "PCK456", 3.2, null, null, Status.IN_DELIVERY, testACP2, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL790", "PCK356", 1.5, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL367", "PCK803", 2.2, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP, testACP2, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL000", "PCK257", 1.5, new Date(2023, 5, 22), new Date(2023, 5, 28), Status.DELIVERED, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL843", "PCK497", 1.6, new Date(2023, 5, 22), new Date(2023, 5, 29), Status.DELIVERED, testACP2, testStore));

        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/parcels/15/delivery")
                .then().statusCode(200)
                .body("size()", is(1)).and()
                .body("parcelStatus", hasItems(Status.IN_DELIVERY.toString())).and()
                .body("pickupCode", hasItems("PCK123"));
    }

    @Test
    @Order(9)
    public void whenGetParcelsWaitingPickup_atSpecificACP_withValidID_thenReturn_StatusOK() throws Exception {
        parcelRepository.saveAndFlush(new Parcel("DEL123", "PCK123", 1.5, null, null, Status.IN_DELIVERY, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL456", "PCK456", 3.2, null, null, Status.IN_DELIVERY, testACP2, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL790", "PCK356", 1.5, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL367", "PCK803", 2.2, new Date(2023, 5, 22), null, Status.WAITING_FOR_PICKUP, testACP2, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL000", "PCK257", 1.5, new Date(2023, 5, 22), new Date(2023, 5, 28), Status.DELIVERED, testACP, testStore));
        parcelRepository.saveAndFlush(new Parcel("DEL843", "PCK497", 1.6, new Date(2023, 5, 22), new Date(2023, 5, 29), Status.DELIVERED, testACP2, testStore));

        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/parcels/17/pickup")
                .then().statusCode(200)
                .body("size()", is(1)).and()
                .body("parcelStatus", hasItems(Status.WAITING_FOR_PICKUP.toString())).and()
                .body("pickupCode", hasItems("PCK356"));
    }

    @Test
    @Order(10)
    public void whenGetParcelsWaitingDelivery_atSpecificACP_withInvalidID_thenReturn_statusNotFound() throws Exception {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/parcels/-5/delivery")
                .then().statusCode(404);
    }

    @Test
    @Order(11)
    public void whenGetParcelsWaitingPickup_atSpecificACP_withInvalidID_thenReturn_statusNotFound() throws Exception {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/parcels/-5/pickup")
                .then().statusCode(404);
    }

    @Test
    @Order(12)
    public void whenGetAllACPOperationalStatistics_thenReturn_statusOK() throws Exception {
        // Doing the test
        io.restassured.path.json.JsonPath path  = RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/acp/statistics")
                .then().statusCode(200)
                .extract().response().jsonPath();

        Map<String, Map<String, Object>> responseMap = path.getMap("$");
        
        for (Map<String, Object> value : responseMap.values()) {
            assertThat(value.containsKey("total_parcels"), equalTo(true));
            assertThat(value.containsKey("parcels_waiting_pickup"), equalTo(true));
            assertThat(value.containsKey("deliveryLimit"), equalTo(true));
            assertThat(value.containsKey("parcels_in_delivery"), equalTo(true));
        }
    }

    @Test
    @Order(13)
    public void whenUpdateACPDetails_withValidID_allFields_thenReturn_statusOK() throws Exception {
        RestAssured.given().contentType("application/json")
                .param("name", "test")
                .param("email", "newemail@mail.pt")
                .param("telephone", "000000000")
                .param("city", "Lalaland")
                .param("address", "Nevermore")
                .when().put(BASE_URI + randomServerPort + "/dropmate/admin/acp/26")
                .then().statusCode(200)
                .body("city", is("Lalaland")).and()
                .body("address", is("Nevermore")).and()
                .body("email", is("newemail@mail.pt")).and()
                .body("deliveryLimit", is(15));
    }

    @Test
    @Order(14)
    public void whenUpdateACPDetails_withValidID_somFields_thenReturn_statusOK() throws Exception {
        RestAssured.given().contentType("application/json")
                .param("city", "Lalaland")
                .param("address", "Nevermore")
                .when().put(BASE_URI + randomServerPort + "/dropmate/admin/acp/28")
                .then().statusCode(200)
                .body("city", is("Lalaland")).and()
                .body("address", is("Nevermore")).and()
                .body("email", is("pickuptwo@mail.pt")).and()
                .body("deliveryLimit", is(15));
    }

    @Test
    @Order(15)
    public void whenUpdateACPDetails_withInvalidID_thenReturn_statusNotFound() throws Exception {
        RestAssured.given().contentType("application/json")
                .param("city", "Lalaland")
                .param("address", "Nevermore")
                .when().put(BASE_URI + randomServerPort + "/dropmate/admin/acp/-28")
                .then().statusCode(404);
    }

}
