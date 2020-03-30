package samt.smajilbasic.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import org.vaadin.filesystemdataprovider.FilesystemData;
import org.vaadin.filesystemdataprovider.FilesystemDataProvider;
import samt.smajilbasic.model.ActionType;
import samt.smajilbasic.model.Resources;
import samt.smajilbasic.entity.Action;
import samt.smajilbasic.entity.GlobalPath;

import java.io.File;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DuplicatesButtonLayout extends FormLayout {

    public DuplicatesButtonLayout(GlobalPath item) {
        Button deleteButton = new Button("Delete");
        Button ignoreButton = new Button("Ignore");
        Button moveButton = new Button("Move");

        deleteButton.setClassName("inside-grid-button");
        ignoreButton.setClassName("inside-grid-button");
        moveButton.setClassName("inside-grid-button");

        Action action = new Action();
        deleteButton.addClickListener(event -> {
            Notification.show("Delete: " + item.getPath());
            ignoreButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            moveButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            if(event.getSource().getThemeName()==null){
                deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                item.getAction().setType(ActionType.DELETE);
            } else {
                deleteButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                item.getAction().setType(null);
            }

        });
        moveButton.addClickListener(event -> {
            Notification.show("Move: " + item.getPath());
            deleteButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            ignoreButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            if(event.getSource().getThemeName()==null) {
                moveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                openFileSelect(item);
                item.getAction().setType(ActionType.MOVE);
            } else {
                moveButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                item.getAction().setType(null);
            }

        });
        ignoreButton.addClickListener(event -> {
            Notification.show("Ignore: " + item.getPath());
            deleteButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            moveButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);

            if(event.getSource().getThemeName()==null){
                ignoreButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                item.getAction().setType(ActionType.IGNORE);
            } else {
                ignoreButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
                item.getAction().setType(null);
            }
        });
        item.setAction(action);
        setResponsiveSteps( new ResponsiveStep("0", 1),new ResponsiveStep(Resources.SIZE_MOBILE_S, 3));

        add(deleteButton, ignoreButton, moveButton);
    }


    /**
     * Method that opens a dialog pop-up with a file browser.
     */
    private void openFileSelect(GlobalPath item) {
        FilesystemData rootData = new FilesystemData(File.listRoots()[0], false);
        FilesystemDataProvider fileSystem = new FilesystemDataProvider(rootData);
        Dialog dialog = new Dialog();
        TreeGrid<File> fileBrowser = new TreeGrid<>();
        fileBrowser.setItems(rootData.getChildren(File.listRoots()[0]));
        fileBrowser.setDataProvider(fileSystem);
        fileBrowser.addSelectionListener(event -> {
            Optional<File> selected = event.getFirstSelectedItem();
            if (selected.get().isDirectory() && selected.get().canWrite()) {
                Action action = item.getAction();
                action.setNewPath(selected.get().getAbsolutePath());
                action.setType(ActionType.MOVE);
                dialog.close();
            } else {
                Notification.show("Path selected is not a directory or it is not writeable",
                    new Resources().getNotificationLength(), Notification.Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                Logger.getGlobal().log(Level.SEVERE, "Move dialog - path selected is not a directory or it is not writeable");
            }
        });

        fileBrowser.addHierarchyColumn(File::getAbsolutePath).setHeader("Path");

        VerticalLayout layout = new VerticalLayout();
        layout.add(new Label("Select a folder"), fileBrowser);
        layout.setMinWidth("50em");
//        layout.setAlignItems(Alignment.CENTER);
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.add(layout);
        dialog.open();

    }
}
