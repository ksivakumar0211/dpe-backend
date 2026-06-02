package in.gov.vocport.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DPE_DOC_UPLOAD_CONFIG")
public class DpeDocUploadConfig {
    @Id
    @Column(name = "SERVERIP", length = 100)
    private String serverIp;

    @Column(name = "SHARE_INFO", length = 100)
    private String shareInfo;

    @Column(name = "USERNAME", length = 50)
    private String username;

    @Column(name = "PASSCODE", length = 50)
    private String passcode;

    @Column(name = "PATH", length = 200)
    private String path;
}
