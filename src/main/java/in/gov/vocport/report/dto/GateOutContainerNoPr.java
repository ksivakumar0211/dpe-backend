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
        name = "gateOutContainerNoPr",
        classes = @ConstructorResult(
                targetClass = GateOutContainerNoPr.class,
                columns = {
                        @ColumnResult(name = "container_no", type = String.class),
                        @ColumnResult(name = "dpe_out_time", type = String.class),
                }
        )
)
public class GateOutContainerNoPr {
    @Id
    private String containerNo;
    private String dpeOutTime;
}
