package in.gov.vocport.repository;

import in.gov.vocport.dto.VesselsInfoDto;
import in.gov.vocport.entities.CtThDocUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CtThDocUploadRepository extends JpaRepository<CtThDocUpload, String> {
//    @Query(value = """
//        SELECT vn.vessel_no,
//               vn.calinv_vcn AS vcn,
//               vn.vessel_name,
//               vn.berthed_time,
//               vn.agent_customer_name,
//               vn.agent_customer_id,
//               vn.zone_id
//        FROM DPE_VESSEL_NO_NAME_VW vn
//        WHERE (:vesselNo IS NULL OR vn.vessel_no LIKE '%' || :vesselNo || '%')
//        ORDER BY SUBSTR(vn.vessel_no,4) DESC
//        """,
//            countQuery = """
//        SELECT COUNT(*)
//        FROM DPE_VESSEL_NO_NAME_VW vn
//        WHERE (:vesselNo IS NULL OR vn.vessel_no LIKE '%' || :vesselNo || '%')
//        """,
//            nativeQuery = true)
//    Page<VesselsInfoDto> findVessels(@Param("vesselNo") String vesselNo, Pageable pageable);

//    @Query(value = """
//    SELECT * FROM (
//        SELECT a.*, ROWNUM rnum FROM (
//            SELECT vn.vessel_no,
//                   vn.calinv_vcn AS vcn,
//                   vn.vessel_name,
//                   vn.berthed_time,
//                   vn.agent_customer_name,
//                   vn.agent_customer_id,
//                   vn.zone_id
//            FROM DPE_VESSEL_NO_NAME_VW vn
//            WHERE (:vesselNo IS NULL OR vn.vessel_no LIKE '%' || :vesselNo || '%')
//            ORDER BY SUBSTR(vn.vessel_no,4) DESC
//        ) a
//        WHERE ROWNUM <= :endRow
//    )
//    WHERE rnum > :startRow
//    """,
//            countQuery = """
//        SELECT COUNT(*)
//        FROM DPE_VESSEL_NO_NAME_VW vn
//        WHERE (:vesselNo IS NULL OR vn.vessel_no LIKE '%' || :vesselNo || '%')
//    """,
//            nativeQuery = true)

@Query(value = """
    SELECT a.vesselNo,
           a.vcn,
           a.vesselName,
           a.berthedTime,
           a.agentCustomerName,
           a.agentCustomerId,
           a.zoneId
    FROM (
        SELECT a.*, ROWNUM rnum FROM (
            SELECT vn.vessel_no AS vesselNo,
                   vn.calinv_vcn AS vcn,
                   vn.vessel_name AS vesselName,
                   vn.berthed_time AS berthedTime,
                   vn.agent_customer_name AS agentCustomerName,
                   vn.agent_customer_id AS agentCustomerId,
                   vn.zone_id AS zoneId
            FROM DPE_VESSEL_NO_NAME_VW vn
            WHERE (:vesselNo IS NULL OR TRIM(:vesselNo) = '' OR vn.vessel_no LIKE '%' || :vesselNo || '%')
            ORDER BY SUBSTR(vn.vessel_no,4) DESC
        ) a
        WHERE ROWNUM <= :endRow
    ) a
    WHERE a.rnum > :startRow
    """,
        nativeQuery = true)
    List<VesselsInfoDto> findVessels(
            @Param("vesselNo") String vesselNo,
            @Param("startRow") int startRow,
            @Param("endRow") int endRow
    );
}
