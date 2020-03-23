package samt.smajilbasic.model;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import samt.smajilbasic.entity.GlobalPath;
import samt.smajilbasic.entity.MinimalDuplicate;
import samt.smajilbasic.views.DuplicatesButtonLayout;

import java.util.List;

public class DuplicateGrid {

    private Grid<GlobalPath> item;
    private MinimalDuplicate duplicate;

    public DuplicateGrid(List<GlobalPath> paths,MinimalDuplicate duplicate){
        Grid<GlobalPath> grid = new Grid<GlobalPath>();
        grid.setItems(paths);

        grid.addColumn(GlobalPath::getPath).setHeader("Path").setFlexGrow(2);
        grid.addColumn(GlobalPath::getDateFormatted).setHeader("Date modified")
            .setFlexGrow(1);

        grid.addColumn(new ComponentRenderer<>(item -> new DuplicatesButtonLayout(item))).setHeader("Manage").setFlexGrow(1);
        grid.setHeightByRows(true);
        item = grid;
        this.duplicate = duplicate;
    }

    public Grid<GlobalPath> getItem() {
        return item;
    }

    public MinimalDuplicate getMinimalDuplicate() {
        return duplicate;
    }
}
