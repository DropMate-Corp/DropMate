package tqs.dropmate.dropmate_backend.datamodel;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusConverter implements AttributeConverter<Status, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Status status) {
        return switch (status) {
            case DELIVERED -> 3;
            case WAITING_FOR_PICKUP -> 2;
            case IN_DELIVERY -> 1;
            default -> throw new IllegalArgumentException("Invalid status converter: " + status);
        };
    }

    @Override
    public Status convertToEntityAttribute(Integer statusInt) {
        return switch (statusInt) {
            case 1 -> Status.IN_DELIVERY;
            case 2 -> Status.WAITING_FOR_PICKUP;
            case 3 -> Status.DELIVERED;
            default -> throw new IllegalArgumentException("Invalid status converter integer: " + statusInt);
        };
    }
}
