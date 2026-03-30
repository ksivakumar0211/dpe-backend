package in.gov.vocport.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.gov.vocport.commonBeans.CommonUtils;
import in.gov.vocport.dto.ProcedureKeyValueDTO;
import in.gov.vocport.entities.CtTdDirectServiceChg;
import in.gov.vocport.entities.CtTdDirectServiceChgId;
import in.gov.vocport.entities.CtThDirectServiceChg;
import in.gov.vocport.repository.DirectPortServiceChargeDetailRepository;
import in.gov.vocport.repository.DirectPortServiceChargeHeaderRepository;
import in.gov.vocport.repository.GenericProcedureRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.ParameterMode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class DirectPortServiceChargeService {
    private final DirectPortServiceChargeHeaderRepository headerRepository;
    private final DirectPortServiceChargeDetailRepository detailRepository;
    private final ObjectMapper mapper;
    private final CommonUtils utility;
    private final AsynchronousService service;
    private final GenericProcedureRepository genericProcedureRepository;

    public void addServiceCharge(/*Map<String, Object> object, String userId, Map<String, Object> result*/CtThDirectServiceChg header, String userId, Map<String, Object> result) {
        List<String> cfsList = new ArrayList<>();
        CtThDirectServiceChg alreadySavedHeader = headerRepository.findById(header.getChitNo()).orElse(null);
        if (alreadySavedHeader == null) {
            header.setCreatedBy(userId);
            header.setCreatedOn(utility.getCurrentTime());
            List<CtTdDirectServiceChg> detailList = new ArrayList<>();
            AtomicLong srlNo = new AtomicLong(1);
            header.getServiceDetails().forEach(detail -> {
                cfsList.add(detail.getCfsNo());
                detail.setId(new CtTdDirectServiceChgId());
                detail.getId().setChitNo(header.getChitNo());
                detail.getId().setSrlNo(srlNo.get());
                detail.setCreatedBy(userId);
                detail.setCreatedOn(utility.getCurrentTime());
                detailList.add(detail);
                srlNo.updateAndGet(v -> v + 1);
            });
            header.setServiceDetails(detailList);
            CtThDirectServiceChg savedHeader = headerRepository.save(header);
            service.populateInInvoiceTable(cfsList, savedHeader.getCreatedBy());
            result.put("success", savedHeader);
        } else {
            if (!alreadySavedHeader.equals(header)) {
                BeanUtils.copyProperties(header, alreadySavedHeader, new String[]{"chitNo", "createdBy", "createdOn", "serviceDetails"});
                alreadySavedHeader.setModifiedBy(userId);
                alreadySavedHeader.setModifiedOn(utility.getCurrentTime());
            }

            AtomicLong srlNo = new AtomicLong(alreadySavedHeader.getServiceDetails().size() + 1);
            header.getServiceDetails().forEach(detail -> {
                if (detail.getId() != null && detail.getId().getSrlNo() != null && StringUtils.isBlank(detail.getPaymentNo())) {
                    alreadySavedHeader.getServiceDetails().forEach(dt -> {
                        if (!dt.equals(detail) && detail.getId().getSrlNo() == dt.getId().getSrlNo()) {
                            BeanUtils.copyProperties(detail, dt, new String[]{"id", "cfsNo", "cfsDate", "thDirectServiceChg", "createdBy", "createdOn"});
                            dt.setModifiedBy(userId);
                            dt.setModifiedOn(utility.getCurrentTime());
                        }
                    });
                } else if ((detail.getId() == null ) || (detail.getId().getSrlNo() == null || detail.getId().getSrlNo() == 0)) {
                    detail.setId(new CtTdDirectServiceChgId());
                    detail.getId().setChitNo(alreadySavedHeader.getChitNo());
                    detail.getId().setSrlNo(srlNo.get());
                    detail.setCreatedOn(utility.getCurrentTime());
                    detail.setCreatedBy(userId);
                    alreadySavedHeader.getServiceDetails().add(detail);
                    srlNo.updateAndGet(v -> v + 1);
                    cfsList.add(detail.getCfsNo());
                }
            });

            CtThDirectServiceChg savedHeader = headerRepository.save(alreadySavedHeader);
            if (!cfsList.isEmpty()) service.populateInInvoiceTable(cfsList, userId);
            result.put("success", savedHeader);
        }
    }

    public void searchServiceCharge(String chitNo, String containerNo, Map<String, Object> result) {
        CtThDirectServiceChg ctThDirectServiceChg = headerRepository.findByChitNoAndContainerNo(chitNo, containerNo);
        if (ctThDirectServiceChg != null && ctThDirectServiceChg.getServiceDetails() != null) {
            ctThDirectServiceChg.getServiceDetails().forEach(dtls -> {
                dtls.setPaymentNo(headerRepository.findPaymentNo(dtls.getCfsNo()));
                dtls.setPaymentDate(headerRepository.findPaymentDate(dtls.getCfsNo()));
            });
        }
        result.put("success", ctThDirectServiceChg);
    }

    public void payStatusCheck(String cfsNo, Map<String, Object> result) {
        result.put("success", detailRepository.findGetOutPaymentStatus(cfsNo));
    }

    public void getDeliveryDate(String admissionChitNo, Map<String, Object> result) {
        result.put("success", detailRepository.findDeliveryDate(admissionChitNo));
    }

    public void updatePaymentInfo(String orderId, List<String> cfsNoList, String chitNo, Map<String, Object> resultMap) {
        CtThDirectServiceChg savedHeader = headerRepository.findById(chitNo).orElse(null);
        if (savedHeader != null) {
            savedHeader.getServiceDetails().forEach(dtls -> {
                if (cfsNoList.contains(dtls.getCfsNo())) {
                    dtls.setE2pRequestId((String) resultMap.get("p2pRequestId"));
                    dtls.setPaymentNo((String) resultMap.get("p2pRequestId"));
                    dtls.setPaymentDate(LocalDate.now());
                    dtls.setRefTranNo(orderId);
                    dtls.setStatus((String) resultMap.get("status"));
                }
            });

            CtThDirectServiceChg modifiedHeader = headerRepository.save(savedHeader);

            cfsNoList.forEach(cfs -> {
                List<ProcedureKeyValueDTO> parameters = new ArrayList<>();
                parameters.add(new ProcedureKeyValueDTO("p_cfs_no", cfs, String.class, ParameterMode.IN));
                genericProcedureRepository.callStoredProcedure("CT_DPE_PKG.UPD_INVOICE_PAY_INFO_PR", parameters, void.class);
            });
        }
    }
}
