package in.gov.vocport.controller;

import in.gov.vocport.service.DocumentUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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
}
