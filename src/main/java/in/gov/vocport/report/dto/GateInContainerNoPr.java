package in.gov.vocport.report.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SqlResultSetMapping(
        name = "gateInContainerNoPr",
        classes = @ConstructorResult(
                targetClass = GateInContainerNoPr.class,
                columns = {
                        @ColumnResult(name = "container_no", type = String.class),
                        @ColumnResult(name = "dpe_in_time", type = String.class),
                }
        )
)
public class GateInContainerNoPr {
    @Id
    private String containerNo;
    private String dpeInTime;
}
