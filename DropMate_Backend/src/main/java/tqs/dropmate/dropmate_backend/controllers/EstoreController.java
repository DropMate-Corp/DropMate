package tqs.dropmate.dropmate_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.exceptions.ResourceNotFoundException;
import tqs.dropmate.dropmate_backend.services.AdminService;
import tqs.dropmate.dropmate_backend.services.StoreService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("dropmate/estore_api")
@CrossOrigin(origins = {"https://dropmate-corp.github.io/DropMate-UI/","https://floralfiestaapi.azurewebsites.net/"})
public class EstoreController {
    private StoreService storeService;
    private AdminService adminService;

    public EstoreController(StoreService storeService, AdminService adminService) {
        this.storeService = storeService;
        this.adminService = adminService;
    }

    /** Used to post a new order placed by a Client of the partner E-Store */
    @PostMapping("/parcel")
    public ResponseEntity<Map<String, String>> createNewOrder(@RequestParam(name = "acpID") Integer acpID,
                                                 @RequestParam(name = "storeID") Integer storeID
                                                 ) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(storeService.createNewOrder(acpID, storeID));
    }

    /** Gets all of the ACP's available to take new orders, that is, the ACP's currently under their operational limit */
    @GetMapping("/acp")
    public ResponseEntity<List<AssociatedCollectionPoint>> getAvailableACP(@RequestParam(name = "storeID") Integer storeID) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(storeService.getAvailableACP(storeID));
    }

    /** Returns the current status of a parcel, as well as it's delivery/pickup date, if available. These are returned in a Map.
     The input is the delivery code of the parcel. */
    @GetMapping("/parcel/{pickupCode}")
    public ResponseEntity<Map<String, String>> getParcelStatus(@PathVariable(name = "pickupCode") String pickupCode) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(storeService.getParcelStatus(pickupCode));
    }

    /** Gets the information about a specific ACP */
    @GetMapping("/acp/{acpID}")
    public ResponseEntity<AssociatedCollectionPoint> getACPDetails(@PathVariable(name = "acpID") Integer acpID) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(adminService.getACPDetails(acpID));
    }
}
