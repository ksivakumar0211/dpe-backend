package in.gov.vocport.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import in.gov.vocport.config.SmbProperties;
import in.gov.vocport.dto.AgentProjection;
import in.gov.vocport.dto.CtThDocUploadRequestDto;
import in.gov.vocport.dto.PagedResponse;
import in.gov.vocport.dto.VesselsInfoDto;
import in.gov.vocport.entities.CtTdDocUpload;
import in.gov.vocport.entities.CtThDocUpload;
import in.gov.vocport.repository.CommonSearchOptionRepository;
import in.gov.vocport.repository.CtThDocUploadRepository;
import in.gov.vocport.repository.GenericProcedureRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_OVERWRITE_IF;

@Service
@RequiredArgsConstructor
public class DocumentUploadService {
    private final GenericProcedureRepository repository;
    private final CtThDocUploadRepository ctThDocUploadRepository;
    private final SmbProperties properties;
    private final ObjectMapper mapper;
    private final CommonSearchOptionRepository commonSearchOptionRepository;

    public void getVesselsNo(String vesselsNo, int page, int size, Map<String, Object> result) {
//        List<ProcedureKeyValueDTO> parameters = new ArrayList<>();
//        parameters.add(new ProcedureKeyValueDTO("p_vessel_no", vesselsNo, String.class, ParameterMode.IN));
//        parameters.add(new ProcedureKeyValueDTO("p_refcur_vessel_info", null, void.class, ParameterMode.REF_CURSOR));
//        List<VesselsInfoDto> vesselsInfos = (List<VesselsInfoDto>) repository.callStoredProcedure("CT_DPE_PKG.GET_VESSEL_INFO_PR", parameters, new ArrayList<VesselsInfoDto>(), "vesselsInfoDto");

//        int page = pageable.getPageNumber();
//        int size = pageable.getPageSize();

//        int startRow = page * size;
//        int endRow = startRow + size;
//        Pageable pageable = PageRequest.of(page, size);
//        List<VesselsInfoDto> vesselsInfos = ctThDocUploadRepository.findVessels(vesselsNo, startRow, endRow);
        PagedResponse<VesselsInfoDto> vesselsInfos = commonSearchOptionRepository.findVessels(vesselsNo, page, size);
        result.put("success", vesselsInfos);
    }

