package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.service.DepartmentService;
import model.service.SellerService;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SellerFormViewController implements Initializable {
    private Seller seller;
    private SellerService sellerService;
    private DepartmentService departmentService;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private Label labelId;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldBaseSalary;
    @FXML
    private DatePicker datePickerBirthdate;
    @FXML
    private ComboBox<Department> comboBoxDepartment;
    @FXML
    private Label labelErrorName;
    @FXML
    private Label labelErrorEmail;
    @FXML
    private Label labelErrorBirthdate;
    @FXML
    private Label labelErrorBaseSalary;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonCancel;

    private ObservableList<Department> observableList;

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public void setServices(SellerService sellerService, DepartmentService departmentService) {
        this.sellerService = sellerService;
        this.departmentService = departmentService;
    }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    @FXML
    private void onButtonSaveAction(ActionEvent event) {
        if (seller == null) {
            throw new IllegalStateException("Entity seller is null.");
        }
        if (sellerService == null) {
            throw new IllegalStateException("Seller service is null.");
        }
        try {
            seller = getFormData();
            sellerService.save(seller);
            Util.currentStage(event).close();
            notifyDataChangeListeners();
        } catch (DbException e) {
            Alerts.showAlert("Error savind seller", null, e.getMessage(), Alert.AlertType.ERROR);
        } catch (ValidationException e) {
            setErrors(e.getErrors());
        }
    }

    private void notifyDataChangeListeners() {
        for (DataChangeListener dataChangeListener : dataChangeListeners) {
            dataChangeListener.onDataChange();
        }
    }

    public void setFormData() {
        if (seller == null) {
            throw new IllegalStateException("Entity seller is null.");
        }
        labelId.setText(seller.getId() != null ? seller.getId().toString() : "");
        textFieldName.setText(seller.getName());
        textFieldEmail.setText(seller.getEmail());
        textFieldBaseSalary.setText(String.format("%.2f", seller.getBaseSalary()));
        if (seller.getBirthDate() != null) {
            datePickerBirthdate.setValue(LocalDate.ofInstant(seller.getBirthDate().toInstant(), ZoneId.systemDefault()));
        }
        if (seller.getDepartment() != null) {
            comboBoxDepartment.setValue(seller.getDepartment());
        } else {
            comboBoxDepartment.getSelectionModel().selectFirst();
        }
    }

    private Seller getFormData() {
        Seller seller = new Seller();
        seller.setId(Util.parseToInt(labelId.getText()));
        String name = textFieldName.getText();
        ValidationException exception = new ValidationException("Validation error");
        if (name != null && !name.trim().equals("")) {
            seller.setName(name);
        } else {
            exception.addError("name", "Field can't be empty.");
        }
        if (exception.getErrors().size() > 0) {
            throw exception;
        }
        return seller;
    }

    public void loadComboBoxDepartments() {
        if (departmentService == null) {
            throw new IllegalStateException("Null department service.");
        }
        List<Department> departments = departmentService.findAll();
        observableList = FXCollections.observableList(departments);
        comboBoxDepartment.setItems(observableList);
    }

    @FXML
    private void onButtonCancelAction(ActionEvent event) {
        Util.currentStage(event).close();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes() {
        Constraints.setTextFieldMaxLength(textFieldName, 70);
        Constraints.setTextFieldMaxLength(textFieldEmail, 70);
        Constraints.setTextFieldDouble(textFieldBaseSalary);
        Util.formatDatePicker(datePickerBirthdate, "dd/MM/yyyy");
        initializeComboBoxDepartment();
    }
    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }

    private void setErrors(Map<String, String> errors) {
        Set<String> fields = errors.keySet();
        if (fields.contains("name")) {
            labelErrorName.setText(errors.get("name"));
        }
    }
}
