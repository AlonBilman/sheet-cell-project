package components.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.StackPane;

import javafx.scene.chart.CategoryAxis;

import java.util.ArrayList;
import java.util.List;

public class ChartMaker {

    private AppController appcontroller;

    public ChartMaker(AppController appcontroller) {
        this.appcontroller = appcontroller;
    }

    public void createChartDialogPopup(int columnCount) {
        ObservableList<String> columnOptions = FXCollections.observableArrayList();
        for (int i = 1; i <= columnCount; i++) {
            columnOptions.add("Column " + (char) ('A' + i - 1));
        }
        Stage popupStage = new Stage();
        popupStage.setTitle("Gr-Settings");
        popupStage.setResizable(false);
        popupStage.initModality(Modality.APPLICATION_MODAL);

        ChoiceBox<String> xColumnChoice = new ChoiceBox<>(columnOptions);
        xColumnChoice.setMinSize(100,25);
        ChoiceBox<String> yColumnChoice = new ChoiceBox<>(columnOptions);
        yColumnChoice.setMinSize(100,25);

        xColumnChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            yColumnChoice.getItems().remove(newVal);
            if (oldVal != null) {
                yColumnChoice.getItems().add(oldVal);
            }
        });

        yColumnChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            xColumnChoice.getItems().remove(newVal);
            if (oldVal != null) {
                xColumnChoice.getItems().add(oldVal);
            }
        });

        ToggleGroup chartTypeGroup = new ToggleGroup();
        RadioButton barChartOption = new RadioButton("Bar Chart");
        barChartOption.setToggleGroup(chartTypeGroup);
        barChartOption.setSelected(true);

        RadioButton lineChartOption = new RadioButton("Line Chart");
        lineChartOption.setToggleGroup(chartTypeGroup);

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {
            String xColumn = xColumnChoice.getValue();
            String yColumn = yColumnChoice.getValue();

            if (xColumn == null || yColumn == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Invalid Selection");
                alert.setHeaderText(null);
                alert.setContentText("Please select both X and Y columns.");
                alert.showAndWait();
                return;
            }
            String chartType = ((RadioButton) chartTypeGroup.getSelectedToggle()).getText();
            confirmButton.setDisable(true);
            chartValuesDialog(chartType, xColumn, yColumn);
            popupStage.close();
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                new Label("Select X Column:"), xColumnChoice,
                new Label("Select Y Column:"), yColumnChoice,
                new Label("Select Chart Type:"), barChartOption, lineChartOption,
                confirmButton
        );

        Scene scene = new Scene(layout);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    public void chartValuesDialog(String chartType, String xColumn, String yColumn) {
        List<String> xColumnValues = appcontroller.getColValuesForChart(chartType, xColumn.substring(6), true);
        List<String> yColumnValues = appcontroller.getColValuesForChart(chartType, yColumn.substring(6), false);

        List<String> selectedXValues = new ArrayList<>();
        List<String> selectedYValues = new ArrayList<>();

        Stage popupStage = new Stage();
        popupStage.setTitle("Select X and Corresponding Y Values");

        Button infoButton = createInfoButton();

        ListView<String> xValuesList = createListView(xColumnValues);
        ListView<String> yValuesList = createListView(yColumnValues);
        yValuesList.setDisable(true);

        Label xLabel = new Label("Select X Value:");
        Label yLabel = new Label("Select Corresponding Y Value:");

        Button confirmButton = new Button("Confirm");
        confirmButton.setDisable(true);

        xValuesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            yValuesList.setDisable(false);
            confirmButton.setDisable(true);
            yValuesList.getSelectionModel().clearSelection();
        });

        yValuesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            confirmButton.setDisable(xValuesList.getSelectionModel().getSelectedItem() == null || newVal == null);
        });

        confirmButton.setOnAction(e -> handleConfirm(xValuesList, yValuesList, selectedXValues, selectedYValues, confirmButton));

        Button finalizeButton = new Button("Finalize Selection");
        finalizeButton.setOnAction(e -> {
            if (chartType.equals("Line Chart")) {
                createLineChart(selectedXValues, selectedYValues);
            } else {
                createBarChart(selectedXValues, selectedYValues);
            }
            popupStage.close();
        });
        VBox layout = new VBox(10, infoButton, xLabel, xValuesList, yLabel, yValuesList, confirmButton, finalizeButton);
        layout.setPadding(new Insets(20));
        popupStage.setScene(new Scene(layout, 400, 400));
        popupStage.showAndWait();
    }

    private Button createInfoButton() {
        Button infoButton = new Button("Press For Info");
        infoButton.setOnAction(e -> {
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Information");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("""
                    Select the X values and their corresponding Y values.
                    Once you are finished selecting please press the Confirm button.
                                       \s
                    You can add as many values as you can (until there is no more values).
                                       \s
                    Press Finalize Selection when you're done and enjoy the chart.""");
            infoAlert.showAndWait();
        });
        return infoButton;
    }

    private ListView<String> createListView(List<String> values) {
        ListView<String> listView = new ListView<>(FXCollections.observableArrayList(values));
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        return listView;
    }

    private void handleConfirm(ListView<String> xValuesList, ListView<String> yValuesList,
                               List<String> selectedXValues, List<String> selectedYValues, Button confirmButton) {
        String selectedX = xValuesList.getSelectionModel().getSelectedItem();
        String selectedY = yValuesList.getSelectionModel().getSelectedItem();

        if (selectedX != null && selectedY != null) {
            selectedXValues.add(selectedX);
            selectedYValues.add(selectedY);

            xValuesList.getItems().remove(selectedX);
            yValuesList.getItems().remove(selectedY);

            xValuesList.getSelectionModel().clearSelection();
            yValuesList.getSelectionModel().clearSelection();

            yValuesList.setDisable(true);
            confirmButton.setDisable(xValuesList.getItems().isEmpty() || yValuesList.getItems().isEmpty());
        }
    }

    public void createLineChart(List<String> xValues, List<String> yValues) {
        //ok to do, we made sure its only doubles.
        List<Double> xNumericValues = xValues.stream().map(Double::parseDouble).toList();
        List<Double> yNumericValues = yValues.stream().map(Double::parseDouble).toList();

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Your Line Chart:");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < xNumericValues.size(); i++) {
            series.getData().add(new XYChart.Data<>(xNumericValues.get(i), yNumericValues.get(i)));
        }
        lineChart.getData().add(series);

        Stage lineChartStage = new Stage();
        lineChartStage.setTitle("Your Bart Chart:");
        StackPane lineChartLayout = new StackPane(lineChart);
        Scene scene = new Scene(lineChartLayout, 800, 600);
        lineChartStage.setScene(scene);
        lineChartStage.show();
    }

    public void createBarChart(List<String> xValues, List<String> yValues) {

        List<Double> yNumericValues = yValues.stream().map(Double::parseDouble).toList();

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Your Bart Chart:");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < yNumericValues.size(); i++) {
            series.getData().add(new XYChart.Data<>(xValues.get(i), yNumericValues.get(i)));
        }
        barChart.getData().add(series);

        Stage barChartStage = new Stage();
        barChartStage.setTitle("Your Bar Chart:");
        StackPane barChartLayout = new StackPane(barChart);
        Scene scene = new Scene(barChartLayout, 800, 600);
        barChartStage.setScene(scene);
        barChartStage.show();
    }
}
