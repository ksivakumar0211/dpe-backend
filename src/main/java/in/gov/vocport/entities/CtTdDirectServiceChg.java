package in.gov.vocport.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "CT_TD_DIRECT_SERVICE_CHG")
@Data
public class CtTdDirectServiceChg {

    @EmbeddedId
    private CtTdDirectServiceChgId id;

    @Column(name = "CFS_NO", length = 20)
    private String cfsNo;

    @Column(name = "CFS_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate cfsDate;

    @Column(name = "SERVICE_TYPE_CD", length = 10)
    private String serviceTypeCd;

    @Column(name = "SERVICE_FROM_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate serviceFromDate;

    @Column(name = "SERVICE_TO_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate serviceToDate;

    @Column(name = "RATE")
    private Double rate;

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "CGST")
    private Double cgst;

    @Column(name = "SGST")
    private Double sgst;

    @Column(name = "IGST")
    private Double igst;

    @Column(name = "TOTAL_VAL")
    private Double totalVal;

    @Column(name = "PAYMENT_NO", length = 100)
    private String paymentNo;

    @Column(name = "PAYMENT_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime paymentDate;

    @Column(name = "SERVICE_REMARKS", length = 1000)
    private String serviceRemarks;

    @Column(name = "CANCEL_FLAG", length = 1)
    private String cancelFlag;

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

    @Column(name = "REF_TRAN_NO", length = 20)
    private String refTranNo;

    @Lob
    @Column(name = "JSON_FILE")
    private String jsonFile;

    @Column(name = "P2PREQUESTID", length = 30)
    private String e2pRequestId;

    @Column(name = "STATUS", length = 100)
    private String status;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "CHIT_NO", referencedColumnName = "CHIT_NO", updatable = false, insertable = false)
    private CtThDirectServiceChg thDirectServiceChg;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CtTdDirectServiceChg that = (CtTdDirectServiceChg) o;
        return Objects.equals(id, that.id) && Objects.equals(cfsNo, that.cfsNo) && Objects.equals(cfsDate, that.cfsDate) && Objects.equals(serviceTypeCd, that.serviceTypeCd) && Objects.equals(serviceFromDate, that.serviceFromDate) && Objects.equals(serviceToDate, that.serviceToDate) && Objects.equals(rate, that.rate) && Objects.equals(amount, that.amount) && Objects.equals(cgst, that.cgst) && Objects.equals(sgst, that.sgst) && Objects.equals(igst, that.igst) && Objects.equals(totalVal, that.totalVal) && Objects.equals(paymentNo, that.paymentNo) && Objects.equals(paymentDate, that.paymentDate) && Objects.equals(serviceRemarks, that.serviceRemarks) && Objects.equals(cancelFlag, that.cancelFlag) && Objects.equals(refTranNo, that.refTranNo) && Objects.equals(jsonFile, that.jsonFile) && Objects.equals(e2pRequestId, that.e2pRequestId) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cfsNo, cfsDate, serviceTypeCd, serviceFromDate, serviceToDate, rate, amount, cgst, sgst, igst, totalVal, paymentNo, paymentDate, serviceRemarks, cancelFlag, refTranNo, jsonFile, e2pRequestId, status);
    }
}
