package in.gov.vocport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContainerGateInDto {

	@NotBlank(message = "Chit No is required")
	private String chitNo;

	@NotBlank(message = "Vehicle No is required")
	@Size(max = 15, message = "Vehicle No max length is 15")
	private String vehicleNo;

	@NotBlank(message = "From Location is required")
	private String fromLocId;

	@NotBlank(message = "To Location is required")
	private String toLocId;

	private String impExpTrns; // E / I / T
	private String beSbNo; // BE/SB (maps to boe_no)
	@NotBlank(message = "CH Agent is required")
	private String chAgentCode; // party_cd

	private String vesselNo;
	private String vesselName;
	private String voyageNo;

	private String shipperName;

	@Size(max = 20, message = "Local Origin max length is 20")
	private String localOrigin;

	private String portOfDestination; // MAN_PORT_CD etc.

	@NotBlank(message = "Weightment is required")
	private String weightmentFlag; // Y/N

	@NotBlank(message = "Security Wall is required")
	private String securityWall; // I/O

	@NotBlank(message = "Gate In Through is required")
	private String gateInThrough; // Road/Rail

	@NotBlank(message = "Container No is required")
	@Size(min = 11, max = 11, message = "Container No must be 11 characters")
	private String containerNo;

	@NotBlank(message = "Container status is required")
	private String containerStatus; // e.g. "20,Load"

	private String cargoName; // description

	private String packages; // bags

	@NotBlank(message = "Quantity is required")
	private String quantity; // you can later convert to BigDecimal

	private String linerCode; // party_cd
	
	private String linerName;

	private String eir;

	private String icdCfsFcs; // C/I/F/N
	private String hazardous; // Y/N
	private String customsExamination; // Y/N
	private String shutOut; // Y/N

	private String foreignCoastalFlag; // F/C (from Voyage select in container section)

	// getters & setters

	public String getChitNo() {
		return chitNo;
	}

	public void setChitNo(String chitNo) {
		this.chitNo = chitNo;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public String getFromLocId() {
		return fromLocId;
	}

	public void setFromLocId(String fromLocId) {
		this.fromLocId = fromLocId;
	}

	public String getToLocId() {
		return toLocId;
	}

	public void setToLocId(String toLocId) {
		this.toLocId = toLocId;
	}

	public String getImpExpTrns() {
		return impExpTrns;
	}

	public void setImpExpTrns(String impExpTrns) {
		this.impExpTrns = impExpTrns;
	}

	public String getBeSbNo() {
		return beSbNo;
	}

	public void setBeSbNo(String beSbNo) {
		this.beSbNo = beSbNo;
	}

	public String getChAgentCode() {
		return chAgentCode;
	}

	public void setChAgentCode(String chAgentCode) {
		this.chAgentCode = chAgentCode;
	}

	public String getVesselNo() {
		return vesselNo;
	}

	public void setVesselNo(String vesselNo) {
		this.vesselNo = vesselNo;
	}

	public String getVesselName() {
		return vesselName;
	}

	public void setVesselName(String vesselName) {
		this.vesselName = vesselName;
	}

	public String getVoyageNo() {
		return voyageNo;
	}

	public void setVoyageNo(String voyageNo) {
		this.voyageNo = voyageNo;
	}

	public String getShipperName() {
		return shipperName;
	}

	public void setShipperName(String shipperName) {
		this.shipperName = shipperName;
	}

	public String getLocalOrigin() {
		return localOrigin;
	}

	public void setLocalOrigin(String localOrigin) {
		this.localOrigin = localOrigin;
	}

	public String getPortOfDestination() {
		return portOfDestination;
	}

	public void setPortOfDestination(String portOfDestination) {
		this.portOfDestination = portOfDestination;
	}

	public String getWeightmentFlag() {
		return weightmentFlag;
	}

	public void setWeightmentFlag(String weightmentFlag) {
		this.weightmentFlag = weightmentFlag;
	}

	public String getSecurityWall() {
		return securityWall;
	}

	public void setSecurityWall(String securityWall) {
		this.securityWall = securityWall;
	}

	public String getGateInThrough() {
		return gateInThrough;
	}

	public void setGateInThrough(String gateInThrough) {
		this.gateInThrough = gateInThrough;
	}

	public String getContainerNo() {
		return containerNo;
	}

	public void setContainerNo(String containerNo) {
		this.containerNo = containerNo;
	}

	public String getContainerStatus() {
		return containerStatus;
	}

	public void setContainerStatus(String containerStatus) {
		this.containerStatus = containerStatus;
	}

	public String getCargoName() {
		return cargoName;
	}

	public void setCargoName(String cargoName) {
		this.cargoName = cargoName;
	}

	public String getPackages() {
		return packages;
	}

	public void setPackages(String packages) {
		this.packages = packages;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getLinerCode() {
		return linerCode;
	}

	public void setLinerCode(String linerCode) {
		this.linerCode = linerCode;
	}

	public String getLinerName() {
		return linerName;
	}

	public void setLinerName(String linerName) {
		this.linerName = linerName;
	}

	public String getEir() {
		return eir;
	}

	public void setEir(String eir) {
		this.eir = eir;
	}

	public String getIcdCfsFcs() {
		return icdCfsFcs;
	}

	public void setIcdCfsFcs(String icdCfsFcs) {
		this.icdCfsFcs = icdCfsFcs;
	}

	public String getHazardous() {
		return hazardous;
	}

	public void setHazardous(String hazardous) {
		this.hazardous = hazardous;
	}

	public String getCustomsExamination() {
		return customsExamination;
	}

	public void setCustomsExamination(String customsExamination) {
		this.customsExamination = customsExamination;
	}

	public String getShutOut() {
		return shutOut;
	}

	public void setShutOut(String shutOut) {
		this.shutOut = shutOut;
	}

	public String getForeignCoastalFlag() {
		return foreignCoastalFlag;
	}

	public void setForeignCoastalFlag(String foreignCoastalFlag) {
		this.foreignCoastalFlag = foreignCoastalFlag;
	}
}
