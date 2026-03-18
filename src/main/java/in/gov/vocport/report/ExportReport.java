package in.gov.vocport.report;

import in.gov.vocport.exception.ReportGenerationException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class ExportReport {
    private final JasperReportsContext jasperReportsContext;
    public void exportJasperReportBytes(JasperPrint jasperPrint, String fileType, HttpServletResponse response, String fileName) throws JRException, IOException, ReportGenerationException {
        String documentName = "";
        switch (fileType) {
            case "TEXT":
                response.setContentType("text/plain");
                documentName = fileName.concat(".txt");
                response.setHeader("Content-Disposition", "attachment;filename=" + documentName);

                JRTextExporter exporter = new JRTextExporter();

                // Configure the exporter
                SimpleTextReportConfiguration textReportConfig = new SimpleTextReportConfiguration();
                textReportConfig.setPageWidthInChars(165);
                textReportConfig.setPageHeightInChars(95);


                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

                StringWriter stringWriter = new StringWriter();
                exporter.setExporterOutput(new SimpleWriterExporterOutput(stringWriter));
                exporter.setConfiguration(textReportConfig);

                try {
                    exporter.exportReport();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new ReportGenerationException("Error during text export: " + e.getMessage());
                }

                if (stringWriter.toString().trim().isEmpty()) throw new ReportGenerationException("No File To Be Downloaded");
                PrintWriter writer = response.getWriter();
                writer.write(stringWriter.toString());
                writer.flush();
                writer.close();
                break;
            case "EXCEL":
                response.setContentType("application/vnd.ms-excel");
                documentName = fileName.concat(".xls");
                response.setHeader("Content-Disposition", "attachment;filename=" + documentName);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                JRXlsxExporter jrXlsxExporter = new JRXlsxExporter();
                SimpleXlsxReportConfiguration reportConfigXLS = new SimpleXlsxReportConfiguration();
                reportConfigXLS.setSheetNames(new String[] { "sheet1" });
                jrXlsxExporter.setConfiguration(reportConfigXLS);
                jrXlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                jrXlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                jrXlsxExporter.exportReport();

                byte[] xlsxData = byteArrayOutputStream.toByteArray();
                if (xlsxData.length > 0) {
                    response.getOutputStream().write(xlsxData);
                } else throw new ReportGenerationException("No Data is available to download");
                response.getOutputStream().flush();
                byteArrayOutputStream.close();
                break;
            case "PDF":
                jasperPrint.setProperty("net.sf.jasperreports.export.pdf.force.linebreak.policy", "true");
                byteArrayOutputStream = new ByteArrayOutputStream();

                JRPropertiesUtil jrProps = JRPropertiesUtil.getInstance(jasperReportsContext);

                jrProps.setProperty("net.sf.jasperreports.default.font.name", "Arial");
                jrProps.setProperty("net.sf.jasperreports.default.pdf.font.name", "Arial");
                jrProps.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H");
                jrProps.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");

                // Use advanced exporter instead of default
                JRPdfExporter jrPdfExporter = new JRPdfExporter(jasperReportsContext);
                jrPdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));

                // Apply PDF configuration
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
//                configuration.setForceLineBreakPolicy(true); // 💥 This fixes layout issues like yours
                jrPdfExporter.setConfiguration(configuration);

//                JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);

                System.out.println("✅ Default font: " + JRPropertiesUtil.getInstance(jasperReportsContext).getProperty("net.sf.jasperreports.default.font.name"));
                jrPdfExporter.exportReport();
                byte[] pdfBytes = byteArrayOutputStream.toByteArray();

                // ✅ Check if the report has any data by verifying PDF size
                if (pdfBytes.length < 1024) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");    // JSON response
                    response.getOutputStream().write("{\"message\":\"No data available for the report.\"}".getBytes());
                    response.getOutputStream().flush();
                    response.getOutputStream().close();
                    return;
                }

                response.setContentType("application/pdf");
                documentName = fileName.concat(".pdf");
                response.setHeader("Content-Disposition", "attachment;filename=" + documentName);
//                JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
                response.getOutputStream().write(pdfBytes);
                response.getOutputStream().flush();
                response.getOutputStream().close();
                break;
            default:
                throw new IllegalArgumentException("Unsupported Format");
        }
    }

    /**
     * Apply monospace font override for TXT export.
     * Ensures proper column alignment when jrxml uses SansSerif or no font.
     */
    public void applyMonospaceForTxt(JasperPrint jasperPrint) {
        if (jasperPrint == null || jasperPrint.getPages() == null) {
            return;
        }

        for (JRPrintPage page : jasperPrint.getPages()) {
            for (JRPrintElement element : page.getElements()) {
                if (element instanceof JRPrintText) {
                    JRPrintText textElement = (JRPrintText) element;
                    textElement.setFontName("Courier New");  // monospace
                    textElement.setFontSize(5f);             // compact size
//                    textElement.setLineSpacingFactor(0f);
                }
            }
        }
        System.out.println("✅ Applied monospace font for TXT export.");
    }
}
