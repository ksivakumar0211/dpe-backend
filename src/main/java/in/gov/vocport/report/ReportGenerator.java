package in.gov.vocport.report;


import in.gov.vocport.exception.ReportGenerationException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ReportGenerator {
    private final DataSource primary;
    private final ExportReport exportReport;
    private static final Map<String, String> whiteList = Map.ofEntries(Map.entry("sample", "sample"));


//    private static final Map<String, String> moduleNameWithReportName = Map.ofEntries(Map.entry("Direct Port Entry Service Charges-Add", "DPE_Bill_PDF.jrxml"));
    public void generateGenericReport(String doc, String object, HttpServletResponse response, String fileName, String reportName) throws ReportGenerationException {
//        String reportName = moduleNameWithReportName.get(moduleName);

        try (Connection connection = primary.getConnection()){
            JSONObject jsonObject = new JSONObject(object);
            //1. Create Required Parameters
            Map<String, Object> parameters = new HashMap<>();
            getAllParameter(parameters, jsonObject);

            if (whiteList.containsKey(reportName)) {
                // Get the subreport directory path using ClassLoader
                ClassLoader classLoader = getClass().getClassLoader();
                URL subReportUrl = classLoader.getResource("xsd/".concat(whiteList.get(reportName)));
                if (subReportUrl == null) {
                    throw new ReportGenerationException("Subreport file not found.");
                }

                // Convert URL to File
                File subReportJrxmlFile = new File(subReportUrl.toURI());
                String subReportJrxmlPath = subReportJrxmlFile.getAbsolutePath();

                // Compile the subreport if necessary (from .jrxml to .jasper)
                String subReportJasperPath = subReportJrxmlPath.replace(".jrxml", ".jasper");

                // Check if the compiled .jasper file exists, otherwise compile it
                File subJasperFile = new File(subReportJasperPath);
                if (!subJasperFile.exists()) {
                    JasperCompileManager.compileReportToFile(subReportJrxmlPath, subReportJasperPath);
                }

                // Set the subreport and other parameters
                String subReportDir = subJasperFile.getParent();
                parameters.put("SUBREPORT_DIR", subReportDir.concat("/"));
                parameters.put("REPORT_CONNECTION", connection);
            }
            String newReportName = reportName.replace(".jrxml", ".jasper");
//            String path = ResourceUtils.getFile("classpath:xsd/".concat(newReportName)).getAbsolutePath();
            try (InputStream jasperInputStream = getClass().getResourceAsStream("/xsd/" + newReportName)) {
//                InputStream jasperInputStream = getClass().getResourceAsStream("/xsd/" + newReportName);

                if (jasperInputStream == null) {
                    throw new FileNotFoundException("Jasper file not found in classpath at /xsd/" + reportName);
                }

                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperInputStream);
//            JasperReport jasperReport = JasperCompileManager.compileReport(path);

//            if (!whiteList.containsKey(reportName)) {
//                JRParameter[] jrParameters = jasperReport.getParameters();
//                AtomicInteger count = new AtomicInteger(0);
//                Arrays.stream(jrParameters).forEach(parameter -> {
//                    if (!parameter.isSystemDefined()) count.updateAndGet(v -> v + 1);
//                });
//
//                if (count.get() != parameters.size()) throw new ReportGenerationException("Fill All The Parameter");
//            }

                JRParameter[] jrParameters = jasperReport.getParameters();
                List<String> reportParameters = new ArrayList<>();
                AtomicInteger count = new AtomicInteger(0);
                Arrays.stream(jrParameters).forEach(parameter -> {
                    if (!parameter.isSystemDefined()) {
                        reportParameters.add(parameter.getName());
                        count.updateAndGet(v -> v + 1);
                    }
                });

                if (count.get() < parameters.size() - 1) throw new ReportGenerationException("Fill All The Parameter");
                if (!parameterChecker(parameters, reportParameters)) throw new ReportGenerationException("Report Parameters are wrong with the report template");

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
//                DataSourceUtils.releaseConnection(connection, primary);

                exportReport.exportJasperReportBytes(jasperPrint, doc, response, fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (JRException exception) {
            throw new ReportGenerationException(exception.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void getAllParameter(Map<String, Object> parameters, JSONObject jsonObject) throws JSONException {
        JSONArray keys = jsonObject.names ();

        for (int i = 0; i < keys.length (); i++) {
            String key = keys.getString (i);
            String value = jsonObject.getString (key);
            parameters.put(key, value);
        }
    }

    private boolean parameterChecker(Map<String, Object> parameters, List<String> reportParameters) {
        return reportParameters.stream().allMatch(parameters::containsKey);
    }
}
