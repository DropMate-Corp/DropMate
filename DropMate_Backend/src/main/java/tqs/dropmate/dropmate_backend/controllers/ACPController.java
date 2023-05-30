package tqs.dropmate.dropmate_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.dropmate.dropmate_backend.datamodel.ACPOperator;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.exceptions.InvalidCredentialsException;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.services.ACPService;

import java.util.List;

@RestController
@RequestMapping("dropmate/acp_api")
@CrossOrigin
public class ACPController {

    private ACPService acpService;

    public ACPController(ACPService acpService) {
        this.acpService = acpService;
    }

    /** Method for the ACP Operator login. */
    @PostMapping("/login")
    public ResponseEntity<ACPOperator> operatorLogin(@RequestParam(name = "email") String email,
                                                     @RequestParam(name = "password") String password) throws InvalidCredentialsException {
        return ResponseEntity.ok().body(acpService.processOperatorLogin(email, password));
    }

    /** Get all parcels belonging to the ACP in the "In delivery" state */
    @GetMapping("/parcel/all/delivery")
    public ResponseEntity<List<Parcel>> getAllParcelsWaitingDelivery(@RequestParam(name = "acpID") Integer acpID) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(acpService.getAllParcelsWaitingDelivery(acpID));
    }

    /** Get all parcels belonging to the ACP in the "Waiting for pickup" state */
    @GetMapping("/parcel/all/pickup")
    public ResponseEntity<List<Parcel>> getAllParcelsWaitingForPickup(@RequestParam(name = "acpID") Integer acpID) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(acpService.getAllParcelsWaitingForPickup(acpID));
    }

    /** Get all parcels belonging to the ACP in the "Delivered" state */
    @GetMapping("/parcel/all/delivered")
    public ResponseEntity<List<Parcel>> getAllParcelsDelivered(@RequestParam(name = "acpID") Integer acpID) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(acpService.getAllParcelsDelivered(acpID));
    }

    /** Used to check-in a parcel when it reaches the Pickup Point */
    @PutMapping("/parcel/{parcelID}/checkin")
    public ResponseEntity<Parcel> checkInParcel(@PathVariable(name = "parcelID") Integer parcelID,
                                                @RequestParam(name = "deliveryCode") String deliveryCode) throws ResourceNotFoundException, InvalidCredentialsException {
        return ResponseEntity.ok().body(acpService.checkInProcess(parcelID, deliveryCode));
    }

    /** Used to check-out a parcel when it reaches the Pickup Point */
    @PutMapping("/parcel/{parcelID}/checkout")
    public ResponseEntity<Parcel> checkOutParcel(@PathVariable(name = "parcelID") Integer parcelID,
                                                 @RequestParam(name = "pickupCode") String pickupCode) throws ResourceNotFoundException, InvalidCredentialsException {
        return ResponseEntity.ok().body(acpService.checkOutProcess(parcelID, pickupCode));
    }

    /** Returns the details of a Parcel */
    @GetMapping("/parcel/{parcelID}")
    public ResponseEntity<Parcel> getParcelInfo(@PathVariable(name = "parcelID") Integer parcelID) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(acpService.getParcelInfo(parcelID));
    }

    /** Returns the current Delivery Limit for the ACP */
    @GetMapping("/limit")
    public ResponseEntity<Integer> getDeliveryLimit(@RequestParam(name = "acpID") Integer acpID) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(acpService.getDeliveryLimit(acpID));
    }

    /** Updates the Delivery Limit for the ACP */
    @PutMapping("/limit")
    public ResponseEntity<Integer> updateDeliveryLimit(@RequestParam(name = "acpID") Integer acpID,
                                                       @RequestParam(name = "deliveryLimit") Integer deliveryLimit) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(acpService.updateDeliveryLimit(acpID, deliveryLimit));
    }
}
