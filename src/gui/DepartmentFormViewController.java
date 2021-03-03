package gui;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.service.DepartmentService;

import java.net.URL;
import java.util.ResourceBundle;

public class DepartmentFormViewController implements Initializable {
    private Department department;
    private DepartmentService departmentService;

    @FXML
    private Label labelId;
    @FXML
    private TextField textFieldName;
    @FXML
    private Label labelError;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonCancel;

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @FXML
    private void onButtonSaveAction(ActionEvent event) {
        if (department == null) {
            throw new IllegalStateException("Entity department is null.");
        }
        if (departmentService == null) {
            throw new IllegalStateException("Department service is null.");
        }
        try {
            department = getFormData();
            departmentService.save(department);
            Util.currentStage(event).close();
        } catch (DbException e) {
            Alerts.showAlert("Error savind department", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Department getFormData() {
        Department department = new Department();
        department.setId(Util.parseToInt(labelId.getText()));
        department.setName(textFieldName.getText());
        return department;
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
        Constraints.setTextFieldMaxLength(textFieldName, 30);
    }

    public void setFormData() {
        if (department == null) {
            throw new IllegalStateException("Entity department is null.");
        }
        labelId.setText(department.getId() != null ? department.getId().toString() : "");
        textFieldName.setText(department.getName());
    }
}
