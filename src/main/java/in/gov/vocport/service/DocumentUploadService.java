package in.gov.vocport.service;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileDispositionInformation;
import com.hierynomus.msfscc.fileinformation.FileInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import in.gov.vocport.config.SmbProperties;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_OVERWRITE_IF;

@Service
@RequiredArgsConstructor
public class DocumentUploadService {
    private final GenericProcedureRepository repository;
    private final CtThDocUploadRepository ctThDocUploadRepository;
    private final SmbProperties properties;

    public void getVesselsNo(String vesselsNo, int page, int size, Map<String, Object> result) {
//        List<ProcedureKeyValueDTO> parameters = new ArrayList<>();
//        parameters.add(new ProcedureKeyValueDTO("p_vessel_no", vesselsNo, String.class, ParameterMode.IN));
//        parameters.add(new ProcedureKeyValueDTO("p_refcur_vessel_info", null, void.class, ParameterMode.REF_CURSOR));
//        List<VesselsInfoDto> vesselsInfos = (List<VesselsInfoDto>) repository.callStoredProcedure("CT_DPE_PKG.GET_VESSEL_INFO_PR", parameters, new ArrayList<VesselsInfoDto>(), "vesselsInfoDto");

        Pageable pageable = PageRequest.of(page, size);
        Page<VesselsInfoDto> vesselsInfos = ctThDocUploadRepository.findVessels(vesselsNo, pageable);
        result.put("success", vesselsInfos);
    }

    public void uploadFile(MultipartFile file, Map<String, Object> result) throws IOException {
        SMBClient client = new SMBClient();

        try (Connection connection = client.connect(properties.getServer())) {

            AuthenticationContext ac =
                    new AuthenticationContext(properties.getUsername(), properties.getPassword().toCharArray(), "");

            Session session = connection.authenticate(ac);

            try (DiskShare diskShare = (DiskShare) session.connectShare(properties.getShare())) {
                String fileName = String.valueOf(LocalDateTime.now()).replace('/', '-').concat(file.getOriginalFilename());

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

                result.put("success", "File Uploaded successfully with the".concat(fileName));
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
}