    public void uploadFile(MultipartFile file, Map<String, Object> result) throws IOException {
        SMBClient client = new SMBClient();

        try (Connection connection = client.connect(properties.getServer())) {

            AuthenticationContext ac =
                    new AuthenticationContext(properties.getUsername(), properties.getPassword().toCharArray(), "");

            Session session = connection.authenticate(ac);

            try (DiskShare diskShare = (DiskShare) session.connectShare(properties.getShare())) {
                String name = String.valueOf(LocalDateTime.now()).replace('/', '-').concat("_").concat(file.getOriginalFilename());
                String fileName = name.replace(':', '-');

                File smbFile = diskShare.openFile(
                        fileName,
                        EnumSet.of(AccessMask.GENERIC_WRITE),
                        EnumSet.of(FileAttributes.FILE_ATTRIBUTE_NORMAL),
                        null,
                        FILE_OVERWRITE_IF,
                        null
                );

                try (OutputStream os = smbFile.getOutputStream()) {
                    os.write(file.getBytes());
                }

                result.put("success", "File Uploaded successfully with the ".concat(fileName));
                result.put("fileName", fileName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public byte[] downloadableFiles(String fileName) throws IOException {
        SMBClient client = new SMBClient();

        try (Connection connection = client.connect(properties.getServer())) {

            AuthenticationContext ac = new AuthenticationContext(
                    properties.getUsername(),
                    properties.getPassword().toCharArray(),
                    ""
            );

            Session session = connection.authenticate(ac);

            try (DiskShare share = (DiskShare) session.connectShare(properties.getShare())) {

                File file = share.openFile(
                        fileName,
                        EnumSet.of(AccessMask.GENERIC_READ),
                        null,
                        null,
                        SMB2CreateDisposition.FILE_OPEN,
                        null
                );

                try (InputStream is = file.getInputStream()) {
                    return is.readAllBytes();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void save(CtThDocUploadRequestDto request, String userId, Map<String, Object> result) {
        CtThDocUpload savedCtThDocUpload = ctThDocUploadRepository.findById(request.getVesselNo()).orElse(null);
        LocalDate currentTime = LocalDate.now();
        if (savedCtThDocUpload == null) {
            CtThDocUpload ctThDocUpload = new CtThDocUpload();
            BeanUtils.copyProperties(request, ctThDocUpload, "documents");
            List<CtTdDocUpload> details = new ArrayList<>();
            AtomicLong srlNo = new AtomicLong(1);
            request.getDocuments().forEach(dto -> {
                CtTdDocUpload ctTdDocUpload = new CtTdDocUpload();
                BeanUtils.copyProperties(dto, ctTdDocUpload, "file");
                MultipartFile file = dto.getFile();
                ctTdDocUpload.setCreatedBy(userId);
                ctTdDocUpload.setCreatedOn(currentTime);
                ctTdDocUpload.setDccUploadPath(properties.getPath());
                Map<String, Object> resp = new HashMap<>();
                try {
                    uploadFile(file, resp);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (resp.containsKey("error")) throw new RuntimeException("Unable to save the document");
                ctTdDocUpload.setDccFileName((String) resp.get("fileName"));
                ctTdDocUpload.setDccDownLink((String) resp.get("fileName"));
                ctTdDocUpload.setVesselNo(ctThDocUpload.getVesselNo());
                ctTdDocUpload.setSrlNo(srlNo.get());
                srlNo.updateAndGet(v -> v + 1);
                details.add(ctTdDocUpload);
            });
            ctThDocUpload.setDocuments(details);
            ctThDocUpload.setCreatedBy(userId);
            ctThDocUpload.setCreatedOn(currentTime);
            result.put("success", ctThDocUploadRepository.save(ctThDocUpload));
        } else {
            AtomicLong srlNo = new AtomicLong(savedCtThDocUpload.getDocuments().size() + 1);
            request.getDocuments().forEach(dto -> {

                if (dto.getSrlNo() == null) {
                    CtTdDocUpload ctTdDocUpload = new CtTdDocUpload();
                    BeanUtils.copyProperties(dto, ctTdDocUpload, "file");
                    MultipartFile file = dto.getFile();
                    ctTdDocUpload.setCreatedBy(userId);
                    ctTdDocUpload.setCreatedOn(LocalDate.now());
                    ctTdDocUpload.setDccUploadPath(properties.getPath());
                    Map<String, Object> resp = new HashMap<>();
                    try {
                        uploadFile(file, resp);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (resp.containsKey("error")) throw new RuntimeException("Unable to save the document");
                    ctTdDocUpload.setDccFileName((String) resp.get("fileName"));
                    ctTdDocUpload.setDccDownLink((String) resp.get("fileName"));
                    ctTdDocUpload.setVesselNo(savedCtThDocUpload.getVesselNo());
                    ctTdDocUpload.setSrlNo(srlNo.get());
                    srlNo.updateAndGet(v -> v + 1);
                    savedCtThDocUpload.getDocuments().add(ctTdDocUpload);
                } else if (StringUtils.isNotBlank(dto.getCancelFlag()) && dto.getCancelFlag().equalsIgnoreCase("Y")) {
                    savedCtThDocUpload.getDocuments()
                            .stream()
                            .filter(doc -> Objects.equals(doc.getSrlNo(), dto.getSrlNo())
                                    && "N".equalsIgnoreCase(doc.getCancelFlag()))
                            .findFirst()
                            .ifPresent(doc -> {
                                doc.setCancelFlag("Y");
                                doc.setModifiedBy(userId);
                                doc.setModifiedOn(currentTime);
                            });
                }
            });

            result.put("success", ctThDocUploadRepository.save(savedCtThDocUpload));
        }
    }

    public void getDoc(String vesselsNo, String agentCode, Map<String, Object> result) {
        CtThDocUpload ctThDocUpload = ctThDocUploadRepository.findById(vesselsNo).orElse(null);
        if (ctThDocUpload != null && StringUtils.isBlank(agentCode)) result.put("success", ctThDocUpload);
        else {
            List<CtTdDocUpload> filtedList = ctThDocUpload != null ? ctThDocUpload.getDocuments()
                    .stream()
                    .filter(doc -> doc.getAgentCustomerId().equals(agentCode))
                    .toList() : null;

            if (filtedList == null || filtedList.isEmpty()) result.put("error", "No Document Added Yet");
            else result.put("success", filtedList);
        }
    }

    public void getAgentList(String search, int pageNo, int pageSize, Map<String, Object> result) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        search = StringUtils.isBlank(search) ? null : search.toUpperCase();
        Page<AgentProjection> agentList = ctThDocUploadRepository.findAgentsWithPagination(search, pageable);
        result.put("success", agentList);
    }
}
