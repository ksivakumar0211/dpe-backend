package in.gov.vocport.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MenuItemDto {
	/**
	 * 
	 */
	private String moduleId;
	private String menuId;
	private String parentMenuId;
	private String menuIdTree;
	private String menuNameTree;
	private String menuLinkName;
	private Integer level;
	private String rootId;
	private String pathLeaf;
	private Integer leaf;
	private String moduleName;
	private List<MenuItemDto> children = new ArrayList<>();

//	public MenuItemDto() {
//	}
//
//	public MenuItemDto(String moduleId, String menuId, String parentMenuId, String menuIdTree, String menuNameTree, String menuLinkName,
//			Integer level, String rootId, String pathLeaf, Integer leaf, String moduleName) {
//		this.moduleId = moduleId;
//		this.menuId = menuId;
//		this.parentMenuId = parentMenuId;
//		this.menuIdTree = menuIdTree;
//		this.menuNameTree = menuNameTree;
//		this.menuLinkName = menuLinkName;
//		this.level = level;
//		this.rootId = rootId;
//		this.pathLeaf = pathLeaf;
//		this.leaf = leaf;
//		this.moduleName = moduleName;
//	}
//
//	// getters & setters
//
//	public String getModuleId() {
//		return moduleId;
//	}
//
//	public void setModuleId(String moduleId) {
//		this.moduleId = moduleId;
//	}
//
//	public String getMenuId() {
//		return menuId;
//	}
//
//	public void setMenuId(String menuId) {
//		this.menuId = menuId;
//	}
//
//	public String getParentMenuId() {
//		return parentMenuId;
//	}
//
//	public void setParentMenuId(String parentMenuId) {
//		this.parentMenuId = parentMenuId;
//	}
//
//	public String getMenuIdTree() {
//		return menuIdTree;
//	}
//
//	public void setMenuIdTree(String menuIdTree) {
//		this.menuIdTree = menuIdTree;
//	}
//
//	public String getMenuNameTree() {
//		return menuNameTree;
//	}
//
//	public void setMenuNameTree(String menuNameTree) {
//		this.menuNameTree = menuNameTree;
//	}
//
//	public String getMenuLinkName() {
//		return menuLinkName;
//	}
//
//	public void setMenuLinkName(String menuLinkName) {
//		this.menuLinkName = menuLinkName;
//	}
//
//	public Integer getLevel() {
//		return level;
//	}
//
//	public void setLevel(Integer level) {
//		this.level = level;
//	}
//
//	public String getRootId() {
//		return rootId;
//	}
//
//	public void setRootId(String rootId) {
//		this.rootId = rootId;
//	}
//
//	public String getPathLeaf() {
//		return pathLeaf;
//	}
//
//	public void setPathLeaf(String pathLeaf) {
//		this.pathLeaf = pathLeaf;
//	}
//
//	public Integer getLeaf() {
//		return leaf;
//	}
//
//	public void setLeaf(Integer leaf) {
//		this.leaf = leaf;
//	}
//
//	public List<MenuItemDto> getChildren() {
//		return children;
//	}
//
//	public String getModuleName() {
//		return moduleName;
//	}
//
//	public void setModuleName(String moduleName) {
//		this.moduleName = moduleName;
//	}
//
//	public void setChildren(List<MenuItemDto> children) {
//		this.children = children;
//	}
}