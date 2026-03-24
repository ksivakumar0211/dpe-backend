package in.gov.vocport.dto;

import java.sql.SQLException;

public interface AgentProjection {
    String getPartyCd() throws SQLException;
    String getAgentNm() throws SQLException;
}
