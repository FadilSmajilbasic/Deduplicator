package samt.smajilbasic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import samt.smajilbasic.communication.Client;
import samt.smajilbasic.entity.GlobalPath;
import samt.smajilbasic.entity.MinimalDuplicate;
import samt.smajilbasic.model.DuplicateGrid;
import samt.smajilbasic.model.Resources;
import samt.smajilbasic.model.Utils;
import samt.smajilbasic.properties.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DuplicateGridService {

    /**
     * The parser used to read the JSON response from the server when updating the
     * status.
     */
    private JSONParser parser = new JSONParser();

    private JSONObject[] duplicatesArray;
    private String reportId;
    private Client client;
    private boolean forMainView;
    private List<GlobalPath> paths = new ArrayList<GlobalPath>();
    private Settings settings = new Settings();
    /**
     * The encoder used to map the return values from the server to a GlobalPath
     * object.
     */
    private Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();


    public DuplicateGridService(String selectValue, Client client, boolean forMainView) {
        this.client = client;
        this.forMainView = forMainView;
        setReportId(selectValue);
        long start = System.currentTimeMillis();
        ResponseEntity<String> response = client.get("report/duplicate/" + reportId + "/");
        Logger.getGlobal().log(Level.INFO, "Got report data in " + (System.currentTimeMillis() - start) + "ms");
        if (response != null) {
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                try {
                    duplicatesArray = Utils.getArray((JSONArray) parser.parse(response.getBody()));
                } catch (ParseException pe) {
                    Logger.getGlobal().log(Level.SEVERE, "Unable to read status");
                    Notification.show("Unable to read status", settings.getNotificationLength(), Notification.Position.TOP_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            } else {
                Logger.getGlobal().log(Level.SEVERE, "Unable to read status");
                Notification.show("Unable to read status", settings.getNotificationLength(), Notification.Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }

    public List<DuplicateGrid> fetchInsideGrids(int offset, int limit) {
        List<DuplicateGrid> insideGrids = new ArrayList<DuplicateGrid>();
        limit = Math.min(offset + limit, duplicatesArray.length);
        for (int i = offset; i < limit; i++) {

            MinimalDuplicate duplicate = new MinimalDuplicate();
            duplicate.setHash(duplicatesArray[i].get("hash").toString());
            duplicate.setSize(Long.parseLong(duplicatesArray[i].get("size").toString()));
            duplicate.setCount(duplicatesArray[i].get("count").toString());

            List<GlobalPath> currPaths = getPathsFromDuplicate(reportId, duplicate.getHash());
            if (currPaths != null) {
                paths.addAll(currPaths);
                DuplicateGrid grid = new DuplicateGrid(currPaths, duplicate, forMainView);
                insideGrids.add(grid);
            } else {
                System.err.println("Unable to fetch paths for duplicate");
            }
        }
        return insideGrids;
    }

    private List<GlobalPath> getPathsFromDuplicate(String report, String hash) {
        ResponseEntity<String> response = client.get("report/duplicate/" + report.split(":")[0] + "/" + hash);
        ObjectMapper objectMapper = encoder.getObjectMapper();
        if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                JSONObject[] array = Utils.getArray((JSONArray) parser.parse(response.getBody()));
                List<GlobalPath> paths = new ArrayList<GlobalPath>();

                for (JSONObject jsonObject : array) {
                    try {
                        paths.add(objectMapper.readValue(jsonObject.toJSONString(), GlobalPath.class));
                    } catch (Exception e) {
                        Logger.getGlobal().log(Level.SEVERE, "Exception while mapping GlobalPath for duplicate representation: " + e.getMessage());
                    }
                }
                return paths;

            } catch (ParseException pe) {
                Notification
                    .show("Unable to read paths from duplicate", settings.getNotificationLength(), Notification.Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return null;
            }
        } else {
            Notification
                .show("Unable to retrieve paths from duplicate", settings.getNotificationLength(), Notification.Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return null;
        }
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public int getTotalDuplicatesCount() {
        return duplicatesArray.length;
    }

    public List<GlobalPath> getPaths() {
        return paths;
    }
}
