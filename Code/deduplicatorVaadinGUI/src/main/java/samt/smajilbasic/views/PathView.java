package samt.smajilbasic.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.navigator.View;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.vaadin.filesystemdataprovider.FileSelect;
import org.vaadin.filesystemdataprovider.FileTypeResolver;
import org.vaadin.filesystemdataprovider.FilesystemData;
import org.vaadin.filesystemdataprovider.FilesystemDataProvider;

import samt.smajilbasic.GlobalPath;
import samt.smajilbasic.communication.Client;

/**
 * PathView
 */
@Route
@PageTitle("PathView")
public class PathView extends VerticalLayout implements View {

    Grid<GlobalPath> pathGrid = null;
    private JSONParser parser = new JSONParser();
    private Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();
    private Client client;
    private String type = "true";
    private TextField pathTextField;
    private File root = new File("/");

    public PathView(Client client) {
        this.client = client;
        pathTextField = new TextField();
        pathTextField.setWidth("70%");
        Button button = new Button("Browse", new Icon(VaadinIcon.FOLDER_OPEN), e -> {
            openRootSelect();
        });

        RadioButtonGroup<String> group = new RadioButtonGroup<String>();
        group.setItems("scan", "ignore");
        group.setValue("scan");
        group.addValueChangeListener(event -> type = event.getValue());
        pathGrid = new Grid<>();
        pathGrid.setVisible(false);
        HorizontalLayout layout = new HorizontalLayout(pathTextField, button, group);
        layout.setAlignItems(Alignment.CENTER);
        layout.setWidthFull();
        add(layout, pathGrid);

        getPaths();
    }

    private void openRootSelect() {
        File[] rootsArray = File.listRoots();
        ArrayList<File> roots = new ArrayList<File>();

        for (File file : rootsArray) {
            roots.add(file);
        }
        if (roots.size() == 1) {
            root = roots.get(0);
            openFileSelect();
        } else {
            Grid<File> rootsGrid = new Grid<File>();
            Dialog rootDialog = new Dialog();
            SelectionListener<Grid<File>, File> listener = new SelectionListener<Grid<File>, File>() {

                @Override
                public final void selectionChange(SelectionEvent<Grid<File>, File> event) {
                    Optional<File> selected = event.getFirstSelectedItem();
                    if (selected.isPresent()) {
                        Notification.show("selected");
                        root = selected.get();
                        rootDialog.close();
                        openFileSelect();
                    }

                }

            };
            rootDialog.add(new Label("Select root path"));
            rootsGrid.setItems(roots);
            rootsGrid.addColumn(File::getAbsolutePath).setHeader("Root");
            rootsGrid.addSelectionListener(listener);
            rootDialog.add(rootsGrid);
            rootsGrid.setVisible(true);
            rootDialog.open();

        }

    }

    private void openFileSelect() {
        FilesystemData rootData = new FilesystemData(root, false);
        FilesystemDataProvider fileSystem = new FilesystemDataProvider(rootData);

        TreeGrid<File> fileBrowser = new TreeGrid<>();
        fileBrowser.setItems(rootData.getChildren(root));

        fileBrowser.setDataProvider(fileSystem);

        fileBrowser.addSelectionListener(event -> {
            Optional<File> selected = event.getFirstSelectedItem();
            if (selected.isPresent()) {
                pathTextField.setValue(selected.get().getAbsolutePath());
            }
        });

        fileBrowser.addHierarchyColumn(File::getAbsolutePath).setHeader("Path");

        Dialog dialog = new Dialog();
        Button confirmButton = new Button("Close", button -> {
            dialog.close();
        });

        VerticalLayout layout = new VerticalLayout();
        layout.add(new Label("Select file or folder"));
        layout.add(fileBrowser);
        layout.add(confirmButton);
        layout.setWidthFull();
        layout.setHeight("500px");
        layout.setAlignItems(Alignment.CENTER);
        dialog.add(layout);
        dialog.setWidth("500px");
        dialog.setHeight("500px");
        dialog.open();
    }

    private void getPaths() {
        pathGrid.setVisible(true);
        ResponseEntity<String> response = client.get("path/");

        if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                JSONObject[] array = Utils.getArray((JSONArray) parser.parse(response.getBody()));
                List<GlobalPath> paths = new ArrayList<GlobalPath>();

                for (JSONObject jsonObject : array) {
                    try {
                        paths.add(encoder.getObjectMapper().readValue(jsonObject.toJSONString(), GlobalPath.class));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }

                pathGrid.setItems(paths);
                pathGrid.addColumn(GlobalPath::getPath).setHeader("Path");
                pathGrid.addColumn(GlobalPath::isignoreFile).setHeader("to be ignored");

                pathGrid.asSingleSelect().addValueChangeListener(event -> {
                    String message = String.format("Selection changed from %s to %s",
                            event.getOldValue() != null ? event.getOldValue().getPath() : "",
                            event.getValue().getPath());
                    Notification.show(message);
                });

            } catch (ParseException pe) {
                Notification.show("Unable to retrieve paths: " + pe.getMessage());
            }
        } else {
            Notification.show("Unable to retrieve paths");
        }

    }
}