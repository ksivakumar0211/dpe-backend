//package in.gov.vocport.report;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/report")
//@RequiredArgsConstructor
//public class ReportController {
//    private final ReportGenerator reportGenerator;
//    @PostMapping("/generate")
//    public ResponseEntity generatePdf(@RequestParam String reportName, @RequestBody Map<String,Object> filters) throws Exception {
//        byte[] reportData = reportGenerator.generateReport(reportName, filters);
//        return ResponseEntity.ok()
//                .header("Content-Disposition","attachment; filename=report.pdf")
//                .body(reportData);
//    }
//}
