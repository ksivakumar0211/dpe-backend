package in.gov.vocport.repository;

import in.gov.vocport.dto.VesselsInfoDto;
import in.gov.vocport.entities.CtThDocUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CtThDocUploadRepository extends JpaRepository<CtThDocUpload, String> {
    @Query(value = """
        SELECT vn.vessel_no,
               vn.calinv_vcn AS vcn,
               vn.vessel_name,
               vn.berthed_time,
               vn.agent_customer_name,
               vn.agent_customer_id,
               vn.zone_id
        FROM DPE_VESSEL_NO_NAME_VW vn
        WHERE (:vesselNo IS NULL OR vn.vessel_no LIKE '%' || :vesselNo || '%')
        ORDER BY SUBSTR(vn.vessel_no,4) DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM DPE_VESSEL_NO_NAME_VW vn
        WHERE (:vesselNo IS NULL OR vn.vessel_no LIKE '%' || :vesselNo || '%')
        """,
            nativeQuery = true)
    Page<VesselsInfoDto> findVessels(@Param("vesselNo") String vesselNo, Pageable pageable);
}
