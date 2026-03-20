package in.gov.vocport.repository;

import in.gov.vocport.dto.*;
import oracle.jdbc.OracleTypes;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CommonSearchOptionRepository {

	private final JdbcTemplate jdbcTemplate;

	public CommonSearchOptionRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public String generateChitNo() {
		String sql = "SELECT CT_PROCESS_PKG.GN_CHIT_CODE_GEN_VW_FN "
				+ "('GI', 'CT_CHIT_NO_VW', 'CHIT_NO', 6, SYSDATE) FROM DUAL";
		return jdbcTemplate.queryForObject(sql, String.class);
	}
	
	public String generateChitNoGateOut() {
		String sql = "SELECT CT_PROCESS_PKG.GN_CHIT_CODE_GEN_VW_FN "
				+ "('GO', 'CT_CHIT_NO_VW', 'CHIT_NO', 6, SYSDATE) FROM DUAL";
		return jdbcTemplate.queryForObject(sql, String.class);
	}

	public PagedResponse<PortDto> searchPorts(String term, int page, int size) {

		String base = """
				FROM VS_M_PORT c
				WHERE c.del_flag='N'
				""";

		List<Object> params = new java.util.ArrayList<>();

		if (term != null && !term.isBlank()) {
			base += " AND (UPPER(c.PORT_NM) LIKE UPPER(?) OR UPPER(c.MAN_PORT_CD) LIKE UPPER(?))";
			String like = "%" + term.trim() + "%";
			params.add(like);
			params.add(like);
		}

		String countSql = "SELECT COUNT(*) " + base;
		@SuppressWarnings("deprecation")
		long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

		int offset = page * size;
//		String dataSql = """
//				SELECT PORT_NM, MAN_PORT_CD
//				""" + base + """
//				ORDER BY PORT_NM ASC
//				OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
//				""";
//
//		params.add(offset);
//		params.add(size);

		String dataSql = """
        SELECT PORT_NM, MAN_PORT_CD
        FROM (
            SELECT p.PORT_NM,
                   p.MAN_PORT_CD,
                   ROWNUM rnum
            FROM (
                SELECT PORT_NM, MAN_PORT_CD
                """ + base + """
                ORDER BY PORT_NM ASC
            ) p
            WHERE ROWNUM <= ?
        )
        WHERE rnum > ?
        """;

		params.add(offset + size); // upper limit
		params.add(offset);        // lower limit

		@SuppressWarnings("deprecation")
		List<PortDto> content = jdbcTemplate.query(dataSql, params.toArray(),
				(rs, rowNum) -> new PortDto(rs.getString("PORT_NM"), rs.getString("MAN_PORT_CD")));

		int totalPages = (int) Math.ceil((double) total / size);
		return new PagedResponse<>(content, page, totalPages, total);
	}

	public PagedResponse<ShipperDto> searchShippers(String term, int page, int size) {
		String base = """
				FROM PO_M_CONSIGNEE_EXPORT s
				WHERE s.active_flag = 'Y'
				""";

		List<Object> params = new java.util.ArrayList<>();

		if (term != null && !term.isBlank()) {
			base += " AND UPPER(s.CONSIGN_NAME) LIKE UPPER(?)";
			String like = "%" + term.trim() + "%";
			params.add(like);
		}

		// total count
		String countSql = "SELECT COUNT(*) " + base;
		@SuppressWarnings("deprecation")
		long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

		int offset = page * size;

//		String dataSql = """
//				SELECT s.CONSIGN_NAME
//				""" + base + """
//				ORDER BY s.CONSIGN_NAME ASC
//				OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
//				""";
//
//		params.add(offset);
//		params.add(size);

		String dataSql = """
        SELECT CONSIGN_NAME
        FROM (
            SELECT s.CONSIGN_NAME,
                   ROWNUM rnum
            FROM (
                SELECT s.CONSIGN_NAME
                """ + base + """
                ORDER BY s.CONSIGN_NAME ASC
            ) s
            WHERE ROWNUM <= ?
        )
        WHERE rnum > ?
        """;

		params.add(offset + size); // upper bound
		params.add(offset);        // lower bound

		@SuppressWarnings("deprecation")
		List<ShipperDto> content = jdbcTemplate.query(dataSql, params.toArray(),
				(rs, rowNum) -> new ShipperDto(rs.getString("CONSIGN_NAME")));

		int totalPages = (int) Math.ceil((double) total / size);
		return new PagedResponse<>(content, page, totalPages, total);
	}

	public PagedResponse<VesselDto> searchVessels(String term, int page, int size) {

		String base = """
				FROM VS_VSINFO_VW v
				WHERE 1=1
				""";

		List<Object> params = new java.util.ArrayList<>();

		if (term != null && !term.isBlank()) {
			base += " AND (UPPER(v.VESSEL_NAME) LIKE UPPER(?) " + "OR UPPER(v.VESSEL_NO) LIKE UPPER(?) "
					+ "OR UPPER(v.VOYAGE_NUMBER) LIKE UPPER(?))";

			String like = "%" + term.trim() + "%";
			params.add(like);
			params.add(like);
			params.add(like);
		}

		String countSql = "SELECT COUNT(*) " + base;
		@SuppressWarnings("deprecation")
		long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

		int offset = page * size;

//		String dataSql = """
//				SELECT v.VESSEL_NO, v.VESSEL_NAME, v.VOYAGE_NUMBER
//				""" + base + """
//				ORDER BY v.VESSEL_NAME ASC
//				OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
//				""";
		String dataSql = """
        SELECT *
        FROM (
            SELECT v.VESSEL_NO,
                   v.VESSEL_NAME,
                   v.VOYAGE_NUMBER,
                   v.STATUS_FOREIGN_COASTAL,
                   ROWNUM rnum
            FROM (
                SELECT v.VESSEL_NO, v.VESSEL_NAME, v.VOYAGE_NUMBER,v.STATUS_FOREIGN_COASTAL
                """ + base + """
                ORDER BY v.VESSEL_NAME ASC
            ) v
            WHERE ROWNUM <= ?
        )
        WHERE rnum > ?
        """;

		params.add(offset + size); // upper bound
		params.add(offset);

		@SuppressWarnings("deprecation")
		List<VesselDto> content = jdbcTemplate.query(dataSql, params.toArray(),
				(rs, rowNum) -> new VesselDto(rs.getString("VESSEL_NO"), rs.getString("VESSEL_NAME"),
						rs.getString("VOYAGE_NUMBER"), rs.getString("STATUS_FOREIGN_COASTAL")));

		int totalPages = (int) Math.ceil((double) total / size);
		return new PagedResponse<>(content, page, totalPages, total);
	}

	public PagedResponse<LocationDto> searchToLocations(String term, String excludeCode, int page, int size) {

		String base = """
				FROM VS_MH_LOCMASTER loc
				WHERE loc.del_flag = 'N'
				  AND loc.location_cd in ('LOC/78', 'LOC/39', 'LOC/37', 'LOC/30')
				  AND loc.location_cd != ?
				""";

		List<Object> params = new java.util.ArrayList<>();
		params.add(excludeCode);

		// Search filter
		if (term != null && !term.isBlank()) {
			base += " AND (UPPER(loc.location_name) LIKE UPPER(?) " + "OR UPPER(loc.location_cd) LIKE UPPER(?))";

			String like = "%" + term.trim() + "%";
			params.add(like);
			params.add(like);
		}

		// Count
		String countSql = "SELECT COUNT(*) " + base;
		@SuppressWarnings("deprecation")
		long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

		int offset = page * size;

		// Data
//		String dataSql = """
//				SELECT loc.location_cd, loc.location_name
//				""" + base + """
//				ORDER BY loc.location_name ASC
//				OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
//				""";
		String dataSql = """
        SELECT *
        FROM (
            SELECT loc.location_cd,
                   loc.location_name,
                   ROWNUM rnum
            FROM (
                SELECT loc.location_cd, loc.location_name
                """ + base + """
                ORDER BY loc.location_name ASC
            ) loc
            WHERE ROWNUM <= ?
        )
        WHERE rnum > ?
        """;

		params.add(offset + size); // upper bound
		params.add(offset);

		@SuppressWarnings("deprecation")
		List<LocationDto> content = jdbcTemplate.query(dataSql, params.toArray(),
				(rs, rowNum) -> new LocationDto(rs.getString("location_cd"), rs.getString("location_name")));

		int totalPages = (int) Math.ceil((double) total / size);
		return new PagedResponse<>(content, page, totalPages, total);
	}
	
	public PagedResponse<LocationDto> searchGateOutFromlocations(String term,int page, int size) {

		    String base = """
		            FROM VS_MH_LOCMASTER loc
		            WHERE loc.del_flag = 'N'
		              AND loc.location_cd in ('LOC/78', 'LOC/39', 'LOC/37', 'LOC/30')
		            """;

		    List<Object> params = new java.util.ArrayList<>();

		    // Search filter
		    if (term != null && !term.isBlank()) {
		        base += " AND (UPPER(loc.location_name) LIKE UPPER(?) " +
		                "OR UPPER(loc.location_cd) LIKE UPPER(?))";

		        String like = "%" + term.trim() + "%";
		        params.add(like);
		        params.add(like);
		    }

		    // Count
		    String countSql = "SELECT COUNT(*) " + base;
		    @SuppressWarnings("deprecation")
		    long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

		    int offset = page * size;

		    // Data
//		    String dataSql = """
//		            SELECT loc.location_cd, loc.location_name
//		            """ + base + """
//		            ORDER BY loc.location_name ASC
//		            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
//		            """;
//
//		    params.add(offset);
//		    params.add(size);

		String dataSql = """
        SELECT location_cd, location_name
        FROM (
            SELECT loc.location_cd,
                   loc.location_name,
                   ROWNUM rnum
            FROM (
                SELECT loc.location_cd, loc.location_name
                """ + base + """
                ORDER BY loc.location_name ASC
            ) loc
            WHERE ROWNUM <= ?
        )
        WHERE rnum > ?
        """;

		params.add(offset + size); // upper bound
		params.add(offset);        // lower bound

		    @SuppressWarnings("deprecation")
		    List<LocationDto> content = jdbcTemplate.query(
		            dataSql,
		            params.toArray(),
		            (rs, rowNum) -> new LocationDto(
		                    rs.getString("location_cd"),
		                    rs.getString("location_name"))
		    );

		    int totalPages = (int) Math.ceil((double) total / size);
		    return new PagedResponse<>(content, page, totalPages, total);
	}
	
	public PagedResponse<LocationDto> searchGateOutTolocations(String term,int page, int size) {
		String base = """
	            FROM VS_MH_LOCMASTER loc
	            WHERE loc.del_flag = 'N'
	              AND loc.location_cd = 'LOC/11'
	            """;

	    List<Object> params = new java.util.ArrayList<>();

	    // Search filter
	    if (term != null && !term.isBlank()) {
	        base += " AND (UPPER(loc.location_name) LIKE UPPER(?) " +
	                "OR UPPER(loc.location_cd) LIKE UPPER(?))";

	        String like = "%" + term.trim() + "%";
	        params.add(like);
	        params.add(like);
	    }

	    // Count
	    String countSql = "SELECT COUNT(*) " + base;
	    @SuppressWarnings("deprecation")
	    long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

	    int offset = page * size;

	    // Data
//	    String dataSql = """
//	            SELECT loc.location_cd, loc.location_name
//	            """ + base + """
//	            ORDER BY loc.location_name ASC
//	            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
//	            """;
//
//	    params.add(offset);
//	    params.add(size);

		String dataSql = """
        SELECT location_cd, location_name
        FROM (
            SELECT loc.location_cd,
                   loc.location_name,
                   ROWNUM rnum
            FROM (
                SELECT loc.location_cd, loc.location_name
                """ + base + """
                ORDER BY loc.location_name ASC
            ) loc
            WHERE ROWNUM <= ?
        )
        WHERE rnum > ?
        """;

		params.add(offset + size); // upper limit
		params.add(offset);        // lower limit

	    @SuppressWarnings("deprecation")
	    List<LocationDto> content = jdbcTemplate.query(
	            dataSql,
	            params.toArray(),
	            (rs, rowNum) -> new LocationDto(
	                    rs.getString("location_cd"),
	                    rs.getString("location_name"))
	    );

	    int totalPages = (int) Math.ceil((double) total / size);
	    return new PagedResponse<>(content, page, totalPages, total);

	}

	public PagedResponse<AgentDto> searchAgents(String term, int page, int size) {

		String base = """
				FROM PO_MH_AGENT a
				WHERE a.del_flag = 'N'
				""";

		List<Object> params = new java.util.ArrayList<>();

		if (term != null && !term.isBlank()) {
			base += " AND (UPPER(a.agent_nm) LIKE UPPER(?) OR UPPER(a.party_cd) LIKE UPPER(?))";
			String like = "%" + term.trim() + "%";
			params.add(like);
			params.add(like);
		}

		// Count total
		String countSql = "SELECT COUNT(*) " + base;
		@SuppressWarnings("deprecation")
		long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

		int offset = page * size;

//		// Fetch page data
//		String dataSql = """
//				SELECT a.party_cd, a.agent_nm
//				""" + base + """
//				ORDER BY a.agent_nm ASC
//				OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
//				""";
//
//		params.add(offset);
//		params.add(size);

		String dataSql = """
        SELECT party_cd, agent_nm
        FROM (
            SELECT a.party_cd,
                   a.agent_nm,
                   ROWNUM rnum
            FROM (
                SELECT a.party_cd, a.agent_nm
                """ + base + """
                ORDER BY a.agent_nm ASC
            ) a
            WHERE ROWNUM <= ?
        )
        WHERE rnum > ?
        """;

		params.add(offset + size); // upper limit
		params.add(offset);        // lower limit

		@SuppressWarnings("deprecation")
		List<AgentDto> content = jdbcTemplate.query(dataSql, params.toArray(),
				(rs, rowNum) -> new AgentDto(rs.getString("party_cd"), rs.getString("agent_nm")));

		int totalPages = (int) Math.ceil((double) total / size);

		return new PagedResponse<>(content, page, totalPages, total);
	}

	public PagedResponse<CargoDto> searchCargo(String term, int page, int size) {

		String base = """
				FROM VS_M_CARGOMST c
				WHERE c.del_flag = 'N'
				""";

		List<Object> params = new java.util.ArrayList<>();

		if (term != null && !term.isBlank()) {
			base += " AND UPPER(c.cargo_cd_desc) LIKE UPPER(?)";
			String like = "%" + term.trim() + "%";
			params.add(like);
		}

		// count
		String countSql = "SELECT COUNT(*) " + base;
		@SuppressWarnings("deprecation")
		long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

		int offset = page * size;

		// data page
//		String dataSql = """
//				SELECT c.cargo_cd,c.cargo_cd_desc
//				""" + base + """
//				ORDER BY c.cargo_cd_desc ASC
//				OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
//				""";
//
//		params.add(offset);
//		params.add(size);

		String dataSql = """
        SELECT cargo_cd, cargo_cd_desc
        FROM (
            SELECT c.cargo_cd,
                   c.cargo_cd_desc,
                   ROWNUM rnum
            FROM (
                SELECT c.cargo_cd, c.cargo_cd_desc
                """ + base + """
                ORDER BY c.cargo_cd_desc ASC
            ) c
            WHERE ROWNUM <= ?
        )
        WHERE rnum > ?
        """;

		params.add(offset + size); // upper limit
		params.add(offset);        // lower limit

		@SuppressWarnings("deprecation")
		List<CargoDto> content = jdbcTemplate.query(dataSql, params.toArray(),
				(rs, rowNum) -> new CargoDto(rs.getString("cargo_cd"),rs.getString("cargo_cd_desc")));

		int totalPages = (int) Math.ceil((double) total / size);

		return new PagedResponse<>(content, page, totalPages, total);
	}
	
	public PagedResponse<ContainerNoDto> searchGateOutContainerNo(String term, int page, int size) {
		String base = """
				FROM CT_T_CONTAINER_IN_PORT_AREA s
				WHERE s.gate_in_through is not null and s.gate_out_through is null
				""";

		List<Object> params = new java.util.ArrayList<>();

		if (term != null && !term.isBlank()) {
			base += " AND UPPER(s.CONTAINER_NO) LIKE UPPER(?)";
			String like = "%" + term.trim() + "%";
			params.add(like);
		}

		// total count
		String countSql = "SELECT COUNT(*) " + base;
		@SuppressWarnings("deprecation")
		long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

		int offset = page * size;

//		String dataSql = """
//				SELECT s.CONTAINER_NO
//				""" + base + """
//				ORDER BY s.GATE_IN_DATE_TIME DESC
//				OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
//				""";
//		params.add(offset);
//		params.add(size);

		String dataSql = """
        SELECT CONTAINER_NO
        FROM (
            SELECT s.CONTAINER_NO,
                   ROWNUM rnum
            FROM (
                SELECT s.CONTAINER_NO
                """ + base + """
                ORDER BY s.GATE_IN_DATE_TIME DESC
            ) s
            WHERE ROWNUM <= ?
        )
        WHERE rnum > ?
        """;

		params.add(offset + size); // upper limit
		params.add(offset);        // lower limit

		@SuppressWarnings("deprecation")
		List<ContainerNoDto> content = jdbcTemplate.query(dataSql, params.toArray(),
				(rs, rowNum) -> new ContainerNoDto(rs.getString("CONTAINER_NO")));

		int totalPages = (int) Math.ceil((double) total / size);
		return new PagedResponse<>(content, page, totalPages, total);
	}

	public PagedResponse<LinerDto> searchLiners(String term, int page, int size) {

		String base = """
				FROM po_mh_agent a, po_md_agent_bank b
				WHERE a.party_cd = b.party_cd
				  AND a.del_flag = 'N'
				  AND b.agent_category = 'LN'
				""";

		List<Object> params = new java.util.ArrayList<>();

		if (term != null && !term.isBlank()) {
			base += " AND (UPPER(a.agent_nm) LIKE UPPER(?) OR UPPER(a.party_cd) LIKE UPPER(?))";
			String like = "%" + term.trim() + "%";
			params.add(like);
			params.add(like);
		}

		// count
		String countSql = "SELECT COUNT(*) " + base;
		@SuppressWarnings("deprecation")
		long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

		int offset = page * size;

//		String dataSql = """
//				SELECT a.party_cd, a.agent_nm
//				""" + base + """
//				ORDER BY a.party_cd
//				OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
//				""";
//
//		params.add(offset);
//		params.add(size);

		String dataSql = """
        SELECT party_cd, agent_nm
        FROM (
            SELECT a.party_cd,
                   a.agent_nm,
                   ROWNUM rnum
            FROM (
                SELECT a.party_cd, a.agent_nm
                """ + base + """
                ORDER BY a.party_cd
            ) a
            WHERE ROWNUM <= ?
        )
        WHERE rnum > ?
        """;

		params.add(offset + size); // upper limit
		params.add(offset);        // lower limit

		@SuppressWarnings("deprecation")
		List<LinerDto> content = jdbcTemplate.query(dataSql, params.toArray(),
				(rs, rowNum) -> new LinerDto(rs.getString("party_cd"), rs.getString("agent_nm")));

		int totalPages = (int) Math.ceil((double) total / size);
		return new PagedResponse<>(content, page, totalPages, total);
	}
	
	@SuppressWarnings("deprecation")
	public String findLocationNameByCode(String code) {
        if (code == null || code.isBlank()) return null;
        try {
            return jdbcTemplate.queryForObject(
                "SELECT location_name FROM VS_MH_LOCMASTER WHERE del_flag='N' AND location_cd = ?",
                new Object[]{code},
                String.class
            );
        } catch (Exception ex) {
            return null;
        }
    }

	public String findVesselNameByCode(String code) {
		if (code == null || code.isBlank()) return null;
		try {
			return jdbcTemplate.queryForObject(
					"SELECT VESSEL_NAME FROM CT_TH_DOC_UPLOAD WHERE  VESSEL_NO= ?",
					new Object[]{code},
					String.class
			);
		} catch (Exception ex) {
			return null;
		}
	}

    @SuppressWarnings("deprecation")
	public String findAgentNameByCode(String partyCd) {
        if (partyCd == null || partyCd.isBlank()) return null;
        try {
            return jdbcTemplate.queryForObject(
                "SELECT agent_nm FROM PO_MH_AGENT WHERE del_flag='N' AND party_cd = ?",
                new Object[]{partyCd},
                String.class
            );
        } catch (Exception ex) {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
	public String findPortNameByCode(String manPortCd) {
        if (manPortCd == null || manPortCd.isBlank()) return null;
        try {
            return jdbcTemplate.queryForObject(
                "SELECT PORT_NM FROM VS_M_PORT WHERE del_flag='N' AND MAN_PORT_CD = ?",
                new Object[]{manPortCd},
                String.class
            );
        } catch (Exception ex) {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
	public String findLinerNameByCode(String partyCd) {
        if (partyCd == null || partyCd.isBlank()) return null;
        try {
            return jdbcTemplate.queryForObject(
                "SELECT a.agent_nm FROM po_mh_agent a, po_md_agent_bank b " +
                "WHERE a.party_cd = b.party_cd AND a.del_flag='N' " +
                "AND b.agent_category = 'LN' AND a.party_cd = ?",
                new Object[]{partyCd},
                String.class
            );
        } catch (Exception ex) {
            return null;
        }
    }
    
    @SuppressWarnings("deprecation")
	public String findCargoNameByCode(String code) {
        if (code == null || code.isBlank()) return null;
        try {
            return jdbcTemplate.queryForObject(
                "SELECT CARGO_CD_DESC FROM VS_M_CARGOMST WHERE del_flag='N' AND CARGO_CD = ?",
                new Object[]{code},
                String.class
            );
        } catch (Exception ex) {
            return null;
        }
    }
    
	public PagedResponse<ContainerNoDto> searchContainerNoForServiceAdd(String term, int page, int size) {
		List<ContainerNoDto> content = new ArrayList<>();
		jdbcTemplate.execute((Connection con) -> {
			CallableStatement cs = con.prepareCall("{ call CT_DPE_PKG.CT_DPE_CONTAINER_NO_PR(?, ?) }");
			cs.setString(1, term);
			cs.registerOutParameter(2, OracleTypes.CURSOR);
			return cs;
		}, (CallableStatement cs) -> {
			cs.execute();
			ResultSet rs = (ResultSet) cs.getObject(2);
			while (rs.next()) {
				content.add(new ContainerNoDto(rs.getString("CONTAINER_NO")));
			}
			rs.close();
			return null;
		});
		int total = content.size();
		int totalPages = (int) Math.ceil((double) total / size);
		return new PagedResponse<>(content, page, totalPages, total);
	}
    
	public ContainerInPortDto findContainerInPortDetails(String containerNo) {
		return jdbcTemplate.execute((Connection con) -> {
			CallableStatement cs = con.prepareCall("{ call CT_DPE_PKG.CT_DPE_CONTAINER_NO_PR(?, ?) }");
			cs.setString(1, containerNo);
			cs.registerOutParameter(2, OracleTypes.CURSOR);
			return cs;
		}, (CallableStatement cs) -> {
			cs.execute();
			ResultSet rs = (ResultSet) cs.getObject(2);
			if (!rs.next()) {
				rs.close();
				return null;
			}
			ContainerInPortDto dto = new ContainerInPortDto();
			dto.setContainerNo(rs.getString("CONTAINER_NO"));
			dto.setChitNo(rs.getString("ADMISSION_CHIT_NO"));
			dto.setGateInDateTime(rs.getTimestamp("ADMISSION_TIME") != null
					? new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(rs.getTimestamp("ADMISSION_TIME"))
					: null);
			dto.setAgentCode(rs.getString("PARTY_CD"));
			dto.setAgentName(rs.getString("CH_AGENT_NAME"));
			dto.setBoeNo(rs.getString("SHIPPING_BILL_NO"));
			dto.setContainerSize(rs.getInt("CONTAINER_SIZE"));
			dto.setLoadingStatus(rs.getString("LOADING_STATUS"));
			dto.setForeignCoastalFlag(rs.getString("FOREIGN_COASTAL_FLAG"));
			rs.close();
			return dto;
		});
	}


	public PagedResponse<VesselsInfoDto> findVessels(String vesselsNo, int page, int size) {
		String base = """
        FROM DPE_VESSEL_NO_NAME_VW vn
        WHERE 1=1
        """;

		List<Object> params = new ArrayList<>();

		// Dynamic filter
		if (vesselsNo != null && !vesselsNo.isBlank()) {
			base += " AND UPPER(TRIM(vn.vessel_no)) LIKE UPPER(?)";
			params.add("%" + vesselsNo.trim() + "%");
		}

		// ✅ Total count query
		String countSql = "SELECT COUNT(*) " + base;

		long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

		// Pagination calculation
		int offset = page * size;

		// ✅ Data query (Oracle 11g compatible pagination)
		String dataSql = """
    	SELECT vesselNo,
           vcn,
           vesselName,
           berthedTime,
           agentCustomerName,
           agentCustomerId,
           zoneId
    FROM (
        SELECT a.*, ROWNUM rnum
        FROM (
            SELECT vn.vessel_no AS vesselNo,
                   vn.calinv_vcn AS vcn,
                   vn.vessel_name AS vesselName,
                   vn.berthed_time AS berthedTime,
                   vn.agent_customer_name AS agentCustomerName,
                   vn.agent_customer_id AS agentCustomerId,
                   vn.zone_id AS zoneId
            """ + base + """
            ORDER BY SUBSTR(vn.vessel_no,4) DESC
        ) a
        WHERE ROWNUM <= ?
    )
    WHERE rnum > ?
    """;

		// Add pagination params
		params.add(offset + size); // upper bound
		params.add(offset);        // lower bound

		// ✅ Execute query
		List<VesselsInfoDto> content = jdbcTemplate.query(
				dataSql,
				params.toArray(),
				(rs, rowNum) -> {
					VesselsInfoDto dto = new VesselsInfoDto();
					dto.setVesselNo(rs.getString("vesselNo"));
					dto.setVcn(rs.getString("vcn"));
					dto.setVesselName(rs.getString("vesselName"));
					dto.setBerthedTime(rs.getTimestamp("berthedTime"));
					dto.setAgentCustomerName(rs.getString("agentCustomerName"));
					dto.setAgentCustomerId(rs.getString("agentCustomerId"));
					dto.setZoneId(rs.getString("zoneId"));
					return dto;
				}
		);

		// Total pages
		int totalPages = (int) Math.ceil((double) total / size);

		// Final response
		return new PagedResponse<>(content, page, totalPages, total);
	}
}