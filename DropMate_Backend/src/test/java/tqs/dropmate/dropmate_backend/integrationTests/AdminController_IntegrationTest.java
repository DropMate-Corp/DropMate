package tqs.dropmate.dropmate_backend.integrationTests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
import tqs.dropmate.dropmate_backend.datamodel.*;
import tqs.dropmate.dropmate_backend.repositories.*;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
//@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@TestPropertySource(locations = "classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminController_IntegrationTest {
    private final static String BASE_URI = "http://localhost:";

    @LocalServerPort
    private int randomServerPort;

    @Autowired
    private AssociatedCollectionPointRepository acpRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private PendingAssociatedCollectionPointRepository pendingACPRepository;
    @Autowired
    private SystemAdministratorRepository systemAdministratorRepository;

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
        // Use the SQL Server container in your test logic
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

        // System admin
        SystemAdministrator user = new SystemAdministrator();
        user.setName("User");
        user.setEmail("user@email.com");
        user.setPassword("password");

        systemAdministratorRepository.saveAndFlush(user);

        // Preparing the test
        pendingACPRepository.deleteAll();

        PendingACP candidateACP = new PendingACP();

        candidateACP.setName("Test New ACP");
        candidateACP.setEmail("newacp@mail.pt");
        candidateACP.setCity("Aveiro");
        candidateACP.setAddress("Fake Street no 1, Aveiro");
        candidateACP.setTelephoneNumber("000000000");
        candidateACP.setDescription("I am a totally legit pickup point");
        candidateACP.setStatus(0);

        PendingACP candidateACP2 = new PendingACP();

        candidateACP2.setName("Test New ACP");
        candidateACP2.setEmail("newacp@mail.pt");
        candidateACP2.setCity("Aveiro");
        candidateACP2.setAddress("Fake Street no 1, Aveiro");
        candidateACP2.setTelephoneNumber("000000000");
        candidateACP2.setDescription("I am a totally legit pickup point");
        candidateACP2.setStatus(1);


        pendingACPRepository.saveAndFlush(candidateACP);
        pendingACPRepository.saveAndFlush(candidateACP2);
    }

    @AfterEach
    public void resetDB(){
        acpRepository.deleteAll();
        parcelRepository.deleteAll();
        storeRepository.deleteAll();
        pendingACPRepository.deleteAll();
        systemAdministratorRepository.deleteAll();
    }

    @Test
    @Order(1)
    void whenGetSpecificOperationStatistics_withValidID_thenReturn_statusOK() {
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
    void whenGetSpecificOperationStatistics_withInvalidID_thenReturn_statusNotFound() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/acp/-21/statistics")
                .then().statusCode(404);
    }

    @Test
    @Order(3)
    void whenGetSpecificACPDetails_withValidID_thenReturn_statusOK() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/acp/5")
                .then().statusCode(200)
                .body("city", is("Aveiro")).and()
                .body("address", is("Fake address 1, Aveiro")).and()
                .body("deliveryLimit", is(10));
    }

    @Test
    @Order(4)
    void whenGetSpecificACPDetails_withInvalidID_thenReturn_statusNotFound() {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/acp/-21")
                .then().statusCode(404);
    }

    @Test
    @Order(5)
    void whenGetAllACP_thenReturn_statusOK() throws Exception {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/acp")
                .then().statusCode(200)
                .body("size()", is(2)).and()
                .body("city", hasItems("Aveiro", "Porto")).and()
                .body("[1].email", is("pickuptwo@mail.pt"));
    }

    @Test
    @Order(6)
    void whenGetAllAParcelsWaitDelivery_thenReturn_statusOK() throws Exception {
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
    void whenGetAllAParcelsWaitPickup_thenReturn_statusOK() throws Exception {
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
    void whenGetParcelsWaitingDelivery_atSpecificACP_withValidID_thenReturn_StatusOK() throws Exception {
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
    void whenGetParcelsWaitingPickup_atSpecificACP_withValidID_thenReturn_StatusOK() throws Exception {
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
    void whenGetParcelsWaitingDelivery_atSpecificACP_withInvalidID_thenReturn_statusNotFound() throws Exception {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/parcels/-5/delivery")
                .then().statusCode(404);
    }

    @Test
    @Order(11)
    void whenGetParcelsWaitingPickup_atSpecificACP_withInvalidID_thenReturn_statusNotFound() throws Exception {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/parcels/-5/pickup")
                .then().statusCode(404);
    }

    @Test
    @Order(12)
    void whenGetAllACPOperationalStatistics_thenReturn_statusOK() throws Exception {
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
    void whenUpdateACPDetails_withValidID_allFields_thenReturn_statusOK() throws Exception {
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
    void whenUpdateACPDetails_withValidID_somFields_thenReturn_statusOK() throws Exception {
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
    void whenUpdateACPDetails_withInvalidID_thenReturn_statusNotFound() throws Exception {
        RestAssured.given().contentType("application/json")
                .param("city", "Lalaland")
                .param("address", "Nevermore")
                .when().put(BASE_URI + randomServerPort + "/dropmate/admin/acp/-28")
                .then().statusCode(404);
    }

    @Test
    @Order(16)
    void reviewCandidateACP_withValidID_notReviewedBefore_thenAcceptACP() throws Exception {
        // Making the call
        RestAssured.with().contentType("application/json")
                .when().put(BASE_URI + randomServerPort + "/dropmate/admin/acp/pending/32/status?newStatus=" + "2")
                .then().statusCode(200)
                .assertThat().body("message", equalTo("Operation denied, as this candidate request has already been reviewed!"));

    }

    @Test
    @Order(17)
    void reviewCandidateACP_withValidID_notReviewedBefore_rejectACP() throws Exception {
        // Performing the call
        RestAssured.given().contentType("application/json")
                .param("newStatus", "1")
                .when().put(BASE_URI + randomServerPort + "/dropmate/admin/acp/pending/33/status")
                .then().statusCode(200)
                .body("message", is("Request rejected!"));
    }

    @Test
    @Order(18)
    void reviewCandidateACP_withValidID_reviewedBefore_thenRejectNewEvaluation() throws Exception {
        // Performing the call
        RestAssured.given().contentType("application/json")
                .param("newStatus", "2")
                .when().put(BASE_URI + randomServerPort + "/dropmate/admin/acp/pending/36/status")
                .then().statusCode(200)
                .body("message", is("Operation denied, as this candidate request has already been reviewed!"));
    }

    @Test
    @Order(19)
    void reviewCandidateACP_withInvalidID_thenReceiveException() throws Exception {
        // Performing the call
        RestAssured.given().contentType("application/json")
                .param("newStatus", "1")
                .when().put(BASE_URI + randomServerPort + "/dropmate/admin/acp/pending/-6/status")
                .then().statusCode(404);
    }

    @Test
    @Order(26)
    void whenAddNewPendingACP_thenReturn_correspondingACP() throws Exception {
        RestAssured.given().contentType(ContentType.JSON)
                .when().post(BASE_URI + randomServerPort + "/dropmate/admin/acp/pending?city=" + "Aveiro"
                + "&address=" + "Fake Street no 1, Aveiro" + "&name=" + "Test New ACP"
                        + "&email=" + "newacp@mail.pt" + "&telephoneNumber=" + "000000000"
                        + "&description=" + "I am a totally legit pickup point")
                .then().statusCode(200)
                .body("city", is("Aveiro")).and()
                .body("address", is("Fake Street no 1, Aveiro")).and()
                .body("email", is("newacp@mail.pt")).and()
                .body("acpId", is(53)).and()
                .body("status", is(0));
    }

    @Test
    @Order(20)
    void deleteACP_withValidID_thenACPDeleted() throws Exception {
        // Performing the call
        RestAssured.given().contentType("application/json")
                .when().delete(BASE_URI + randomServerPort + "/dropmate/admin/acp/40")
                .then().statusCode(200)
                .body("message", is("ACP succesfully deleted!"));
    }

    @Test
    @Order(21)
    void deleteACP_withInvalidID_thenThrowException() throws Exception {
        // Performing the call
        RestAssured.given().contentType("application/json")
                .param("newStatus", "1")
                .when().put(BASE_URI + randomServerPort + "/dropmate/admin/acp/-41")
                .then().statusCode(404);
    }

    @Test
    @Order(22)
    void whenGetAllStores_thenReturn_statusOK() throws Exception {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/estores")
                .then().statusCode(200)
                .body("size()", is(1)).and()
                .body("city", hasItems("Porto")).and()
                .body("[0].email", is("pickuptwo@mail.pt"));

    }

    @Test
    @Order(23)
    void whenLoginValidUser_thenReturnUser_andStatus200() {
        RestAssured.with().contentType("application/json")
                .when().post(BASE_URI + randomServerPort + "/dropmate/admin/login?email=" + "user@email.com" + "&password=" + "password")
                .then().statusCode(200)
                .assertThat().body("name", equalTo("User"))
                .assertThat().body("email", equalTo("user@email.com"))
                .assertThat().body("password", equalTo("password"));
    }

    @Test
    @Order(24)
    void whenLoginWithInvalidEmail_thenReturnStatus401() {
        RestAssured.with().contentType("application/json")
                .when().post(BASE_URI + randomServerPort + "/dropmate/admin/login?email=" + "invalidemail@email.com" + "&password=" + "password")
                .then().statusCode(401)
                .assertThat().body("message", equalTo("Invalid login credentials"));
    }

    @Test
    @Order(25)
    void whenLoginWithInvalidPassword_thenReturnStatus401() {
        RestAssured.with().contentType("application/json")
                .when().post(BASE_URI + randomServerPort + "/dropmate/admin/login?email=" + "user@email.com" + "&password=" + "invalidPassword")
                .then().statusCode(401)
                .assertThat().body("message", equalTo("Invalid login credentials"));
    }

    @Test
    @Order(26)
    void whenGetAllPendingACP_thenReturn_statusOK() throws Exception {
        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/acp/pending")
                .then().statusCode(200)
                .body("size()", is(2)).and()
                .body("city", hasItems("Aveiro")).and()
                .body("[1].email", is("newacp@mail.pt"));

    }

}
