package gui;

import app.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.service.DepartmentService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DepartmentListViewController implements Initializable, DataChangeListener {

    private DepartmentService service;

    @FXML
    private TableView<Department> tableViewDepartment;
    @FXML
    private TableColumn<Department, Integer> tableColumnId;
    @FXML
    private TableColumn<Department, String> tableColumnName;
    @FXML
    private Button buttonNew;

    private ObservableList<Department> observableList;

    @FXML
    public void onButtonNewAction(ActionEvent event) {
        Stage parentStage = Util.currentStage(event);
        Department department = new Department();
        createDialogForm(department, parentStage,"DepartmentFormView.fxml");
    }

    public void setService(DepartmentService service) {
        this.service = service;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Null service.");
        }
        List<Department> departmentList = service.findAll();
        observableList = FXCollections.observableArrayList(departmentList);
        tableViewDepartment.setItems(observableList);
    }

    private void createDialogForm(Department department, Stage parentStage, String absoluteName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            DepartmentFormViewController controller = loader.getController();
            controller.setDepartment(department);
            controller.setDepartmentService(service);
            controller.subscribeDataChangeListener(this);
            controller.setFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter department data:");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();
        } catch (IOException e) {
            Alerts.showAlert("IOException", "Error loading view", e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public void onDataChange() {
        updateTableView();
    }
}
