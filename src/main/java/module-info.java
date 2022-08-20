module kfupm {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires unirest.java;
    opens kfupm to javafx.fxml;
    exports kfupm;
}
