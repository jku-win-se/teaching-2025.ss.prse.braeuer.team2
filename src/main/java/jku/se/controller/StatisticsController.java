package jku.se.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import jku.se.Database;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class StatisticsController extends Controller {

    // Switch back to the admin panel
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        switchScene(event, "adminPanel.fxml");
    }

    // FXML UI components
    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;
    @FXML private ComboBox<String> statSelector;
    @FXML private ComboBox<String> typeSelector;
    @FXML private ComboBox<String> statusSelector;
    @FXML private Button exportCSVButton;
    @FXML private Button exportPDFButton;

    // This method is called automatically after the FXML is loaded (AI)
    public void initialize() {
        // Populate dropdown options
        statSelector.getItems().addAll(
                "Refund per month",
                "Amount of Invoices per month",
                "Average invoices per user"
        );

        typeSelector.getItems().addAll(
                "both",
                "Restaurant",
                "Supermarket"
        );

        statusSelector.getItems().addAll(
                "only accepted",
                "only denied",
                "only pending",
                "all"
        );

        // Set default selections
        typeSelector.getSelectionModel().selectFirst();
        statSelector.getSelectionModel().selectFirst();
        statusSelector.getSelectionModel().selectFirst();

        // Load the initial chart data
        updateCharts();

        // Add listeners to dropdowns to refresh charts when selection changes
        statSelector.valueProperty().addListener((obs, oldVal, newVal) -> updateCharts());
        typeSelector.valueProperty().addListener((obs, oldVal, newVal) -> updateCharts());
        statusSelector.valueProperty().addListener((obs, oldVal, newVal) -> updateCharts());

        // Set up export button actions
        exportCSVButton.setOnAction(event -> exportToCSV());
        exportPDFButton.setOnAction(event -> exportToPDF());
    }

    // Updates both charts based on current selections (AI)
    private void updateCharts() {
        String selectedStat = statSelector.getValue();
        if (selectedStat != null) {
            loadChartData(selectedStat); // Bar chart data
            loadPieChartData(selectedStat); // Pie chart data
        }
    }

    private void loadChartData(String selectedMetric) {
        barChart.getData().clear(); // Clear previous data
        double maxYValue = 0.0;
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String barColor = "#5F1AA3E8"; // Default bar color
        series.setName(selectedMetric); // Label the chart series

        // Determine filtering conditions based on dropdown selections
        String selectedType = typeSelector.getValue();
        boolean filterRestaurant = selectedType.equals("Restaurant");
        boolean filterSupermarkt = selectedType.equals("Supermarkt");
        List<String> conditions = new ArrayList<>();
        String selectedStatus = statusSelector.getValue();

        // Apply filters for type and status
        switch (selectedStatus) {
            case "only accepted" -> conditions.add("r.status = 'ACCEPTED'");
            case "only denied"  -> conditions.add("r.status = 'DENIED'");
            case "only pending" -> conditions.add("r.status = 'PENDING'");
            // "alle" means no filter applied
        }
        if (filterRestaurant) conditions.add("r.typ = 'RESTAURANT'");
        if (filterSupermarkt) conditions.add("r.typ = 'SUPERMARKET'");

        String sql;

        if (selectedMetric.equals("Refund per month")) {
            sql = """
            WITH monate AS (
                SELECT generate_series(
                    date_trunc('month', CURRENT_DATE) - INTERVAL '11 month',
                    date_trunc('month', CURRENT_DATE),
                    interval '1 month'
                ) AS monat
            )
            SELECT
                monate.monat AS monat,
                COALESCE(SUM(r.refund), 0) AS wert
            FROM
                monate
            LEFT JOIN
                rechnungen r ON date_trunc('month', r.datum) = monate.monat
        """;

            if (!conditions.isEmpty()) {
                sql += " AND " + String.join(" AND ", conditions) + "\n";
            }

            sql += """
            GROUP BY
                monate.monat
            ORDER BY
                monate.monat;
        """;

        } else if (selectedMetric.equals("Amount of Invoices per month")) {
            sql = """
            WITH monate AS (
                SELECT generate_series(
                    date_trunc('month', CURRENT_DATE) - INTERVAL '11 month',
                    date_trunc('month', CURRENT_DATE),
                    interval '1 month'
                ) AS monat
            )
            SELECT
                monate.monat AS monat,
                COALESCE(COUNT(r.id), 0) AS wert
            FROM
                monate
            LEFT JOIN
                rechnungen r ON date_trunc('month', r.datum) = monate.monat
        """;

            if (!conditions.isEmpty()) {
                sql += " AND " + String.join(" AND ", conditions) + "\n";
            }

            sql += """
            GROUP BY
                monate.monat
            ORDER BY
                monate.monat;
        """;

        } else {
            sql = """
            WITH monate AS (
                SELECT generate_series(
                    date_trunc('month', CURRENT_DATE) - INTERVAL '11 month',
                    date_trunc('month', CURRENT_DATE),
                    interval '1 month'
                ) AS monat
            ),
            rechnungen_monat_benutzer AS (
                SELECT
                    date_trunc('month', r.datum) AS monat,
                    r.username,
                    COUNT(*) AS anzahl_rechnungen
                FROM
                    rechnungen r
        """;
            if (!conditions.isEmpty()) {
                sql += "WHERE " + String.join(" AND ", conditions) + "\nAND ";
            } else {
                sql += "WHERE ";
            }

            sql += """
                r.datum >= date_trunc('month', CURRENT_DATE) - INTERVAL '11 months'
                GROUP BY
                    date_trunc('month', r.datum), r.username
            ),
            durchschnitt_pro_monat AS (
                SELECT
                    m.monat,
                    ROUND(AVG(rmb.anzahl_rechnungen), 2) AS wert
                FROM
                    monate m
                LEFT JOIN
                    rechnungen_monat_benutzer rmb ON m.monat = rmb.monat
                GROUP BY
                    m.monat
                ORDER BY
                    m.monat
            )
            SELECT
                monat,
                wert
            FROM
                durchschnitt_pro_monat;
        """;
        }

        // Execute the query and build chart data
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.GERMAN);

            while (rs.next()) {
                LocalDate monatDate = rs.getDate("monat").toLocalDate();
                String monatLabel = monatDate.format(formatter);
                double wert = rs.getDouble("wert");
                XYChart.Data<String, Number> data = new XYChart.Data<>(monatLabel, wert);
                series.getData().add(data);
                if (wert > maxYValue) maxYValue = wert;
            }

            barChart.getData().add(series);

            // Tooltip + Hover Styling
            for (XYChart.Data<String, Number> data : series.getData()) {
                String tooltipText = data.getXValue() + ": " + data.getYValue();
                Tooltip tooltip = new Tooltip(tooltipText);
                Tooltip.install(data.getNode(), tooltip);
                data.getNode().setStyle("-fx-bar-fill: " + barColor);

                data.getNode().setOnMouseEntered(event ->
                        data.getNode().setStyle("-fx-bar-fill: #78a6d5;")
                );
                data.getNode().setOnMouseExited(event ->
                        data.getNode().setStyle("-fx-bar-fill: " + barColor + ";")
                );
            }

            barChart.setLegendVisible(false);
            resetYAxis(maxYValue);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Loads data into the PieChart based on the selected metric
    private void loadPieChartData(String selectedMetric) {
        pieChart.getData().clear(); // Clear previous pie chart data

        // Get filter values from UI dropdowns
        String selectedType = typeSelector.getValue();
        boolean filterRestaurant = selectedType.equals("Restaurant");
        boolean filterSupermarkt = selectedType.equals("Supermarket");
        List<String> conditions = new ArrayList<>();
        String selectedStatus = statusSelector.getValue();

        // Add SQL conditions based on selected status
        switch (selectedStatus) {
            case "only accepted" -> conditions.add("r.status = 'ACCEPTED'");
            case "only denied"  -> conditions.add("r.status = 'DENIED'");
            case "only pending" -> conditions.add("r.status = 'PENDING'");
        }
        // Add SQL conditions based on selected type
        if (filterRestaurant) conditions.add("r.typ = 'RESTAURANT'");
        if (filterSupermarkt) conditions.add("r.typ = 'SUPERMARKET'");

        String sql;

        // SQL for total refund by type
        if (selectedMetric.equals("Refund per month")) {
            sql = """
            SELECT
                r.typ,
                SUM(r.refund) AS anzahl_rechnungen
            FROM
                rechnungen r
        """;

            if (!conditions.isEmpty()) {
                sql += "WHERE " + String.join(" AND ", conditions) + "\n";
            }

            sql += "GROUP BY r.typ;";

        // SQL for count of invoices by type
        } else if (selectedMetric.equals("Amount of Invoices per month")) {
            sql = """
            SELECT
                r.typ,
                COUNT(*) AS anzahl_rechnungen
            FROM
                rechnungen r
        """;

            if (!conditions.isEmpty()) {
                sql += "WHERE " + String.join(" AND ", conditions) + "\n";
            }

            sql += "GROUP BY r.typ;";

        // SQL for average invoices per user by type
        } else {
            sql = """
            SELECT
                typ,
                AVG(anzahl_rechnungen) AS anzahl_rechnungen
            FROM (
                SELECT
                    r.typ,
                    COUNT(*) AS anzahl_rechnungen
                FROM
                    rechnungen r
        """;

            if (!conditions.isEmpty()) {
                sql += "WHERE " + String.join(" AND ", conditions) + "\n";
            }

            sql += """
                GROUP BY r.username, r.typ
            ) AS subquery
            GROUP BY typ;
        """;
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Define colors for each type
            Map<String, String> typeColors = new HashMap<>();
            typeColors.put("RESTAURANT", "-fx-pie-color: #692F70E8;");
            typeColors.put("SUPERMARKET", "-fx-pie-color: #5F1AA3E8;");

            // Disable default PieChart visuals (for cleaner custom visuals)
            pieChart.setLabelLineLength(0);
            pieChart.setLabelsVisible(false);
            pieChart.setLegendVisible(false);
            pieChart.setPadding(new Insets(0));

            // Read SQL result and populate the chart
            while (rs.next()) {
                String typ = rs.getString("typ");
                double anzahl = rs.getDouble("anzahl_rechnungen");

                // Create data item and display raw count in the label
                PieChart.Data data = new PieChart.Data(typ, anzahl);
                data.setName(typ + " (" + (int) anzahl + ")");
                pieChart.getData().add(data);

                // Add a tooltip to show value when hovered
                Tooltip tooltip = new Tooltip(data.getName());
                Tooltip.install(data.getNode(), tooltip);
            }

            // Set custom colors for each pie slice based on type
            for (PieChart.Data data : pieChart.getData()) {
                String typ = data.getName().split(" ")[0]; // Extract type
                if (typeColors.containsKey(typ)) {
                    data.getNode().setStyle(typeColors.get(typ));
                }
            }

            // Add external labels around the pie chart
            addPieChartLabels(pieChart);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Adds percentage labels around the PieChart slices (AI)
    private void addPieChartLabels(PieChart chart) {
        // Calculate total value of all slices
        double total = chart.getData().stream().mapToDouble(PieChart.Data::getPieValue).sum();
        double startAngle = 0;

        // Determine the center of the chart
        double centerX = chart.getLayoutX() + chart.getPrefWidth() / 2;
        double centerY = chart.getLayoutY() + chart.getPrefHeight() / 2;

        // Calculate radius values
        double innerRadius = Math.min(chart.getPrefWidth(), chart.getPrefHeight()) / 2.0;
        double labelRadius = innerRadius + 30; // Distance from center to label

        Pane parent = (Pane) chart.getParent();

        // If only one slice, remove existing labels and skip adding new ones
        if (chart.getData().size() == 1) {
            parent.getChildren().removeIf(node ->
                    ("pie-label".equals(node.getId()) || "pie-line".equals(node.getId())));
            return;
        }

        // Clear any previously added labels
        parent.getChildren().removeIf(node ->
                ("pie-label".equals(node.getId()) || "pie-line".equals(node.getId())));

        // Loop through each slice to place a label
        for (PieChart.Data data : chart.getData()) {
            double angle = (data.getPieValue() / total) * 360; // Slice angle
            double midAngle = Math.toRadians(startAngle + angle / 2); // Middle of slice

            int percent = (int) ((data.getPieValue() / total) * 100); // Percentage value
            String shortLabel = abbreviate(data.getName()); // Shorten the name if needed
            String labelText = shortLabel + " (" + percent + "%)"; // Label text

            // Calculate label position based on angle and radius
            double labelX = centerX + labelRadius * Math.cos(midAngle);
            double labelY = centerY + labelRadius * Math.sin(midAngle);

            // Create and style label
            Text label = new Text(labelText);
            label.setId("pie-label");
            label.setFill(Color.BLACK);
            label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            label.setLayoutX(labelX - label.prefWidth(-1) / 2); // Center horizontally
            label.setLayoutY(labelY); // Vertical position

            // Add label to the chart's parent container
            parent.getChildren().addAll(label);
            startAngle += angle; // Move to the next slice
        }
    }

    // Returns an abbreviation for a given category name (AI)
    private String abbreviate(String name) {
        switch (name.toUpperCase()) {
            case "SUPERMARKET":
                return "SUP";
            case "RESTAURANT":
                return "RES";
            default:
                return name.length() <= 3 ? name : name.substring(0, 3).toUpperCase();
        }
    }

    // Resets the Y-axis of the bar chart based on the maximum Y value (AI)
    private void resetYAxis(double maxYValue) {
        int stepSize = calculateStepSize(maxYValue); // Determine appropriate tick step
        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
        yAxis.setAutoRanging(false); // Disable automatic scaling
        yAxis.setLowerBound(0); // Start from 0

        // Calculate and set a rounded upper bound based on the step size
        double upperBound = Math.ceil(maxYValue / stepSize) * stepSize;
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(stepSize); // Set distance between tick marks
    }

    // Determines the tick step size based on the maximum Y value (AI)
    private int calculateStepSize(double maxYValue) {
        if (maxYValue <= 50) return 5;
        else if (maxYValue <= 100) return 10;
        else if (maxYValue <= 500) return 20;
        else if (maxYValue <= 1000) return 50;
        else return 100; // Default step size for larger values
    }

    private void exportToCSV() {
        String selectedMetric = statSelector.getValue();
        String selectedType = typeSelector.getValue();
        String selectedStatus = statusSelector.getValue();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV-file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV-file", "*.csv"));
        fileChooser.setInitialFileName(
                selectedMetric.replace(" ", "_") + "_" +
                        selectedType + "_" +
                        selectedStatus.replace(" ", "_") + ".csv"
        );

        File file = fileChooser.showSaveDialog(exportCSVButton.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Deutsche Spaltenüberschriften
                writer.println("\"Month\";\"Amount\"");

                // Formatierung im deutschen Stil (z.B. 1.234,56)
                NumberFormat germanFormat = NumberFormat.getInstance(Locale.GERMAN);
                germanFormat.setMinimumFractionDigits(2);

                for (XYChart.Series<String, Number> series : barChart.getData()) {
                    for (XYChart.Data<String, Number> data : series.getData()) {
                        String formattedValue = germanFormat.format(data.getYValue());
                        writer.println("\"" + data.getXValue() + "\";" + formattedValue);
                    }
                }

                showSuccess("Export successful", "Data was successfully exported as a CSV-file!");
            } catch (FileNotFoundException e) {
                showError("Error", "CSV Export failed: " + e.getMessage());
            }
        }
    }


    // Exports chart data to a PDF file (AI)
    private void exportToPDF() {
        String selectedMetric = statSelector.getValue();
        String selectedType = typeSelector.getValue();
        String selectedStatus = statusSelector.getValue();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Safe PDF-file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF-file", "*.pdf"));
        fileChooser.setInitialFileName(
                selectedMetric.replace(" ", "_") + "_" +
                        selectedType + "_" +
                        selectedStatus.replace(" ", "_") + ".pdf"
        );

        File file = fileChooser.showSaveDialog(exportPDFButton.getScene().getWindow());
        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage(PDRectangle.LETTER);
                document.addPage(page);

                WritableImage chartImage = barChart.snapshot(new SnapshotParameters(), null);
                PixelReader reader = chartImage.getPixelReader();

                BufferedImage bufferedImage = new BufferedImage(
                        (int) chartImage.getWidth(),
                        (int) chartImage.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );
                for (int y = 0; y < chartImage.getHeight(); y++) {
                    for (int x = 0; x < chartImage.getWidth(); x++) {
                        bufferedImage.setRGB(x, y, reader.getArgb(x, y));
                    }
                }

                PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // Titel auf Deutsch
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 750);
                    contentStream.showText("Statistics " + selectedMetric + " (" + selectedType + ", " + selectedStatus + ")");
                    contentStream.endText();

                    // Diagrammbild
                    contentStream.drawImage(pdImage, 100, 500, 400, 200);

                    // Tabellenüberschriften auf Deutsch
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 480);
                    contentStream.showText("Month");
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText("Amount");
                    contentStream.endText();

                    int yPosition = 460;
                    for (XYChart.Series<String, Number> series : barChart.getData()) {
                        for (XYChart.Data<String, Number> data : series.getData()) {
                            contentStream.beginText();
                            contentStream.newLineAtOffset(100, yPosition);
                            contentStream.showText(data.getXValue()); // Monat
                            contentStream.newLineAtOffset(150, 0);
                            contentStream.showText(data.getYValue().toString()); // Wert
                            contentStream.endText();
                            yPosition -= 20;
                        }
                    }
                }

                document.save(file);
                showSuccess("Export successful", "Data was successfully exported as PDF!");
            } catch (IOException e) {
                showError("Error", "Export failed: " + e.getMessage());
            }
        }
    }

}