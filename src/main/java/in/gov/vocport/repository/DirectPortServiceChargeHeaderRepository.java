package in.gov.vocport.repository;

import in.gov.vocport.entities.CtThDirectServiceChg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface DirectPortServiceChargeHeaderRepository extends JpaRepository<CtThDirectServiceChg, String> {
    CtThDirectServiceChg findByChitNoAndContainerNo(String chitNo, String containerNo);
    @Query(value = "select CT_DPE_PKG.FN_GET_PAYMENT_NO(:p_cfs_no) from dual", nativeQuery = true)
    String findPaymentNo(@Param("p_cfs_no") String cfsNo);

    @Query(value = "select CT_DPE_PKG.FN_GET_PAYMENT_DATE(:p_cfs_no) from dual", nativeQuery = true)
    LocalDate findPaymentDate(@Param("p_cfs_no") String cfsNo);

    @Query(value = "select CT_DPE_PKG.FN_GET_REF_TRAN_NO(:p_prefix, :p_cfs_date) from dual", nativeQuery = true)
    String findRefTransNo(@Param("p_prefix") String prefix, @Param("p_cfs_date") LocalDate cfsDate);
}
