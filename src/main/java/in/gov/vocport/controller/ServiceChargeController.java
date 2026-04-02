package in.gov.vocport.controller;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import in.gov.vocport.dto.ServiceDto;
import jakarta.annotation.PostConstruct;
import oracle.jdbc.OracleTypes;

@RestController
public class ServiceChargeController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private SimpleJdbcCall serviceCall;

    @GetMapping("/generate-cfs")
    public String generateCfsNo() {

        return jdbcTemplate.queryForObject(
            """
            SELECT Gn_Tran_Primary_Key_Gen_Fn(
                'DPE',
                'CT_TD_DIRECT_SERVICE_CHG',
                'CFS_NO',
                6,
                SYSDATE
            )
            FROM DUAL
            """,
            String.class
        );
    }
    
    @PostConstruct
    public void initServiceCall() {

        serviceCall = new SimpleJdbcCall(jdbcTemplate)
            .withCatalogName("CT_DPE_PKG")
            .withProcedureName("CT_DPE_SERVICE_PR")
            .withoutProcedureColumnMetaDataAccess()   // 🔴 IMPORTANT
            .declareParameters(
                    new SqlParameter("p_service_id", Types.VARCHAR),
                    new SqlParameter("p_cont_size", Types.INTEGER),
                    new SqlParameter("p_loading_status", Types.VARCHAR),
                    new SqlParameter("p_origin", Types.VARCHAR),
                    new SqlOutParameter(
                    "p_refcur_service_id",
                    OracleTypes.CURSOR,
                    (rs, rowNum) -> new ServiceDto(
                        rs.getString("SERVICE_ID"),
                        rs.getString("SERVICE_NAME"),
                        rs.getString("SERVICE_TYPE")
                    )
                )
            );
    }


    @SuppressWarnings("unchecked")
	@GetMapping("/services")
    public List<ServiceDto> loadServices(@RequestParam(required = false) String serviceId, @RequestParam int countSize, @RequestParam String loadingStatus, @RequestParam String origin) {

        Map<String, Object> object = Map.of("p_service_id", serviceId,
                "p_cont_size", countSize,
                "p_loading_status", loadingStatus,
                "p_origin", origin);
        Map<String, Object> result = serviceCall.execute(
            new MapSqlParameterSource(object)
        );

        return (List<ServiceDto>) result.get("p_refcur_service_id");
    }
    
//    @SuppressWarnings("deprecation")
//	@GetMapping("/rate")
//    public Double getRate(
//            @RequestParam String serviceId,
//            @RequestParam String containerSize,
//            @RequestParam String loadingStatus,
//            @RequestParam String foreignCoastalFlag) {
//        return jdbcTemplate.queryForObject(
//            "SELECT CT_DPE_PKG.FN_GET_RATE(?, ?, ?, ?) FROM DUAL",
//            new Object[]{serviceId, containerSize, loadingStatus, foreignCoastalFlag},
//            Double.class
//        );
//    }

    @GetMapping("/rate")
    public Double getRate(
            @RequestParam String serviceId,
            @RequestParam String containerSize,
            @RequestParam String loadingStatus,
            @RequestParam String foreignCoastalFlag,
            @RequestParam(required = false) Integer numberOfDays) {
        return jdbcTemplate.queryForObject(
                "SELECT CT_DPE_PKG.FN_GET_RATE_ALL(?, ?, ?, ?, ?) FROM DUAL",
                new Object[]{serviceId, containerSize, loadingStatus, foreignCoastalFlag, numberOfDays},
                Double.class
        );
    }
}

