package in.gov.vocport.service;

import in.gov.vocport.dto.AllMenuDto;
import in.gov.vocport.dto.ProcedureKeyValueDTO;
import in.gov.vocport.repository.GenericProcedureRepository;
import jakarta.persistence.ParameterMode;
import lombok.Data;
import oracle.jdbc.OracleTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import in.gov.vocport.dto.MenuItemDto;
import java.sql.*;
import java.util.*;

@Service
@Data
public class MenuService {

	private static final Logger logger = LoggerFactory.getLogger(MenuService.class);
	private final JdbcTemplate jdbcTemplate;
	private final GenericProcedureRepository repository;

	public MenuService(JdbcTemplate jdbcTemplate, GenericProcedureRepository repository) {
		this.jdbcTemplate = jdbcTemplate;
        this.repository = repository;
    }

	public List<MenuItemDto> getMenuForUser(String userId) {
		List<MenuItemDto> flat = fetchMenuFromProc(userId);
		return buildTree(flat);
	}
	public List<AllMenuDto> getAllMenus() {
		List<ProcedureKeyValueDTO> parameters = new ArrayList<>();
		parameters.add(new ProcedureKeyValueDTO("p_refcur_all_menu_item", null, void.class, ParameterMode.REF_CURSOR));
		return (List<AllMenuDto>) repository.callStoredProcedure("CT_DPE_PKG.get_all_menu_item_pr", parameters, new ArrayList<AllMenuDto>(), "allItemDto");
	}

	private List<MenuItemDto> fetchMenuFromProc(String userId) {
//		System.out.println("userId------------------------------>>>"+userId);
		final String callSql = "{call CT_DPE_PKG.GET_MENU_ITEM_PR(?, ?)}";

		return jdbcTemplate.execute((CallableStatementCreator) con -> {
			CallableStatement cs = con.prepareCall(callSql);
			cs.setString(1, userId);
			cs.registerOutParameter(2, OracleTypes.CURSOR);
			return cs;
		}, (CallableStatementCallback<List<MenuItemDto>>) cs -> {

			cs.execute();
			ResultSet rs = (ResultSet) cs.getObject(2);
			if (rs == null)
				return Collections.emptyList();

			List<MenuItemDto> list = new ArrayList<>();

			while (rs.next()) {
				String moduleId = safe(rs, "MODULE_ID");
				String menuId = safe(rs, "MENU_ID");
				String parentMenuId = safe(rs, "PARENT_MENU_ID");
				String menuIdTree = safe(rs, "MENU_ID_TREE");
				String menuNameTree = safe(rs, "MENU_NAME_TREE");
				String menuLinkName = safe(rs, "MENU_LINK_NAME");
				String levelStr = safe(rs, "LEVEL");
				String rootId = safe(rs, "ROOT_ID");
				String leafStr = safe(rs, "LEAF");
				String path = safe(rs, "PATH");
				String moduleName = safe(rs, "MODULE_NAME");
				Integer level = parseIntOrNull(levelStr);
				Integer leaf = parseIntOrNull(leafStr);

				MenuItemDto item = new MenuItemDto();
				item.setModuleId(moduleId);
				item.setMenuId(menuId);
				item.setParentMenuId(parentMenuId);
				item.setMenuIdTree(menuIdTree);
				item.setMenuNameTree(menuNameTree);
				item.setMenuLinkName(menuLinkName);
				item.setLevel(level);
				item.setRootId(rootId);
				item.setLeaf(leaf);
				item.setPathLeaf(path);
				item.setModuleName(moduleName);
				list.add(item);
			}
			return list;
		});
	}

	private List<MenuItemDto> buildTree(List<MenuItemDto> flat) {

		Map<String, MenuItemDto> map = new LinkedHashMap<>();
		for (MenuItemDto it : flat) {
			map.put(it.getMenuId(), it);
		}

		List<MenuItemDto> roots = new ArrayList<>();

		for (MenuItemDto it : flat) {
			String parentId = it.getParentMenuId();
			if (parentId == null || parentId.isEmpty() || !map.containsKey(parentId)) {
				roots.add(it);
			} else {
				map.get(parentId).getChildren().add(it);
			}
		}

		return roots;
	}

	private String safe(ResultSet rs, String col) {
		try {
			String v = rs.getString(col);
			return v == null ? null : v.trim();
		} catch (Exception e) {
			logger.warn("Column not found: {}", col);
			return null;
		}
	}

	private Integer parseIntOrNull(String s) {
		try {
			return (s == null) ? null : Integer.parseInt(s.trim());
		} catch (Exception ex) {
			return null;
		}
	}
}