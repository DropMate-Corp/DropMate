package tqs.dropmate.dropmate_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;
import tqs.dropmate.dropmate_backend.utils.SuccessfulRequest;

import java.util.List;

@RestController
@RequestMapping("dropmate/acp_api")
@CrossOrigin
public class ACPController {

    // Method for the ACP Operator login
    @PostMapping("/login")
    public ResponseEntity<SuccessfulRequest> operatorLogin(@RequestParam(name = "email") String email,
                                                           @RequestParam(name = "password") String password){
        return null;
    }

    // Get all parcels belonging to the ACP in the "In delivery" state
    @GetMapping("/parcel/all/delivery")
    public ResponseEntity<List<Parcel>> getAllParcelsInDelivery(@RequestParam(name = "acpID") Long acpID){
        return null;
    }

    // Get all parcels belonging to the ACP in the "Waiting for pickup" state
    @GetMapping("/parcel/all/pickup")
    public ResponseEntity<List<Parcel>> getAllParcelsWaitingForPickup(@RequestParam(name = "acpID") Long acpID){
        return null;
    }

    // Get all parcels belonging to the ACP in the "Delivered" state
    @GetMapping("/parcel/all/delivered")
    public ResponseEntity<List<Parcel>> getAllParcelsDelivered(@RequestParam(name = "acpID") Long acpID){
        return null;
    }

    // Used to check-in a parcel when it reaches the Pickup Point
    @PutMapping("/parcel/{parcelID}/checkin")
    public ResponseEntity<SuccessfulRequest> checkInParcel(@PathVariable(name = "parcelID") Long parcelID,
                                                           @RequestParam(name = "deliveryCode") String deliveryCode){
        return null;
    }

    // Used to check-out a parcel when it reaches the Pickup Point
    @PutMapping("/parcel/{parcelID}/checkout")
    public ResponseEntity<SuccessfulRequest> checkOutParcel(@PathVariable(name = "parcelID") Long parcelID,
                                                           @RequestParam(name = "pickupCode") String pickupCode){
        return null;
    }

    // Returns the details of a Parcel
    @GetMapping("/parcel/{parcelID}")
    public ResponseEntity<Parcel> getParcelInfo(@PathVariable(name = "parcelID") Long parcelID){
        return null;
    }

    // Returns the current Delivery Limit for the ACP
    @GetMapping("/limit")
    public ResponseEntity<Integer> getDeliveryLimit(@RequestParam(name = "acpID") Long acpID){
        return null;
    }

    // Updates the Delivery Limit for the ACP
    @PutMapping("/limit")
    public ResponseEntity<Integer> updateDeliveryLimit(@RequestParam(name = "acpID") Long acpID){
        return null;
    }
}
