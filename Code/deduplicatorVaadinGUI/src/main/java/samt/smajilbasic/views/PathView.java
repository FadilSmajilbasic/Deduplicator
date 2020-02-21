package samt.smajilbasic.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.FocusNotifier;
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
 * PathView is the view to manage paths.
 */
@Route(value = "path", registerAtStartup = true)
@PageTitle(value = "Deduplicator - Path")
public class PathView extends BaseView {

    /**
     * The grid used to rappresent the paths that are in the database.
     */
    private Grid<GlobalPath> pathGrid = null;
    /**
     * The parser used to read the JSON response from the server when updating the status.
     */
    private JSONParser parser = new JSONParser();
    /**
     * The encoder used to map the return values from the server to a GlobalPath object.
     */
    private Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();
    /**
     * The HTTP/HTTPS client used for the communication.
     */
    private Client client;
    /**
     * Defines if the path to be added is to be scanned or ignored.
     */
    private String type = "scan";
    /**
     * Defines the input text field of the path.
     */
    private TextField pathTextField;
    /**
     * Defines the dialog pop-up that the user uses to modify a path.
     */
    private Dialog dialogModify = new Dialog();

    /**
     * Defines the default root path for the fileBrowser.
     */
    private File root = new File("/");

    /**
     * The default constructor.
     */
    public PathView() {
        super();
        if (UI.getCurrent().getSession().getAttribute(LoginView.CLIENT_STRING) == null) {
            UI.getCurrent().getPage().setLocation("login/");
        } else {
            client = (Client) UI.getCurrent().getSession().getAttribute(LoginView.CLIENT_STRING);
            pathTextField = new TextField("Path");
            pathTextField.addFocusListener(new ComponentEventListener<FocusNotifier.FocusEvent<TextField>>() {

                @Override
                public void onComponentEvent(FocusNotifier.FocusEvent<TextField> event) {
                    openRootSelect();
                }
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
            layout.add(pathTextField, group, addButton);
            layout.setAlignItems(Alignment.BASELINE);
            layout.setWidthFull();
            layout.setFlexGrow(1, pathTextField);
            layout.setWrapMode(WrapMode.WRAP);
            add( layout, pathGrid);
            updatePaths();
        }
    }
     /**
      * Method that manages the process of saving a path.
      */
    private void savePath() {
        ResponseEntity<String> response = client.savePath(pathTextField.getValue(), type);

        if (response != null) {
            JSONObject resp = new JSONObject();
            try {
                resp = (JSONObject) parser.parse(response.getBody());
                if (response.getStatusCode() == HttpStatus.OK) {
                    Notification.show("Path successfully saved", NOTIFICATION_LENGTH, Position.TOP_END);
                } else {

                    System.out.println("[ERROR] saving path: " + resp.get("message").toString());
                    Notification.show(resp.get("message").toString(), NOTIFICATION_LENGTH, Position.TOP_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } catch (ParseException pe) {
                Notification.show("Unable to parse the response", NOTIFICATION_LENGTH, Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification.show("Unable to get response from server", NOTIFICATION_LENGTH, Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

        updatePaths();

    }

    /**
     * Method that opens a dialog of the roots in the case that there are multiple.
     */
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
                        root = selected.get();
                        rootDialog.close();
                        openFileSelect();
                    }

                }

            };

            rootDialog.setCloseOnOutsideClick(false);
            rootDialog.add(new Label("Select root path"));
            rootsGrid.setItems(roots);
            rootsGrid.addColumn(File::getAbsolutePath).setHeader("Root");
            rootsGrid.addSelectionListener(listener);
            rootDialog.add(rootsGrid);
            rootsGrid.setVisible(true);
            rootDialog.open();

        }

    }

    /**
     * Method that opens a dialog pop-up with a file browser.
     */
    private void openFileSelect() {
        FilesystemData rootData = new FilesystemData(root, false);
        FilesystemDataProvider fileSystem = new FilesystemDataProvider(rootData);

        TreeGrid<File> fileBrowser = new TreeGrid<>();
        fileBrowser.setItems(rootData.getChildren(root));
        fileBrowser.setDataProvider(fileSystem);
        fileBrowser.addSelectionListener(event -> {
            Optional<File> selected = event.getFirstSelectedItem();
            pathTextField.setValue(selected.get().getAbsolutePath());

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
        dialog.setCloseOnOutsideClick(false);
        dialog.add(layout);
        dialog.open();
    }

    /**
     * Method that updates the fields of the pathsGrid.
     */
    private void updatePaths() {
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
                            Label pathLabelModfiy = new Label(event.getValue().getPath());
                            Label title = new Label("Select action");
                            Button deleteButton = new Button("Delete", e -> {
                                deletePath(event.getValue());
                                dialog.close();
                            });

                            Button modifyButton = new Button("Modify", e -> {
                                dialogModify = new Dialog();
                                Label titleModify = new Label("Select action");
                                VerticalLayout vLayoutModify = new VerticalLayout();
                                HorizontalLayout hLayoutModify = new HorizontalLayout();
                                HorizontalLayout hLayoutModify2 = new HorizontalLayout();
                                RadioButtonGroup<String> group = new RadioButtonGroup<String>();

                                Button modifyConfirmButton = new Button("Confirm", eventModify -> {
                                    try {
                                        modifyPath(event.getValue(), (group.getValue() == "scan"));
                                        dialogModify.close();
                                        dialog.close();
                                    } catch (RuntimeException re) {
                                        Notification
                                                .show("Invalid Path" + re.getMessage(), NOTIFICATION_LENGTH,
                                                        Notification.Position.TOP_END)
                                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                                    }
                                });
                                Button cancelButtonModify = new Button("Cancel", cancelEvent -> {
                                    dialogModify.close();
                                    dialog.close();
                                });
                                pathLabelModfiy.setMinWidth("30em");
                                group.setItems("scan", "ignore");
                                group.setValue(event.getValue().isignoreFile() ? "ignore" : "scan");
                                group.addValueChangeListener(groupModify -> type = groupModify.getValue());
                                hLayoutModify.add(pathLabelModfiy, group);
                                hLayoutModify.setFlexGrow(1, pathLabelModfiy);
                                hLayoutModify2.add(modifyConfirmButton, cancelButtonModify);
                                vLayoutModify.add(titleModify, hLayoutModify, hLayoutModify2);
                                dialogModify.add(vLayoutModify);
                                dialogModify.setCloseOnOutsideClick(false);
                                dialogModify.open();
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
                Notification.show("Unable to retrieve paths: " + pe.getMessage(), NOTIFICATION_LENGTH, Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } else {
            Notification.show("Unable to retrieve paths", NOTIFICATION_LENGTH, Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }

    }

    /**
     * Method that updates a path's type using the client.
     */
    private void modifyPath(GlobalPath oldPath, boolean newIgnoreValue) {
        client.modifyPath(oldPath, (newIgnoreValue ? "ignore" : "scan"));
        updatePaths();
    }

    /**
     * Method that deletes a path's type using the client.
     */
    private void deletePath(GlobalPath path) {
        client.deletePath(path);
        updatePaths();
    }
}