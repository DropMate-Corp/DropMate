package tqs.dropmate.dropmate_backend.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "associated_collection_points")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AssociatedCollectionPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer acpId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String telephoneNumber;

    @Column(name = "delivery_limit", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer deliveryLimit;

    @Column
    private String managerContact;

    @ElementCollection
    @CollectionTable(name = "acp_operational_details", joinColumns = @JoinColumn(name = "acpId"))
    @MapKeyColumn(name = "statistic_name")
    @Column(name = "statistic_value")
    private Map<String, Integer> operationalStatistics = new HashMap<>();

    @OneToMany(mappedBy = "acpId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ACPOperator> operators = new ArrayList<>();

    @OneToMany(mappedBy = "pickupACP", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Parcel> associatedParcels = new ArrayList<>();

    // Getter methods that need to be ignored on JSON replies
    @JsonIgnore
    public List<ACPOperator> getOperators() {return operators;}

    @JsonIgnore
    public List<Parcel> getAssociatedParcels() {
        return associatedParcels;
    }
}
