package in.gov.vocport.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.gov.vocport.dto.AgentDto;
import in.gov.vocport.dto.CargoDto;
import in.gov.vocport.dto.ContainerInPortDto;
import in.gov.vocport.dto.ContainerNoDto;
import in.gov.vocport.dto.LinerDto;
import in.gov.vocport.dto.LocationDto;
import in.gov.vocport.dto.PagedResponse;
import in.gov.vocport.dto.PortDto;
import in.gov.vocport.dto.ShipperDto;
import in.gov.vocport.dto.VesselDto;
import in.gov.vocport.service.AddEditServiceCharge;
import in.gov.vocport.service.ContainerGateInService;
import in.gov.vocport.service.ContainerGateOutService;

@RestController
public class CommonSearchOptionController {

	@Autowired
    private ContainerGateInService gateInService;
    
	@Autowired
    private ContainerGateOutService gateOutService;
	
	@Autowired
	private AddEditServiceCharge addEditServiceCharge;
	
    @GetMapping("/ports")
    public PagedResponse<PortDto> getPorts(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return gateInService.searchPortOfDestinations(q, page, size);
    }

    @GetMapping("/shippers")
    public PagedResponse<ShipperDto> getShippers(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return gateInService.searchShippers(q, page, size);
    }

    @GetMapping("/vessels")
    public PagedResponse<VesselDto> getVessels(
    		@RequestParam(name = "q") String q,
    		@RequestParam(name = "page") int page,
    		@RequestParam(name = "size") int size) {

        return gateInService.searchVessels(q, page, size);
    }
    
    @GetMapping("/locations")
    public PagedResponse<LocationDto> getToLocations(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "exclude") String excludeCode,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return gateInService.searchToLocations(q, excludeCode, page, size);
    }
    
    @GetMapping("/agents")
    public PagedResponse<AgentDto> getAgents(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return gateInService.searchAgents(q, page, size);
    }
    
    @GetMapping("/cargo")
    public PagedResponse<CargoDto> getCargo(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return gateInService.searchCargo(q, page, size);
    }
    
    @GetMapping("/liners")
    public PagedResponse<LinerDto> getLiners(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return gateInService.searchLiners(q, page, size);
    }
    
    @GetMapping("/gateOutFromlocations")
    public PagedResponse<LocationDto> gateOutFromlocations(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return gateOutService.searchGateOutFromlocations(q, page, size);
    }
    
    @GetMapping("/gateOutTolocations")
    public PagedResponse<LocationDto> gateOutTolocations(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return gateOutService.searchGateOutTolocations(q, page, size);
    }
    
    @GetMapping("/gateOutContainerNo")
    public PagedResponse<ContainerNoDto> gateOutContainerNo(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return gateOutService.gateOutContainerNo(q, page, size);
    }
    
    @GetMapping("/containerNoForServiceAdd")
    public PagedResponse<ContainerNoDto> containerNoForServiceAdd(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        return addEditServiceCharge.searchContainerNoForServiceAdd(q, page, size);
    }
    
    @GetMapping("/containerInPortDetails")
    public ResponseEntity<ContainerInPortDto> getContainerInPortDetails(
            @RequestParam String containerNo) {
        ContainerInPortDto dto = addEditServiceCharge.getContainerInPortDetails(containerNo);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }


}

