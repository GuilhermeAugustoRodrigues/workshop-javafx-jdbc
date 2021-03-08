package gui;

import app.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Util;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.service.DepartmentService;
import model.service.SellerService;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerListViewController implements Initializable, DataChangeListener {

    private SellerService sellerService;

    @FXML
    private TableView<Seller> tableViewSeller;
    @FXML
    private TableColumn<Seller, Integer> tableColumnId;
    @FXML
    private TableColumn<Seller, String> tableColumnName;
    @FXML
    private TableColumn<Seller, String> tableColumnEmail;
    @FXML
    private TableColumn<Seller, Date> tableColumnBirthDate;
    @FXML
    private TableColumn<Seller, Double> tableColumnBaseSalary;
    @FXML
    private TableColumn<Seller, Seller> tableColumnEdit;
    @FXML
    private TableColumn<Seller, Seller> tableColumnDelete;
    @FXML
    private Button buttonNew;

    private ObservableList<Seller> observableList;

    @FXML
    public void onButtonNewAction(ActionEvent event) {
        Stage parentStage = Util.currentStage(event);
        Seller seller = new Seller();
        createDialogForm(seller, parentStage,"SellerFormView.fxml");
    }

    public void setService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        Util.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
        Util.formatTableColumnDouble(tableColumnBaseSalary, 2);
        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
    }

    private void initializeEditButtons() {
        tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEdit.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Edit");

            @Override
            protected void updateItem(Seller seller, boolean empty) {
                super.updateItem(seller, empty);
                if (seller == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event ->
                        createDialogForm(seller, Util.currentStage(event), "SellerFormView.fxml" ));
            }
        });
    }

    private void initializeDeleteButtons() {
        tableColumnDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnDelete.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Delete");

            @Override
            protected void updateItem(Seller seller, boolean empty) {
                super.updateItem(seller, empty);
                if (seller == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeEntity(seller));
            }
        });
    }

    private void removeEntity(Seller seller) {
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation",
                "Are you sure? This operation can not be undone.");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (sellerService == null) {
                throw new IllegalStateException("Service is null.");
            }
            try {
                sellerService.delete(seller);
                updateTableView();
            } catch (DbIntegrityException e) {
                Alerts.showAlert("Error deleting seller.", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }

    }

    public void updateTableView() {
        if (sellerService == null) {
            throw new IllegalStateException("Null service.");
        }
        List<Seller> departmentList = sellerService.findAll();
        observableList = FXCollections.observableArrayList(departmentList);
        tableViewSeller.setItems(observableList);
        initializeEditButtons();
        initializeDeleteButtons();
    }

    private void createDialogForm(Seller seller, Stage parentStage, String absoluteName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            SellerFormViewController controller = loader.getController();
            controller.setSeller(seller);
            controller.setServices(sellerService, new DepartmentService());
            controller.loadComboBoxDepartments();
            controller.subscribeDataChangeListener(this);
            controller.setFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter seller data:");
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
