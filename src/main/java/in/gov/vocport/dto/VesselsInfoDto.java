package in.gov.vocport.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@SqlResultSetMapping(
        name = "vesselsInfoDto",
        classes = @ConstructorResult(
                targetClass = VesselsInfoDto.class,
                columns = {
                        @ColumnResult(name = "vessel_no",type = String.class),
                        @ColumnResult(name = "vcn", type = String.class),
                        @ColumnResult(name = "vessel_name", type = String.class),
                        @ColumnResult(name = "berthed_time", type = LocalDateTime.class),
                        @ColumnResult(name = "agent_customer_name", type = String.class),
                        @ColumnResult(name = "agent_customer_id", type = String.class),
                        @ColumnResult(name = "zone_id", type = String.class)

                }
        )
)
public class VesselsInfoDto {
    @Id
    private String vesselNo;
    private String vcn;
    private String vesselName;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime berthedTime;
    private String agentCustomerName;
    private String agentCustomerId;
    private String zoneId;
}
