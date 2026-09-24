package in.gov.vocport.dto;

import jakarta.persistence.ParameterMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProcedureKeyValueDTO {
    private String parameterKey;
    private Object parameterValue;
    private Class parameterValueType;
    private ParameterMode parameterMode;

    @SuppressWarnings("unchecked")
	public <T> T getCastedValue() {
        return (T) parameterValueType.cast(parameterValue);
    }
}