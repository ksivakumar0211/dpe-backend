package in.gov.vocport;

import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReportsContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class PmsApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(PmsApplication.class, args);
		JasperReportsContext jrCtx = ctx.getBean(JasperReportsContext.class);
		JRPropertiesUtil jrProps = JRPropertiesUtil.getInstance(jrCtx);
		ctx.getBean(PmsApplication.class).compileJasperreports(jrCtx);
		
		/*
		 * BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); String
		 * rawPassword = "Abcd123#"; String encodedPassword =
		 * encoder.encode(rawPassword); System.out.println("Encoded password: " +
		 * encodedPassword);
		 */
		
		//BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	    //System.out.println(encoder.matches("1234", "$2a$10$S9z8VKMK02bHuEDNPQJijeo8kx58sIRNWdjqjIbcvJWx0K7mYO.9C"));
	}

	private void compileJasperreports(JasperReportsContext jrCtx) {
		JasperCompileManager compileManager = JasperCompileManager.getInstance(jrCtx);
		File dir = new File("src/main/resources/xsd");
		File[] jrxmlFiles = dir.listFiles((d, name) -> name.endsWith(".jrxml"));
		File[] jasperFiles = dir.listFiles((d, name) -> name.endsWith(".jasper"));

		Set<String> jasperSet = new HashSet<>();
		prepareJasperFileSet(jasperSet, jasperFiles);
		if (jrxmlFiles != null) {
			for (File jrxmlFile : jrxmlFiles) {
				try {
					System.out.println("Compiling JRXML from: " + jrxmlFile.getAbsolutePath());
					String jasperFileName = jrxmlFile.getAbsolutePath().replace(".jrxml", ".jasper");
					if (!jasperSet.stream().anyMatch(key -> jasperFileName.endsWith(key))) {
						String jasperFile = jrxmlFile.getAbsolutePath().replace(".jrxml", ".jasper");
						compileManager.compileReportToFile(jrxmlFile.getAbsolutePath(), jasperFile);
						System.out.println("Compiled: " + jasperFile + " In " + jrxmlFile.getAbsolutePath());
					}
				} catch (Exception e) {
					System.err.println("Error compiling " + jrxmlFile.getName() + ": " + e.getMessage());
				}
			}
		} else {
			System.out.println("No JRXML files found in the directory.");
		}
	}

	private void prepareJasperFileSet(Set<String> jasperSet, File[] jasperFiles) {
		Arrays.stream(jasperFiles).forEach(file ->
				jasperSet.add(file.getName())
		);
	}
}
