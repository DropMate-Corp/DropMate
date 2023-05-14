package tqs.dropmate.dropmate_backend.controllers;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.dropmate.dropmate_backend.datamodel.AssociatedCollectionPoint;
import tqs.dropmate.dropmate_backend.datamodel.Parcel;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("dropmate/estore_api")
@CrossOrigin
public class EstoreController {

    // Used to post a new order placed by a Client of the partner E-Store
    @PostMapping("/parcel")
    public ResponseEntity<Parcel> createNewOrder(@RequestParam(name = "parcel") Parcel parcel){
        return null;
    }

    // Gets all of the ACP's available to take new orders, that is, the ACP's currently under their operational limit
    @GetMapping("/acp")
    public ResponseEntity<List<AssociatedCollectionPoint>> getAvailableACP(){
        return null;
    }

    // Returns the current status of a parcel, as well as it's delivery/pickup date, if available. These are returned in a Map.
    // The input is the delivery code of the parcel.
    @GetMapping("/parcel/{parcelCode}")
    public RequestEntity<Map<String, String>> getParcelStatus(@PathVariable(name = "parcelCode") String parcelCode){
        return null;
    }

    // Gets the information about a specific ACP
    @GetMapping("/acp/{acpID}")
    public RequestEntity<AssociatedCollectionPoint> getACPDetails(@PathVariable(name = "acpID") Long acpID){
        return null;
    }
}
