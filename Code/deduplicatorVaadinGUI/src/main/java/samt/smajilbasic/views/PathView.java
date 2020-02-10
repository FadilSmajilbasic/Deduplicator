package samt.smajilbasic.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
    
    public PathView(Client client) {
        this.client = client;
        Label title = new Label("Path View");
        
        pathGrid = new Grid<>();

        pathGrid.setVisible(false);
        add(title, pathGrid);

        getPaths();
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