package in.gov.vocport.entities;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "CT_TH_DIRECT_SERVICE_CHG")
@Data
public class CtThDirectServiceChg {

    @Id
    @Column(name = "CHIT_NO", length = 25, nullable = false)
    private String chitNo;

    @Column(name = "CONTAINER_NO", length = 11)
    private String containerNo;

    @Column(name = "GATE_IN_DATE_TIME")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime gateInDateTime;

    @Column(name = "PARTY_CD", length = 10)
    private String partyCd;

    @Column(name = "AGENT_CUSTOMER_NAME", length = 100)
    private String agentCustomerName;

    @Column(name = "BOE_NO", length = 20)
    private String boeNo;

    @Column(name = "TEN_DELIVERY_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate tenDeliveryDate;

    @Column(name = "ACT_DELIVERY_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate actDeliveryDate;

    @Column(name = "ZONE_ID", length = 10)
    private String zoneId;

    @Column(name = "CREATED_BY", length = 10)
    private String createdBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "CREATED_ON")
    private Date createdOn;

    @Column(name = "MODIFIED_BY", length = 10)
    private String modifiedBy;

    @Temporal(TemporalType.DATE)
    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;

//    @OneToMany(mappedBy = "id.chitNo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<CtTdDirectServiceChg> serviceDetails;

    @JsonManagedReference
    @OneToMany(mappedBy = "thDirectServiceChg", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<CtTdDirectServiceChg> serviceDetails;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CtThDirectServiceChg that = (CtThDirectServiceChg) o;
        return Objects.equals(chitNo, that.chitNo) && Objects.equals(containerNo, that.containerNo) && Objects.equals(gateInDateTime, that.gateInDateTime) && Objects.equals(partyCd, that.partyCd) && Objects.equals(agentCustomerName, that.agentCustomerName) && Objects.equals(boeNo, that.boeNo) && Objects.equals(tenDeliveryDate, that.tenDeliveryDate) && Objects.equals(actDeliveryDate, that.actDeliveryDate) && Objects.equals(zoneId, that.zoneId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chitNo, containerNo, gateInDateTime, partyCd, agentCustomerName, boeNo, tenDeliveryDate, actDeliveryDate, zoneId);
    }
}
