package in.gov.vocport.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@SqlResultSetMapping(
        name = "LocationInfoDto",
        classes = @ConstructorResult(
                targetClass = LocationInfoDto.class,
                columns = {
                        @ColumnResult(name = "location_cd",type = String.class),
                        @ColumnResult(name = "location_name", type = String.class)
                }
        )
)
public class LocationInfoDto {
    @Id
    private String locationCd;
    private String locationName;
}
