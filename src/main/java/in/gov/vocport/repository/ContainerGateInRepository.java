package in.gov.vocport.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import in.gov.vocport.entities.ContainerGateInEntity;

@Repository
public interface ContainerGateInRepository extends JpaRepository<ContainerGateInEntity, String> {
	
	List<ContainerGateInEntity> findTop1ByCreatedByOrderByCreatedOnDesc(String createdBy);
	
	List<ContainerGateInEntity> findByCreatedByAndGateOutDateTimeIsNotNullOrderByGateOutDateTimeDesc(String modifiedBy);;

	@Query(value = "SELECT CT_DPE_PKG.FN_GATE_OUT_CHECK(:p_container_no) FROM dual", nativeQuery = true)
	String findGetOutPaymentStatus(@Param("p_container_no") String containerNo);

	@Query(value = "SELECT CT_DPE_PKG.FN_LEO_STATUS_CHECK(:p_container_no) FROM dual", nativeQuery = true)
	Object findGetOutLeoStatus(String containerNo);
}
