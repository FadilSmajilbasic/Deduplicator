package samt.smajilbasic.model;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import samt.smajilbasic.entity.GlobalPath;
import samt.smajilbasic.entity.MinimalDuplicate;
import samt.smajilbasic.views.DuplicatesButtonLayout;

import java.util.List;

public class DuplicateGrid {

    private Grid<GlobalPath> item;
    private MinimalDuplicate duplicate;

    public DuplicateGrid(List<GlobalPath> paths,MinimalDuplicate duplicate,boolean forMainView){
        Grid<GlobalPath> grid = new Grid<GlobalPath>();
        grid.setItems(paths);
        grid.addColumn(GlobalPath::getPath).setHeader("Path").setFlexGrow(6);
        grid.addColumn(new ComponentRenderer<Label,GlobalPath>((dateLabel ->{
            return new Label(dateLabel.getDateFormatted());
        }))).setHeader(new Label("Date modified")).setFlexGrow(1);
        if(!forMainView)
            grid.addColumn(new ComponentRenderer<FormLayout,GlobalPath>(DuplicatesButtonLayout::new)).setHeader("Manage").setFlexGrow(2);
        grid.setHeightByRows(true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setSizeFull();
        grid.setClassName("inside-grid");
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
