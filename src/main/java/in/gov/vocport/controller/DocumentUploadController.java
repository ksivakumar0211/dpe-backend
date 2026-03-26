package in.gov.vocport.controller;

import in.gov.vocport.dto.CtThDocUploadRequestDto;
import in.gov.vocport.entities.CtThDocUpload;
import in.gov.vocport.service.DocumentUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doc")
@RequiredArgsConstructor
public class DocumentUploadController {
    private final DocumentUploadService documentUploadService;

    @GetMapping("/get/vessels")
    public ResponseEntity getVesselsNo(@RequestParam(required = false) String vesselsNo, @RequestParam int page,
                                       @RequestParam int size) {
        Map<String, Object> result = new HashMap<>();
        documentUploadService.getVesselsNo(vesselsNo, page, size, result);
        return result.containsKey("error") ? new ResponseEntity<>(result, HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity save(@ModelAttribute CtThDocUploadRequestDto request, @RequestParam String userId, @RequestParam String agentCode) throws IOException {
        Map<String, Object> result = new HashMap<>();
        documentUploadService.save(request, userId, agentCode, result);
        return result.containsKey("error") ? new ResponseEntity<>(result, HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestBody MultipartFile file) throws IOException {
        if (file == null || file.isEmpty() || file.getSize() > 500 * 1024) {
            throw new IllegalArgumentException("File should not be empty and size must be less than 500 KB");
        }
        Map<String, Object> result = new HashMap<>();
        documentUploadService.uploadFile(file, result);
        return result.containsKey("error") ? new ResponseEntity<>(result, HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("/download")
    public ResponseEntity downloadFile(@RequestParam String fileName) throws IOException {
        byte[] file = documentUploadService.downloadableFiles(fileName);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    @GetMapping("/get-doc")
    public ResponseEntity getDoc(@RequestParam String vesselsNo, @RequestParam(required = false) String agentCode) {
        Map<String, Object> result = new HashMap<>();
        documentUploadService.getDoc(vesselsNo, agentCode, result);
        return result.containsKey("error") ? new ResponseEntity<>(result, HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("/get-agents")
    public ResponseEntity getAgentList(@RequestParam(required = false) String search, @RequestParam int pageNo, @RequestParam int pageSize) {
        Map<String, Object> result = new HashMap<>();
        documentUploadService.getAgentList(search, pageNo, pageSize, result);
        return result.containsKey("error") ? new ResponseEntity<>(result, HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/get-document-type")
    public ResponseEntity getDocumentType() {
        Map<String, Object> result = new HashMap<>();
        documentUploadService.getDocumentType(result);
        return result.containsKey("error") ? new ResponseEntity<>(result, HttpStatus.BAD_REQUEST) : new ResponseEntity<>(result, HttpStatus.OK);
    }
}
