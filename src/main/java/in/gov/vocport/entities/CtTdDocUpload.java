package in.gov.vocport.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "CT_TD_DOC_UPLOAD")
@IdClass(CtTdDocUploadId.class)
public class CtTdDocUpload {
    @Id
    @Column(name = "VESSEL_NO")
    private String vesselNo;

    @Id
    @Column(name = "SRL_NO")
    private Long srlNo;

    @Column(name = "DOCUMENT_TYPE")
    private String documentType;

    @Column(name = "DOCUMENT_REMARKS")
    private String documentRemarks;

    @Column(name = "DOC_UPLOAD_DATE")
    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate docUploadDate;

    @Column(name = "DCC_UPLOAD_PATH")
    private String dccUploadPath;

    @Column(name = "DCC_FILE_NAME")
    private String dccFileName;

    @Column(name = "DCC_DOWN_LINK")
    private String dccDownLink;

    @Column(name = "CANCEL_FLAG")
    private String cancelFlag;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "VESSEL_NO", referencedColumnName = "VESSEL_NO", insertable = false, updatable = false)
    private CtThDocUpload header;
}
