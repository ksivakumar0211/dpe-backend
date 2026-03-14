package in.gov.vocport.service;

import in.gov.vocport.dto.ProcedureKeyValueDTO;
import in.gov.vocport.dto.VesselsInfoDto;
import in.gov.vocport.repository.CtThDocUploadRepository;
import in.gov.vocport.repository.GenericProcedureRepository;
import jakarta.persistence.ParameterMode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentUploadService {
    private final GenericProcedureRepository repository;
    private final CtThDocUploadRepository ctThDocUploadRepository;

    public void getVesselsNo(String vesselsNo, int page, int size, Map<String, Object> result) {
//        List<ProcedureKeyValueDTO> parameters = new ArrayList<>();
//        parameters.add(new ProcedureKeyValueDTO("p_vessel_no", vesselsNo, String.class, ParameterMode.IN));
//        parameters.add(new ProcedureKeyValueDTO("p_refcur_vessel_info", null, void.class, ParameterMode.REF_CURSOR));
//        List<VesselsInfoDto> vesselsInfos = (List<VesselsInfoDto>) repository.callStoredProcedure("CT_DPE_PKG.GET_VESSEL_INFO_PR", parameters, new ArrayList<VesselsInfoDto>(), "vesselsInfoDto");

        Pageable pageable = PageRequest.of(page, size);
        Page<VesselsInfoDto> vesselsInfos = ctThDocUploadRepository.findVessels(vesselsNo, pageable);
        result.put("success", vesselsInfos);
    }
}
