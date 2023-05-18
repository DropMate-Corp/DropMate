package tqs.dropmate.dropmate_backend.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Table(name = "parcels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer parcelId;

    @Column(nullable = false)
    private String deliveryCode;

    @Column(nullable = false)
    private String pickupCode;

    @Column(nullable = false)
    private Double weight;

    @Column
    private Date deliveryDate;

    @Column
    private Date pickupDate;

    @Convert(converter = StatusConverter.class)
    private Status parcelStatus;

    public Parcel(String deliveryCode, String pickupCode, Double weight, Date deliveryDate, Date pickupDate, Status parcelStatus) {
        this.deliveryCode = deliveryCode;
        this.pickupCode = pickupCode;
        this.weight = weight;
        this.deliveryDate = deliveryDate;
        this.pickupDate = pickupDate;
        this.parcelStatus = parcelStatus;
    }

    public Parcel(String deliveryCode, String pickupCode, Double weight, Date deliveryDate, Date pickupDate, Status parcelStatus, AssociatedCollectionPoint pickupACP, Store store) {
        this.deliveryCode = deliveryCode;
        this.pickupCode = pickupCode;
        this.weight = weight;
        this.deliveryDate = deliveryDate;
        this.pickupDate = pickupDate;
        this.parcelStatus = parcelStatus;
        this.pickupACP = pickupACP;
        this.store = store;
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="acpId", nullable = false)
    @ToString.Exclude
    private AssociatedCollectionPoint pickupACP;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="storeId", nullable = false)
    @ToString.Exclude
    private Store store;
}
