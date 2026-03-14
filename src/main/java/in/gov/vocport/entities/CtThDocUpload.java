package in.gov.vocport.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.aot.hint.SerializationHints;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "CT_TH_DOC_UPLOAD")
public class CtThDocUpload {
    @Id
    @Column(name = "VESSEL_NO")
    private String vesselNo;

    @Column(name = "VCN")
    private String vcn;

    @Column(name = "VESSEL_NAME")
    private String vesselName;

    @Column(name = "BERTHED_TIME")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime berthedTime;

    @Column(name = "AGENT_CUSTOMER_ID")
    private String agentCustomerId;

    @Column(name = "AGENT_CUSTOMER_NAME")
    private String agentCustomerName;

    @Column(name = "ZONE_ID")
    private String zoneId;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_ON")
    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate createdOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "MODIFIED_ON")
    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate modifiedOn;

    @JsonManagedReference
    @OneToMany(mappedBy = "header", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CtTdDocUpload> documents;
}
