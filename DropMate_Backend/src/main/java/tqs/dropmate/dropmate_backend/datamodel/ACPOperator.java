package tqs.dropmate.dropmate_backend.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "operators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ACPOperator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer operatorID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="acpId", nullable = false)
    @ToString.Exclude
    private AssociatedCollectionPoint acpId;
}
