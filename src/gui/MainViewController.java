package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML
    private MenuItem menuItemSeller;
    @FXML
    private MenuItem menuItemDepartment;
    @FXML
    private MenuItem menuItemAbout;

    @FXML
    private void onMenuItemSellerAction() {
        System.out.println("MenuItemSellerAction");
    }
    @FXML
    private void onMenuItemDepartmentAction() {
        System.out.println("MenuItemDepartmentAction");
    }
    @FXML
    private void onMenuItemAboutAction() {
        System.out.println("MenuItemAboutAction");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
