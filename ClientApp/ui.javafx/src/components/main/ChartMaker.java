package components.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.StackPane;

import javafx.scene.chart.CategoryAxis;

import java.util.List;

public class ChartMaker {

    private final AppController appcontroller;

    public ChartMaker(AppController appcontroller) {
        this.appcontroller = appcontroller;
    }

    public void createChartDialogPopup(int columnCount) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Gr-Settings");
        popupStage.setResizable(false);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        ObservableList<String> columnOptions = FXCollections.observableArrayList();
        for (int i = 1; i <= columnCount; i++) {
            columnOptions.add("Column " + (char) ('A' + i - 1));
        }
        ChoiceBox<String> xColumnChoice = new ChoiceBox<>(columnOptions);
        xColumnChoice.setMinSize(90, 20);
        ChoiceBox<String> yColumnChoice = new ChoiceBox<>(columnOptions);
        yColumnChoice.setMinSize(90, 20);

        //disable duplicate selections
        xColumnChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            yColumnChoice.getItems().remove(newVal);
            if (oldVal != null) yColumnChoice.getItems().add(oldVal);
        });
        yColumnChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            xColumnChoice.getItems().remove(newVal);
            if (oldVal != null) xColumnChoice.getItems().add(oldVal);
        });

        TextField xRangeFromField = createRangeTextField("From");
        TextField xRangeToField = createRangeTextField("To");
        TextField yRangeFromField = createRangeTextField("From");
        TextField yRangeToField = createRangeTextField("To");

        ToggleGroup chartTypeGroup = new ToggleGroup();
        RadioButton barChartOption = new RadioButton("Bar Chart");
        barChartOption.setToggleGroup(chartTypeGroup);
        barChartOption.setSelected(true); // Set Bar Chart as default
        RadioButton lineChartOption = new RadioButton("Line Chart");
        lineChartOption.setToggleGroup(chartTypeGroup);

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {
            if (xColumnChoice.getValue() == null || yColumnChoice.getValue() == null ||
                    xRangeFromField.getText().isEmpty() || xRangeToField.getText().isEmpty() ||
                    yRangeFromField.getText().isEmpty() || yRangeToField.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Please select both X and Y columns and specify valid ranges.").showAndWait();
                return;
            }
            int xFrom = Integer.parseInt(xRangeFromField.getText());
            int xTo = Integer.parseInt(xRangeToField.getText());
            int yFrom = Integer.parseInt(yRangeFromField.getText());
            int yTo = Integer.parseInt(yRangeToField.getText());
            if (xFrom > xTo || yFrom > yTo) {
                new Alert(Alert.AlertType.WARNING, "'From' value cannot be greater than 'To' for both X and Y ranges.").showAndWait();
                return;
            }
            String chartType = barChartOption.isSelected() ? "Bar Chart" : "Line Chart";
            confirmButtonClicked(xColumnChoice.getValue(), yColumnChoice.getValue(), xFrom, xTo, yFrom, yTo, chartType);
            popupStage.close();
        });
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Select X Column and Range:"), createSelectionBox(xColumnChoice, xRangeFromField, xRangeToField),
                new Label("Select Y Column and Range:"), createSelectionBox(yColumnChoice, yRangeFromField, yRangeToField),
                new Label("Select Chart Type:"), new HBox(10, barChartOption, lineChartOption),
                confirmButton
        );
        popupStage.setScene(new Scene(layout));
        popupStage.showAndWait();
    }

    private void confirmButtonClicked(String colX, String colY, int xFrom, int xTo, int yFrom, int yTo, String chartType) {
        String colXVal = colX.substring(7).trim();
        String colYVal = colY.substring(7).trim();
        String paramsX = colXVal + xFrom + ".." + colXVal + xTo;
        String paramsY = colYVal + yFrom + ".." + colYVal + yTo;
        appcontroller.confirmChartClicked(chartType, paramsX, paramsY);
    }

    //I allow only numbers that does not start with 0. and no negative values (cant put -)
    private TextField createRangeTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setPrefWidth(50);
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return (newText.matches("[1-9]\\d*") || newText.isEmpty()) ? change : null;
        }));
        return textField;
    }

    private HBox createSelectionBox(ChoiceBox<String> columnChoice, TextField rangeFrom, TextField rangeTo) {
        return new HBox(10, columnChoice, new Label("From:"), rangeFrom, new Label("To:"), rangeTo);
    }

    public void createLineChart(List<Double> xValues, List<Double> yValues) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Your Line Chart:");
        int size = Math.min(yValues.size(), xValues.size());
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < size; i++) {
            series.getData().add(new XYChart.Data<>(xValues.get(i), yValues.get(i)));
        }
        lineChart.getData().add(series);

        Stage lineChartStage = new Stage();
        lineChartStage.setTitle("Your Line Chart:");
        StackPane lineChartLayout = new StackPane(lineChart);
        Scene scene = new Scene(lineChartLayout, 800, 600);
        lineChartStage.setScene(scene);
        lineChartStage.show();
    }

    public void createBarChart(List<Double> xValues, List<Double> yValues) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Your Bar Chart:");
        int size = Math.min(yValues.size(), xValues.size());
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < size; i++) {
            series.getData().add(new XYChart.Data<>(xValues.get(i).toString(), yValues.get(i)));
        }
        barChart.getData().add(series);

        Stage barChartStage = new Stage();
        barChartStage.setTitle("Your Bar Chart:");
        StackPane barChartLayout = new StackPane(barChart);
        Scene scene = new Scene(barChartLayout, 800, 600);
        barChartStage.setScene(scene);
        barChartStage.show();
    }

    public void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
