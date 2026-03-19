package in.gov.vocport.dto;

public class VesselDto {
 private String vesselNo;
 private String vesselName;
 private String voyageNumber;
 private String foriegnCostal;

 public VesselDto(String vesselNo, String vesselName, String voyageNumber, String foriegnCostal) {
     this.vesselNo = vesselNo;
     this.vesselName = vesselName;
     this.voyageNumber = voyageNumber;
     this.foriegnCostal = foriegnCostal;
 }

    public String getVesselNo() { return vesselNo; }
    public String getVesselName() { return vesselName; }
    public String getVoyageNumber() { return voyageNumber; }
    public String getForiegnCostal() {
        return foriegnCostal;
    }
}


