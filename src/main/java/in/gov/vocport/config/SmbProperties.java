package in.gov.vocport.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "smb")
@Getter
@Setter
public class SmbProperties {
    private String server;
    private String share;
    private String username;
    private String password;
}
