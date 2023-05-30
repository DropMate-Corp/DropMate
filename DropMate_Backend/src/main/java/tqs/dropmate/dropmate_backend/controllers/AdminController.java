package tqs.dropmate.dropmate_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.dropmate.dropmate_backend.datamodel.*;
import tqs.dropmate.dropmate_backend.exceptions.InvalidCredentialsException;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.services.AdminService;
import tqs.dropmate.dropmate_backend.utils.SuccessfulRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("dropmate/admin")
@CrossOrigin(origins = "https://dropmate-corp.github.io/DropMate-UI/")
public class AdminController {
    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /** Method for the DropMate administrator login */
    @PostMapping("/login")
    public ResponseEntity<SystemAdministrator> adminLogin(@RequestParam(name = "email") String email,
                                                          @RequestParam(name = "password") String password) throws InvalidCredentialsException {
        return ResponseEntity.ok().body(adminService.processAdminLogin(email, password));
    }


    // ACP Methods


    /** This method returns all the ACP's associated with the Platform */
    @GetMapping("/acp")
    public ResponseEntity<List<AssociatedCollectionPoint>> getAllACP(){
        return ResponseEntity.ok().body(adminService.getAllACP());
    }

    /** Updates the details of an ACP */
    @PutMapping("/acp/{acpID}")
    public ResponseEntity<AssociatedCollectionPoint> updateACP(@PathVariable(name = "acpID") Integer acpID,
                                                       @RequestParam(name = "email", required = false) String email,
                                                       @RequestParam(name = "name", required = false) String name,
                                                       @RequestParam(name = "telephone", required = false) String telephone,
                                                       @RequestParam(name = "city", required = false) String city,
                                                       @RequestParam(name = "address", required = false) String address) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(adminService.updateACPDetails(acpID, email, name, telephone, city, address));
    }

    /** This method returns the details associated with a specific ACP */
    @GetMapping("/acp/{acpID}")
    public ResponseEntity<AssociatedCollectionPoint> getACPDetails(@PathVariable(name = "acpID") Integer acpID) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(adminService.getACPDetails(acpID));
    }

    /** Deletes an ACP */
    @DeleteMapping ("/acp/{acpID}")
    public ResponseEntity<SuccessfulRequest> deleteACP(@PathVariable(name = "acpID") Integer acpID) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(adminService.removeACP(acpID));
    }



    // E-Store Methods


    /** This method returns all the E-Stores associated with the Platform */
    @GetMapping("/estores")
    public ResponseEntity<List<Store>> getAllStores(){
        return ResponseEntity.ok().body(adminService.getAllStores());
    }


    // Operational Statistics


    /** This method returns the statistics associated with all ACPs */
    @GetMapping("/acp/statistics")
    public ResponseEntity<Map<Integer, Map<String, Integer>>> getAllACPStatistics(){
        return ResponseEntity.ok().body(adminService.getAllACPStatistics());
    }

    /** This method returns the statistics associated with a specific ACP */
    @GetMapping("/acp/{acpID}/statistics")
    public ResponseEntity<Map<String, Integer>> getACPStatistics(@PathVariable(name = "acpID") Integer acpID) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(adminService.getSpecificACPStatistics(acpID));
    }


    // Management of Parcels


    /** This method returns all the parcels waiting for delivery */
    @GetMapping("/parcels/all/delivery")
    public ResponseEntity<List<Parcel>> getAllParcelsWaitingDelivery(){
        return ResponseEntity.ok().body(adminService.getAllParcelsWaitingDelivery());
    }

    /** This method returns all the parcels waiting for pickup */
    @GetMapping("/parcels/all/pickup")
    public ResponseEntity<List<Parcel>> getAllParcelsWaitingPickup(){
        return ResponseEntity.ok().body(adminService.getAllParcelsWaitingPickup());
    }

    /** This method returns all the parcels waiting for delivery at a specific ACP */
    @GetMapping("/parcels/{acpID}/delivery")
    public ResponseEntity<List<Parcel>> getAllParcelsWaitingDeliveryAtACP(@PathVariable(name = "acpID") Integer acpID) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(adminService.getParcelsWaitingDeliveryAtACP(acpID));
    }

    /** This method returns all the parcels waiting for pickup at a specific ACP */
    @GetMapping("/parcels/{acpID}/pickup")
    public ResponseEntity<List<Parcel>> getAllParcelsWaitingPickupAtACP(@PathVariable(name = "acpID") Integer acpID) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(adminService.getParcelsWaitingPickupAtACP(acpID));
    }


    // Management of Pending ACP's


    /** Adds a new ACP to the pending list */
    @PostMapping("/acp/pending")
    public ResponseEntity<PendingACP> addNewPendingACP(@RequestParam(name = "name") String name,
                                                       @RequestParam(name = "email") String email,
                                                       @RequestParam(name = "city") String city,
                                                       @RequestParam(name = "address") String address,
                                                       @RequestParam(name = "telephoneNumber") String telephoneNumber,
                                                       @RequestParam(name = "description") String description) {

        return ResponseEntity.ok().body(adminService.addNewPendingAcp(name, email, city, address, telephoneNumber, description));
    }

    /** Gets all of the candidate ACP's  */
    @GetMapping("/acp/pending")
    public ResponseEntity<List<PendingACP>> getPendingACP(){return ResponseEntity.ok().body(adminService.getAllPendingACP());}

    /** Changes the status of a candidate ACP */
    @PutMapping("/acp/pending/{acpID}/status")
    public ResponseEntity<SuccessfulRequest> changePendingACPStatus(@PathVariable(name = "acpID") Integer candidateID,
                                                                    @RequestParam(name = "newStatus") Integer newStatus) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(adminService.changePendingACPStatus(candidateID, newStatus));
    }
}
