//package in.gov.vocport.report;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;
//import in.gov.vocport.report.dto.LayoutColumn;
//import in.gov.vocport.report.dto.LayoutField;
//import in.gov.vocport.report.dto.LayoutSection;
//import in.gov.vocport.report.dto.ReportLayout;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.util.*;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ReportGenerator {
//    private final ObjectMapper objectMapper;
//
//    public byte[] generateReport(String reportName, Map<String,Object> filters) throws Exception {
//        ReportLayout layout =
//                load("reports_utility/JsonFile/"+ reportName + ".json");
//
//        Map<String,Object> header =
//                fetchHeaderData(filters);
//
//        List<Map<String,Object>> rows =
//                fetchReportData(filters);
//
//        return generate(layout, filters, header, rows);
//    }
//    public byte[] generate(ReportLayout layout,
//                           Map<String,Object> parameters,
//                           Map<String,Object> headerData,
//                           List<Map<String,Object>> tableData) throws Exception {
//
//
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//        Document document = new Document(PageSize.A4);
//        PdfWriter.getInstance(document,out);
//
//        document.open();
//
//        renderTitle(document, layout.getTitle());
//
//        renderFlowFields(document, layout.getParameters(), parameters);
//
//        renderFlowFields(document, layout.getHeaderFields(), headerData);
//
//        for(LayoutSection section : layout.getSections()){
//
//            renderTable(document, section, tableData);
//
//        }
//
//        document.close();
//
//        return out.toByteArray();
//    }
//
//    private void renderTable(Document document, LayoutSection section, List<Map<String, Object>> data) throws DocumentException, IOException {
//        List<LayoutColumn> columns = section.getColumns();
//
//        BaseFont font = BaseFont.createFont();
//
//        float fontSize = 10;
//
//        float[] widths = new float[columns.size()];
//
//        int i=0;
//
//        for(LayoutColumn column : columns){
//
//            widths[i++] = calculateColumnWidth(
//                    font,fontSize,
//                    column.getHeader(),
//                    data,
//                    column.getField());
//        }
//
//        PdfPTable table = new PdfPTable(columns.size());
//
//        table.setTotalWidth(widths);
//        table.setLockedWidth(true);
//
//        for(LayoutColumn column : columns){
//
//            PdfPCell header = new PdfPCell(new Phrase(column.getHeader()));
//
//            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
//
//            header.setHorizontalAlignment(Element.ALIGN_CENTER);
//
//            table.addCell(header);
//        }
//
//        for(Map<String,Object> row : data){
//
//            for(LayoutColumn column : columns){
//
//                Object val = row.get(column.getField());
//
//                PdfPCell cell =
//                        new PdfPCell(new Phrase(val==null?"":val.toString()));
//
//                cell.setNoWrap(true);
//
//                cell.setHorizontalAlignment(resolveAlign(column.getAlign()));
//
//                table.addCell(cell);
//            }
//        }
//
//        document.add(table);
//
//    }
//
//    private void renderFlowFields(Document document, List<LayoutField> fields, Map<String, Object> data) throws DocumentException, IOException {
//        if(fields == null) return;
//
//        BaseFont font = BaseFont.createFont();
//
//        float fontSize = 10;
//
//        float pageWidth =
//                document.getPageSize().getWidth()
//                        - document.leftMargin()
//                        - document.rightMargin();
//
//        float currentWidth = 0;
//
//        PdfPTable row = new PdfPTable(1);
//
//        for(LayoutField field : fields){
//
//            Object value = data.get(field.getField());
//
//            String text = field.getLabel() + " : " +
//                    (value==null ? "" : value.toString());
//
//            float textWidth = font.getWidthPoint(text,fontSize) + 30;
//
//            if(currentWidth + textWidth > pageWidth){
//
//                document.add(row);
//
//                row = new PdfPTable(1);
//
//                currentWidth = 0;
//            }
//
//            PdfPCell cell = new PdfPCell(new Phrase(text));
//
//            cell.setBorder(Rectangle.NO_BORDER);
//
//            row.addCell(cell);
//
//            currentWidth += textWidth;
//        }
//
//        document.add(row);
//    }
//
//    private void renderTitle(Document document, String title) throws DocumentException {
//        if(title == null) return;
//
//        Font font = new Font(Font.FontFamily.HELVETICA,16,Font.BOLD);
//
//        Paragraph p = new Paragraph(new Chunk(title, font));
//
//        p.setAlignment(Element.ALIGN_CENTER);
//        p.setSpacingAfter(15);
//
//        document.add(p.getChunks().get(0));
//    }
//
//    private float calculateColumnWidth(BaseFont font,
//                                       float fontSize,
//                                       String header,
//                                       List<Map<String,Object>> data,
//                                       String field){
//
//        float maxWidth = font.getWidthPoint(header,fontSize);
//
//        int counter = 0;
//
//        for(Map<String,Object> row : data){
//
//            Object val = row.get(field);
//
//            if(val!=null){
//
//                float width = font.getWidthPoint(val.toString(),fontSize);
//
//                if(width > maxWidth)
//                    maxWidth = width;
//            }
//
//            counter++;
//
//            if(counter>100) break;
//        }
//
//        return maxWidth + 20;
//    }
//
//    private int resolveAlign(String align){
//
//        if("RIGHT".equalsIgnoreCase(align))
//            return Element.ALIGN_RIGHT;
//
//        if("CENTER".equalsIgnoreCase(align))
//            return Element.ALIGN_CENTER;
//
//        return Element.ALIGN_LEFT;
//    }
//
//    public Map<String,Object> fetchHeaderData(Map<String,Object> filters){
//
//        Map<String,Object> header = new LinkedHashMap<>();
//
//        header.put("agentName","ABC LOGISTICS");
//        header.put("agentCode","AG101");
//        header.put("zone","EAST");
//
//        return header;
//    }
//
//    public List<Map<String,Object>> fetchReportData(Map<String,Object> filters){
//
//        List<Map<String,Object>> rows = new ArrayList<>();
//
//        Map<String,Object> r1 = new HashMap<>();
//        r1.put("containerNo","CONT123");
//        r1.put("slNo",1);
//        r1.put("serviceType","STORAGE");
//        r1.put("rate",500);
//        r1.put("amount",1000);
//
//        rows.add(r1);
//
//        Map<String,Object> r2 = new HashMap<>();
//        r2.put("containerNo","CONT123");
//        r2.put("slNo",2);
//        r2.put("serviceType","HANDLING");
//        r2.put("rate",200);
//        r2.put("amount",400);
//
//        rows.add(r2);
//
//        Map<String,Object> r3 = new HashMap<>();
//        r3.put("containerNo","CONT456");
//        r3.put("slNo",1);
//        r3.put("serviceType","STORAGE");
//        r3.put("rate",600);
//        r3.put("amount",1200);
//
//        rows.add(r3);
//
//        Map<String,Object> r4 = new HashMap<>();
//        r4.put("containerNo","CONT456");
//        r4.put("slNo",2);
//        r4.put("serviceType","LOADING");
//        r4.put("rate",300);
//        r4.put("amount",600);
//
//        rows.add(r4);
//
//        return rows;
//    }
//
//    public ReportLayout load(String path) throws Exception {
//
//        ClassLoader classLoader = getClass().getClassLoader();
//
//        InputStream inputStream = classLoader.getResourceAsStream(path);
//
//        if (inputStream == null) {
//            throw new RuntimeException("Report layout not found: " + path);
//        }
//
//        return objectMapper.readValue(inputStream, ReportLayout.class);
//    }
//}
