package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Util {
    public static Stage currentStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }
    public static Integer parseToInt(String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
