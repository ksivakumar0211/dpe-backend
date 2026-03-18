package in.gov.vocport.report;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JasperReportsContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ReportBean {
    @Bean
    public JasperReportsContext jasperReportsContext() {
        JasperReportsContext jrCtx = DefaultJasperReportsContext.getInstance();
//        JRPropertiesUtil jrProps = JRPropertiesUtil.getInstance(jrCtx);

//        jrProps.setProperty("net.sf.jasperreports.default.font.name", "Arial");
//        jrProps.setProperty("net.sf.jasperreports.default.pdf.font.name", "Arial");
//        jrProps.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H");
//        jrProps.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
//
//        System.out.println("✅ Arial font registered");
        return jrCtx;
    }

//    @Bean
//    public RestTemplate restTemplate() {
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        factory.setConnectTimeout(10 * 60 * 1000); // 10 min
//        factory.setReadTimeout(10 * 60 * 1000);    // 10 min
//
//        return new RestTemplate(factory);
//    }
}
