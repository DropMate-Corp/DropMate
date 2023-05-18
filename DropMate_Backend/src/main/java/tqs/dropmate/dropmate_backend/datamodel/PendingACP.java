package tqs.dropmate.dropmate_backend.datamodel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidate_associated_collection_points")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PendingACP {
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

    // 0 - Pending; 1 - Rejected; 2 - Accepted
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer status;

    @Column(nullable = false)
    private String description;
}
