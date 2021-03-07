package gui;

import db.DbException;
import gui.listeners.DataChangeListener;
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
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.service.SellerService;

import java.net.URL;
import java.util.*;

public class SellerFormViewController implements Initializable {
    private Seller seller;
    private SellerService sellerService;
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

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

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
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

    private void setErrors(Map<String, String> errors) {
        Set<String> fields = errors.keySet();
        if (fields.contains("name")) {
            labelError.setText(errors.get("name"));
        }
    }
}
