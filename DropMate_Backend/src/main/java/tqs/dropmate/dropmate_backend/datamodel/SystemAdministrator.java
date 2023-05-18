package tqs.dropmate.dropmate_backend.datamodel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_administrators")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SystemAdministrator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer adminID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}
