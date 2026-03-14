package in.gov.vocport.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CtTdDocUploadRequestDto {
    private Long srlNo;

    private String documentType;

    private String documentRemarks;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate docUploadDate;

    private String cancelFlag;

    private String dccDownLink;

    private MultipartFile file;
}
