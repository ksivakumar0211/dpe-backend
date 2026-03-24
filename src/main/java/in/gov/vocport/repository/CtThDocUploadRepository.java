package in.gov.vocport.repository;

import in.gov.vocport.dto.AgentProjection;
import in.gov.vocport.dto.VesselsInfoDto;
import in.gov.vocport.entities.CtThDocUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CtThDocUploadRepository extends JpaRepository<CtThDocUpload, String> {
    @Query(value = """
    SELECT 
        a.party_cd AS partyCd,
        a.agent_nm AS agentNm
    FROM PO_MH_AGENT a
    WHERE (:search IS NULL 
           OR :search = '' 
           OR UPPER(a.party_cd) LIKE '%' || UPPER(:search) || '%'
           OR UPPER(a.agent_nm) LIKE '%' || UPPER(:search) || '%')
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM PO_MH_AGENT a
    WHERE (:search IS NULL 
           OR :search = '' 
           OR UPPER(a.party_cd) LIKE '%' || UPPER(:search) || '%'
           OR UPPER(a.agent_nm) LIKE '%' || UPPER(:search) || '%')
    """,
            nativeQuery = true)
    Page<AgentProjection> findAgentsWithPagination(@Param("search") String search, Pageable pageable);
}
