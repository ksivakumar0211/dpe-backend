package in.gov.vocport.report;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportGenerator reportService;

    @PostMapping("/jasper/{fileType}/{reportName}")
    public void generateReport(@PathVariable("fileType") String fileType, @PathVariable("reportName")String reportName, @RequestBody String object, HttpServletResponse response) throws IOException {
        try {
            response.reset(); // Clear any existing data in the response
            // Reapply CORS headers after resetting the response
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");

            String fileName = "Report" + "_" + LocalDateTime.now();
            reportService.generateGenericReport(fileType, object, response, fileName, reportName);
        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(exception.getMessage());
            response.getWriter().flush();
        }
    }


    @GetMapping("/get-in/container-pr")
    public ResponseEntity gateInContainerNoPr(@RequestParam(required = false) String containerNo) throws IOException {
        Map<String, Object> result = new HashMap<>();
        reportService.gateInContainerNoPr(containerNo, result);
        return result.containsKey("success") ? new ResponseEntity<>(result, HttpStatus.OK) : new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @GetMapping ("/get-out/container-pr")
    public ResponseEntity gateOutContainerNoPr(@RequestParam(required = false) String containerNo) throws IOException {
        Map<String, Object> result = new HashMap<>();
        reportService.gateOutContainerNoPr(containerNo, result);
        return result.containsKey("success") ? new ResponseEntity<>(result, HttpStatus.OK) : new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
