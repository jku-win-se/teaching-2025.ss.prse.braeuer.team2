package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
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
                "Rückvergütung pro Monat",
                "Anzahl Rechnungen pro Monat",
                "Durchschnitt Rechnungen pro Benutzer"
        );

        typeSelector.getItems().addAll(
                "beide",
                "Restaurant",
                "Supermarkt"
        );

        statusSelector.getItems().addAll(
                "nur akzeptierte",
                "nur abgelehnte",
                "nur ausstehende",
                "alle"
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

    // Loads data into the BarChart based on the selected metric (AI)
    private void loadChartData(String selectedMetric) {
        barChart.getData().clear(); // Clear previous data
        Double maxYValue = 0.0;
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String barColor = "#3D64E1FF"; // Default bar color
        series.setName(selectedMetric); // Label the chart series

        // Determine filtering conditions based on dropdown selections
        String selectedType = typeSelector.getValue();
        boolean filterRestaurant = selectedType.equals("Restaurant");
        boolean filterSupermarkt = selectedType.equals("Supermarkt");
        List<String> conditions = new ArrayList<>();
        String selectedStatus = statusSelector.getValue();

        // Apply filters for type and status
        switch (selectedStatus) {
            case "nur akzeptierte" -> conditions.add("r.status = 'ACCEPTED'");
            case "nur abgelehnte"  -> conditions.add("r.status = 'DENIED'");
            case "nur ausstehende" -> conditions.add("r.status = 'PENDING'");
            // "alle" means no filter applied
        }
        if (filterRestaurant) conditions.add("r.typ = 'RESTAURANT'");
        if (filterSupermarkt) conditions.add("r.typ = 'SUPERMARKET'");

        String sql = ""; // SQL query placeholder

        // Choose SQL query based on the selected metric
        if (selectedMetric.equals("Rückvergütung pro Monat")) { // Choose the SQL query based on selected metric
            // SQL: Total refund per month over the past 12 months
            sql = """
            WITH monate AS (
                SELECT generate_series(
                    date_trunc('month', CURRENT_DATE) - INTERVAL '11 month',
                    date_trunc('month', CURRENT_DATE),
                    interval '1 month'
                ) AS monat
            )
            SELECT
                to_char(monate.monat, 'Mon YYYY') AS monat,
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

        } else if (selectedMetric.equals("Anzahl Rechnungen pro Monat")) {
            // SQL: Number of invoices per month over the past 12 months
            sql = """
            WITH monate AS (
                SELECT generate_series(
                    date_trunc('month', CURRENT_DATE) - INTERVAL '11 month',
                    date_trunc('month', CURRENT_DATE),
                    interval '1 month'
                ) AS monat
            )
            SELECT
                to_char(monate.monat, 'Mon YYYY') AS monat,
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
            // SQL: Average invoices per user per month
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
            // Add WHERE conditions if necessary
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
                to_char(monat, 'Mon YYYY') AS monat,
                wert
            FROM
                durchschnitt_pro_monat;
        """;
        }

        // Execute the query and build chart data
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String monat = rs.getString("monat");
                double wert = rs.getDouble("wert");
                XYChart.Data<String, Number> data = new XYChart.Data<>(monat, wert);
                series.getData().add(data);
                if (wert > maxYValue) maxYValue = wert; // Track max Y value for scaling
            }

            // Add the filled series to the chart
            barChart.getData().add(series);

            // Customize each bar (tooltip, hover effect, color)
            for (XYChart.Data<String, Number> data : series.getData()) {
                String tooltipText = data.getXValue() + ": " + data.getYValue();
                Tooltip tooltip = new Tooltip(tooltipText);
                Tooltip.install(data.getNode(), tooltip);
                data.getNode().setStyle("-fx-bar-fill: " + barColor);

                // Highlight on hover
                data.getNode().setOnMouseEntered(event ->
                        data.getNode().setStyle("-fx-bar-fill: #78a6d5;")
                );
                data.getNode().setOnMouseExited(event ->
                        data.getNode().setStyle("-fx-bar-fill: " + barColor + ";")
                );
            }

            barChart.setLegendVisible(false); // Hide legend
            resetYAxis(maxYValue); // Adjust Y-axis to fit data

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Fehler:\n" + sql); // Log full SQL for debugging
        }
    }

    // Loads data into the PieChart based on the selected metric
    private void loadPieChartData(String selectedMetric) {
        pieChart.getData().clear(); // Clear previous pie chart data

        // Get filter values from UI dropdowns
        String selectedType = typeSelector.getValue();
        boolean filterRestaurant = selectedType.equals("Restaurant");
        boolean filterSupermarkt = selectedType.equals("Supermarkt");
        List<String> conditions = new ArrayList<>();
        String selectedStatus = statusSelector.getValue();

        // Add SQL conditions based on selected status
        switch (selectedStatus) {
            case "nur akzeptierte" -> conditions.add("r.status = 'ACCEPTED'");
            case "nur abgelehnte"  -> conditions.add("r.status = 'DENIED'");
            case "nur ausstehende" -> conditions.add("r.status = 'PENDING'");
        }
        // Add SQL conditions based on selected type
        if (filterRestaurant) conditions.add("r.typ = 'RESTAURANT'");
        if (filterSupermarkt) conditions.add("r.typ = 'SUPERMARKET'");

        String sql = "";

        // SQL for total refund by type
        if (selectedMetric.equals("Rückvergütung pro Monat")) {
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
        } else if (selectedMetric.equals("Anzahl Rechnungen pro Monat")) {
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
            typeColors.put("RESTAURANT", "-fx-pie-color: #272498FF;");
            typeColors.put("SUPERMARKET", "-fx-pie-color: #1976d2;");

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
            System.err.println("Fehlerhaftes SQL:\n" + sql); // Debug: print SQL if it fails
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

    // Exports chart data to a CSV file (AI)
    private void exportToCSV() {
        // Get selected metric and filters
        String selectedMetric = statSelector.getValue();
        String selectedType = typeSelector.getValue();
        String selectedStatus = statusSelector.getValue();

        // Create and configure a FileChooser for saving the CSV
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName(
                selectedMetric.replace(" ", "_") + "_" +
                        selectedType + "_" +
                        selectedStatus.replace(" ", "_") + ".csv"
        );

        // Show save dialog and proceed if user selects a file
        File file = fileChooser.showSaveDialog(exportCSVButton.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("\"Month\";\"Value\"");

                // Create a number format using German locale (e.g., 1.234,56)
                NumberFormat germanFormat = NumberFormat.getInstance(Locale.GERMAN);
                germanFormat.setMinimumFractionDigits(2);

                // Loop through the chart data and write each data point to the fil
                for (XYChart.Series<String, Number> series : barChart.getData()) {
                    for (XYChart.Data<String, Number> data : series.getData()) {
                        String formattedValue = germanFormat.format(data.getYValue()); // Format value
                        writer.println("\"" + data.getXValue() + "\";" + formattedValue); // Write month and value
                    }
                }

                // Show success alert
                showAlert("Export Successful", "Data exported to CSV successfully!");
            } catch (FileNotFoundException e) {
                // Show error alert if saving fails
                showAlert("Export Error", "Failed to export CSV: " + e.getMessage());
            }
        }
    }

    // Exports chart data to a PDF file (AI)
    private void exportToPDF() {

        // Get selected filters and metric
        String selectedMetric = statSelector.getValue();
        String selectedType = typeSelector.getValue();
        String selectedStatus = statusSelector.getValue();

        // Create and configure a FileChooser for saving the PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName(
                selectedMetric.replace(" ", "_") + "_" +
                        selectedType + "_" +
                        selectedStatus.replace(" ", "_") + ".pdf"
        );

        // Show save dialog and proceed if user selects a file
        File file = fileChooser.showSaveDialog(exportPDFButton.getScene().getWindow());
        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                // Create a new PDF page
                PDPage page = new PDPage(PDRectangle.LETTER);
                document.addPage(page);
                // Take a snapshot of the current bar chart
                WritableImage chartImage = barChart.snapshot(new SnapshotParameters(), null);
                PixelReader reader = chartImage.getPixelReader();
                // Convert the JavaFX image to a BufferedImage
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
                // Convert BufferedImage to a PDImageXObject for PDF
                PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);

                // Add content to the PDF
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // Draw the title
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 750);
                    contentStream.showText(selectedMetric + " (" + selectedType + ", " + selectedStatus + ")");
                    contentStream.endText();

                    // Draw the bar chart image
                    contentStream.drawImage(pdImage, 100, 500, 400, 200);

                    // Add column headers for the data table
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 480);
                    contentStream.showText("Month");
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText("Value");
                    contentStream.endText();

                    // Write each data row below the chart
                    int yPosition = 460;
                    for (XYChart.Series<String, Number> series : barChart.getData()) {
                        for (XYChart.Data<String, Number> data : series.getData()) {
                            contentStream.beginText();
                            contentStream.newLineAtOffset(100, yPosition);
                            contentStream.showText(data.getXValue()); // Month
                            contentStream.newLineAtOffset(150, 0);
                            contentStream.showText(data.getYValue().toString()); // Value
                            contentStream.endText();
                            yPosition -= 20; // Move to the next line
                        }
                    }
                }

                // Save the PDF to the selected file
                document.save(file);
                showAlert("Export Successful", "Data exported to PDF successfully!");
            } catch (IOException e) {
                showAlert("Export Error", "Failed to export PDF: " + e.getMessage());
            }
        }
    }
}