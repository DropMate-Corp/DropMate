package tqs.dropmate.dropmate_backend.integrationTests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DropMate_IntegrationTest {
    private final static String BASE_URI = "http://api:";

    @LocalServerPort
    private int randomServerPort;

    @Autowired
    private AssociatedCollectionPointRepository acpRepository;

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

    @Test
    public void whenGetAllACP_thenReturn_statusOK() throws Exception {
        acpRepository.save(new AssociatedCollectionPoint("PickUpPointOne", "pickupone@mail.pt", "Aveiro", "Fake address 1, Aveiro", "953339994", 10 ));
        acpRepository.save(new AssociatedCollectionPoint("PickUpPointTwo", "pickuptwo@mail.pt", "Porto", "Fake address 2, Porto", "935264901", 15 ));

        RestAssured.with().contentType("application/json")
                .when().get(BASE_URI + randomServerPort + "/dropmate/admin/acp")
                .then().statusCode(200)
                .body("size()", is(2)).and()
                .body("name", hasItems("Aveiro", "Porto")).and()
                .body("[1].email", is("pickuptwo@mail.pt"));

    }

}
