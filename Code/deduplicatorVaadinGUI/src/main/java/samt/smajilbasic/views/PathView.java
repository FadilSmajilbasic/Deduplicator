package samt.smajilbasic.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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

    public PathView(Client client) {
        this.client = client;
        Label title = new Label("Path View");

        pathTextField = new TextField();
        Button button = new Button("Browse",new Icon(VaadinIcon.FOLDER_OPEN),e->{openFileBrowser();});

        RadioButtonGroup<String> group = new RadioButtonGroup<>();
        group.setItems("scan", "ignore");
        group.addValueChangeListener(event -> type = event.getValue());

        pathGrid = new Grid<>();
        pathGrid.setVisible(false);

        add(title,new HorizontalLayout(pathTextField, button, group), pathGrid);

        getPaths();
    }

    private void openFileBrowser() {
        File[] roots = File.listRoots();
        String root = "/";
        for (File file : roots) {
            Notification.show(file.getAbsolutePath());
        }
        if(roots.length ==1){
            root = roots[0].getAbsolutePath();
        }
        

        File rootFile = new File(root);
        FileSelect fileSelect = new FileSelect(rootFile);
        fileSelect.addValueChangeListener(event -> {
            File file = fileSelect.getValue();
            pathTextField.setValue(file.getAbsolutePath());
        });

        fileSelect.setWidth("50%");
        fileSelect.setHeight("50%");
        // setSizeFull();
        fileSelect.setLabel("Select file");

        // Window subWindow = new Window("Modal View");

        // VerticalLayout subContent = new VerticalLayout();
        // subContent.setMargin(true);
        // subWindow.setContent(subContent);
    
        // // Put some components in it
        // subContent.addComponent(new Label("Label"));
        // subContent.addComponent(new Button("Button"));

        // subWindow.addComponent(fileSelect);

        // subWindow.center();
        Dialog dialog = new Dialog();
        dialog.add(new Label("Asdd"));
        dialog.setWidth("40%");
        dialog.setHeight("20%");
        
        // add(dialog);
        // add(components);
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
                    String message = String.format("Selection changed from %s to %s", event.getOldValue().getPath(),
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