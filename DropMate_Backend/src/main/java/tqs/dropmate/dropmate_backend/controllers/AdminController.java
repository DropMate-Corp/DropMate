package tqs.dropmate.dropmate_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.dropmate.dropmate_backend.datamodel.*;
import tqs.dropmate.dropmate_backend.services.ACPService;
import tqs.dropmate.dropmate_backend.services.AdminService;
import tqs.dropmate.dropmate_backend.services.StoreService;
import tqs.dropmate.dropmate_backend.utils.SuccessfulRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("dropmate/admin")
@CrossOrigin
public class AdminController {
    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /** Method for the DropMate administrator login */
    @PostMapping("/login")
    public ResponseEntity<SystemAdministrator> adminLogin(@RequestParam(name = "email") String email,
                                                          @RequestParam(name = "password") String password){
        return null;
    }

    /** This method returns all the ACP's associated with the Platform */
    @GetMapping("/acp")
    public ResponseEntity<List<AssociatedCollectionPoint>> getAllACP(){
        return ResponseEntity.ok().body(adminService.getAllACP());
    }

    /** This method is used to associate a new ACP with the platform */
    @PostMapping("/acp")
    public ResponseEntity<SuccessfulRequest> addACP(@RequestParam(name = "acp") AssociatedCollectionPoint acp){
        return null;
    }

    /** Updates the details of an ACP */
    @PutMapping("/acp")
    public ResponseEntity<SuccessfulRequest> updateACP(@RequestParam(name = "email", required = false) String email,
                                                       @RequestParam(name = "name", required = false) String name,
                                                       @RequestParam(name = "telephone", required = false) String telephone,
                                                       @RequestParam(name = "city", required = false) String city,
                                                       @RequestParam(name = "address", required = false) String address){
        return null;
    }

    /** This method returns the details associated with a specific ACP */
    @GetMapping("/acp/{acpID}")
    public ResponseEntity<AssociatedCollectionPoint> getACPDetails(@PathVariable(name = "acpID") Long acpID){
        return null;
    }

    /** Deletes an ACP */
    @DeleteMapping ("/acp/{acpID}")
    public ResponseEntity<SuccessfulRequest> deleteACP(@PathVariable(name = "acpID") Long acpID){
        return null;
    }

    /** This method returns all the E-Stores associated with the Platform */
    @GetMapping("/estores/")
    public ResponseEntity<List<Store>> getAllStores(){
        return null;
    }

    /** This method returns the statistics associated with all ACPs */
    @GetMapping("/acp/statistics")
    public ResponseEntity<Map<AssociatedCollectionPoint, Map<String, String>>> getAllACPStatistics(){
        return null;
    }

    /** This method returns the statistics associated with a specific ACP */
    @GetMapping("/acp/{acpID}/statistics")
    public ResponseEntity<Map<String, String>> getACPStatistics(@PathVariable(name = "acpID") Long acpID){
        return null;
    }

    /** This method returns all the parcels waiting for delivery */
    @GetMapping("/parcels/all/delivery")
    public ResponseEntity<List<Parcel>> getAllParcelsWaitingDelivery(){
        return ResponseEntity.ok().body(adminService.getAllParcelsWaitingDelivery());
    }

    /** This method returns all the parcels waiting for pickup */
    @GetMapping("/parcels/all/pickup")
    public ResponseEntity<List<Parcel>> getAllParcelsWaitingPickup(){
        return null;
    }

    /** This method returns all the parcels waiting for delivery at a specific ACP */
    @GetMapping("/parcels/{acpID}/delivery")
    public ResponseEntity<List<Parcel>> getAllParcelsWaitingDeliveryAtACP(@PathVariable(name = "acpID") Long acpID){
        return null;
    }

    /** This method returns all the parcels waiting for pickup at a specific ACP */
    @GetMapping("/parcels/{acpID}/pickup")
    public ResponseEntity<List<Parcel>> getAllParcelsWaitingPickupAtACP(@PathVariable(name = "acpID") Long acpID){
        return null;
    }

    /** Returns all the Operators associated with each ACP */
    @GetMapping("/acp/operators")
    public ResponseEntity<Map<AssociatedCollectionPoint, ACPOperator>> getAllOperators(){
        return null;
    }

    /** Adds a new ACP to the pending list */
    @PostMapping("/acp/pending")
    public ResponseEntity<SuccessfulRequest> addNewPendingACP(@RequestParam(name = "candidateACP") PendingACP candidateACP){return null;}

    /** Gets all of the candidate ACP's  */
    @GetMapping("/acp/pending")
    public ResponseEntity<List<PendingACP>> getPendingACP(){return null;}

    /** Changes the status of a candidate ACP */
    @PostMapping("/acp/pending/{acpID}/status")
    public ResponseEntity<SuccessfulRequest> changePendingACPStatus(@PathVariable(name = "candidateACP") PendingACP candidateACP,
                                                                    @RequestParam(name = "oldStatus") Integer oldStatus,
                                                                    @RequestParam(name = "newStatus") Integer newStatus) {return null;}
}
