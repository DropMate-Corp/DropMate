package tqs.dropmate.dropmate_backend.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer storeId;

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

    @Column
    private String managerContact;

    @Column
    private String field;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer ordersDelivered;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Parcel> associatedParcels = new ArrayList<>();

    // Getter methods that need to be ignored on JSON replies
    @JsonIgnore
    public List<Parcel> getAssociatedParcels() {
        return associatedParcels;
    }
}
