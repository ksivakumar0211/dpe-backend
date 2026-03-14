package in.gov.vocport.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class CtTdDocUploadId implements Serializable {
    private String vesselNo;
    private Long srlNo;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CtTdDocUploadId that = (CtTdDocUploadId) o;
        return Objects.equals(vesselNo, that.vesselNo) && Objects.equals(srlNo, that.srlNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vesselNo, srlNo);
    }
}
