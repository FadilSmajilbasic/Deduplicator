package samt.smajilbasic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.apache.tomcat.jni.Global;
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
import samt.smajilbasic.views.DuplicatesButtonLayout;
import samt.smajilbasic.views.Utils;

import java.util.ArrayList;
import java.util.List;

public class DuplicateGridService {

    /**
     * The parser used to read the JSON response from the server when updating the
     * status.
     */
    private JSONParser parser = new JSONParser();

    private JSONObject[] duplicatesArray;
    private String reportId;
    private Client client;
    ;
    /**
     * The encoder used to map the return values from the server to a GlobalPath
     * object.
     */
    private Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();


    public DuplicateGridService(String selectValue, Client client) {
        this.client = client;
        setReportId(selectValue);
        System.out.println("path: " + reportId);
        long start = System.currentTimeMillis();
        ResponseEntity<String> response = client.get("report/duplicate/" + reportId + "/");
        System.out.println("got data in " + (System.currentTimeMillis() - start) + "ms");
        if (response != null) {
            try {
                start = System.currentTimeMillis();
                duplicatesArray = Utils.getArray((JSONArray) parser.parse(response.getBody()));
                System.out.println("parsed data in " + (System.currentTimeMillis() - start) + "ms");
                List<MinimalDuplicate> duplicates = new ArrayList<MinimalDuplicate>();

            } catch (ParseException pe) {
                Notification.show("Unable to read status", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END)
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
            duplicate.setSize(duplicatesArray[i].get("size").toString());
            duplicate.setCount(duplicatesArray[i].get("count").toString());

            List<GlobalPath> paths = getPathsFromDuplicate(reportId, duplicate.getHash());
            if (paths != null) {
                DuplicateGrid grid = new DuplicateGrid(paths,duplicate);
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
                        System.out.println(e.getMessage());
                    }
                }
                return paths;

            } catch (ParseException pe) {
                Notification
                    .show("Unable to read paths from duplicate", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return null;
            }
        } else {
            Notification
                .show("Unable to retrieve paths from duplicate", Resources.NOTIFICATION_LENGTH, Notification.Position.TOP_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return null;
        }
    }

    public void setReportId(String selectValue) {
        this.reportId = selectValue.split(":")[0];
    }

    public int getTotalDuplicatesCount() {
        return duplicatesArray.length;
    }

}
