package in.gov.vocport.repository;

import in.gov.vocport.entities.DpeDocUploadConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocUploadConfigRepository extends JpaRepository<DpeDocUploadConfig, String> {
}
