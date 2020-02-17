package samt.smajilbasic.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.WrapMode;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.navigator.View;
import com.vaadin.ui.Window.ResizeEvent;
import com.vaadin.ui.Window.ResizeListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.vaadin.filesystemdataprovider.FilesystemData;
import org.vaadin.filesystemdataprovider.FilesystemDataProvider;

import samt.smajilbasic.GlobalPath;
import samt.smajilbasic.communication.Client;

/**
 * PathView
 */
@Route(value = "path", registerAtStartup = true)
@PageTitle(value = "PathView")
public class PathView extends VerticalLayout implements View, ResizeListener {

    Grid<GlobalPath> pathGrid = null;
    private JSONParser parser = new JSONParser();
    private Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();
    private Client client;
    private String type = "true";
    private TextField pathTextField;

    private final static int LENGTH = 2000;
    private File root = new File("/");

    public PathView() {
        if (UI.getCurrent().getSession().getAttribute(LoginView.CLIENT_STRING) == null) {
            UI.getCurrent().getPage().setLocation("login/");
        } else {
            client = (Client) UI.getCurrent().getSession().getAttribute(LoginView.CLIENT_STRING);
            pathTextField = new TextField();
            Button browseButton = new Button("Browse", new Icon(VaadinIcon.FOLDER_OPEN), e -> {
                openRootSelect();
            });
            Button addButton = new Button("Add", new Icon(VaadinIcon.PLUS), e -> {
                savePath();
            });

            pathTextField.setMinWidth("30em");

            RadioButtonGroup<String> group = new RadioButtonGroup<String>();
            group.setItems("scan", "ignore");
            group.setValue("scan");
            group.addValueChangeListener(event -> type = event.getValue());

            pathGrid = new Grid<>();
            pathGrid.setVisible(false);

            FlexLayout layout = new FlexLayout();
            layout.add(pathTextField, browseButton, group, addButton);
            layout.setAlignItems(Alignment.START);
            layout.setWidthFull();
            layout.setFlexGrow(1, pathTextField);
            layout.setWrapMode(WrapMode.WRAP);
            add(layout, pathGrid);
            updatePaths();
        }
    }

    private void savePath() {
        String resp = client.savePath(pathTextField.getValue(), type);

        if (resp.equals("OK")) {
            Notification.show("Path added with success", LENGTH, Position.BOTTOM_END);
        } else {
            System.out.println("[ERROR] saving path: " + resp);
            Notification.show(resp, LENGTH, Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        updatePaths();

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
        layout.add(new Label("Select file or folder"), fileBrowser, confirmButton);
        layout.setFlexGrow(1, fileBrowser);
        fileBrowser.setWidthFull();
        layout.setMinWidth("50em");
        layout.setAlignItems(Alignment.CENTER);
        dialog.add(layout);
        dialog.open();
    }

    private void updatePaths() {
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

                if (pathGrid.getColumns().size() == 0) {
                    pathGrid.addColumn(GlobalPath::getPath).setHeader("Path").setFlexGrow(1);
                    pathGrid.addColumn(GlobalPath::isignoreFile).setHeader("Ignored").setFlexGrow(0);

                    pathGrid.asSingleSelect().addValueChangeListener(event -> {
                        if (event.getValue() != null) {
                            Dialog dialog = new Dialog();
                            VerticalLayout vLayout = new VerticalLayout();
                            HorizontalLayout hLayout = new HorizontalLayout();

                            Label title = new Label("Select action");
                            Button deleteButton = new Button("Delete", e -> {
                                deletePath(event.getValue());
                            });
                            Button modifyButton = new Button("Modify", e -> {
                                Dialog dialogModify = new Dialog();
                                Label titleModify = new Label("Select action");
                                VerticalLayout vLayoutModify = new VerticalLayout();
                                HorizontalLayout hLayoutModify = new HorizontalLayout();
                                TextField pathTextFieldModify = new TextField("Path");
                                pathTextFieldModify.setMinWidth("30em");
                                RadioButtonGroup<String> group = new RadioButtonGroup<String>();
                                group.setItems("scan", "ignore");
                                group.setValue("scan");
                                group.addValueChangeListener(groupModify -> type = groupModify.getValue());

                                Button modifyConfirmButton = new Button("Confirm", eventModify -> modifyPath(
                                        event.getValue(),
                                        new GlobalPath(pathTextFieldModify.getValue(), (group.getValue() == "scan"))));
                                hLayoutModify.add(pathTextFieldModify, group);
                                hLayoutModify.setFlexGrow(1, pathTextFieldModify);

                                vLayoutModify.add(titleModify, hLayoutModify, modifyConfirmButton);
                                dialogModify.add(vLayoutModify);
                            });
                            Button closeButton = new Button("Close", e -> dialog.close());
                            Label path = new Label();

                            path.setText(event.getValue().getPath());

                            hLayout.add(deleteButton, modifyButton, closeButton);
                            vLayout.add(title, hLayout);
                            dialog.add(vLayout);
                            dialog.open();
                        }

                    });
                }

            } catch (ParseException pe) {
                Notification.show("Unable to retrieve paths: " + pe.getMessage());
            }
        } else {
            Notification.show("Unable to retrieve paths");
        }

    }

    private void modifyPath(GlobalPath oldPath, GlobalPath newPath) {
        client.modifyPath(oldPath, newPath);
        updatePaths();
    }

    private void deletePath(GlobalPath path) {
        client.deletePath(path);
        updatePaths();
    }

    @Override
    public void windowResized(ResizeEvent e) {

    }
}