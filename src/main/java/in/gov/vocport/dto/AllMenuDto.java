package in.gov.vocport.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@SqlResultSetMapping(
        name = "allItemDto",
        classes = @ConstructorResult(
                targetClass = AllMenuDto.class,
                columns = {
                        @ColumnResult(name = "MODULE_ID", type = String.class),
                        @ColumnResult(name = "MENU_ID", type = String.class),
                        @ColumnResult(name = "PARENT_MENU_ID", type = String.class),
                        @ColumnResult(name = "MENU_ID_TREE", type = String.class),
                        @ColumnResult(name = "MENU_NAME_TREE", type = String.class),
                        @ColumnResult(name = "MENU_LINK_NAME", type = String.class),
                        @ColumnResult(name = "LEVEL", type = Integer.class),
                        @ColumnResult(name = "ROOT_ID", type = String.class),
                        @ColumnResult(name = "LEAF", type = Integer.class),
                        @ColumnResult(name = "PATH", type = String.class),
                        @ColumnResult(name = "MODULE_NAME", type = String.class),

                }
        )
)
public class AllMenuDto {
    private String moduleId;
    @Id
    private String menuId;
    private String parentMenuId;
    private String menuIdTree;
    private String menuNameTree;
    private String menuLinkName;
    private Integer level;
    private String rootId;
    private Integer leaf;
    private String path;
    private String moduleName;
}
