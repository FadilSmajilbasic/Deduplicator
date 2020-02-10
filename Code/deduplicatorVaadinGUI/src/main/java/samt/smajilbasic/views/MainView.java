package samt.smajilbasic.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import samt.smajilbasic.communication.Client;
import samt.smajilbasic.listener.LoginListener;

import java.util.Iterator;
import java.util.stream.Stream;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;

/**
 * MainView
 */
@Route
public class MainView extends VerticalLayout implements LoginListener  {
        MenuBar menuBar;
        MenuItem path;
        private Client client;
        public MainView() {

                menuBar = new MenuBar();

                MenuItem login = menuBar.addItem("Login", e -> changeView(LoginView.class));
                path =  menuBar.addItem("Path", e -> changeView(PathView.class));
                path.setVisible(false);
                menuBar.setSizeFull();
                setAlignItems(Alignment.CENTER);
                add(menuBar);

        }

        

        private void changeView(Class view) {
                
                Stream<Component> components = getChildren();
                Iterator<Component> comp = components.iterator();
                
                while (comp.hasNext()){
                        Component component = comp.next();
                        if(!component.equals(menuBar)){
                                remove(component);
                        }   
                }
                Component resultView = null;

                if(view.equals(LoginView.class)){
                        resultView = new LoginView(this);
                }if(view.equals(PathView.class)){
                        resultView = new PathView(client);
                }
                if(resultView != null)
                        add(resultView);
                else
                        Notification.show("invalid result");

        }

        @Override
        public void userConnected(Client client) {
                path.setVisible(true);
                this.client = client;
        }
}