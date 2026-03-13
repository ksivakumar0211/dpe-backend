package in.gov.vocport.service;

import in.gov.vocport.dto.ProcedureKeyValueDTO;
import in.gov.vocport.repository.GenericProcedureRepository;
import jakarta.persistence.ParameterMode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsynchronousService {
    private final GenericProcedureRepository genericProcedureRepository;
    @Async
    public void populateInInvoiceTable(List<String> cfsList, String createdBy) {
        cfsList.forEach(cfs -> {
            List<ProcedureKeyValueDTO> parameters = new ArrayList<>();
            parameters.add(new ProcedureKeyValueDTO("p_cfs_no", cfs, String.class, ParameterMode.IN));
            parameters.add(new ProcedureKeyValueDTO("p_created_by", createdBy, String.class, ParameterMode.IN));

            genericProcedureRepository.callStoredProcedure("CT_DPE_PKG.CT_DPE_INVOICE_POPULATION_PR", parameters, null);
        });
    }
}
