package in.gov.vocport.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class ContainerInPortDto {
	private String containerNo;
    private String chitNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private String gateInDateTime;
    private String agentCode;
    private String agentName;
    private String boeNo;
    private Integer containerSize;
    private String loadingStatus;
    private String foreignCoastalFlag;
}
