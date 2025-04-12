package jku.se.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import javafx.stage.FileChooser;
import jku.se.Database;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.io.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.common.PDRectangle;


public class StatisticsController extends Controller {

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        switchScene(event, "adminPanel.fxml");
    }

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private PieChart pieChart;

    @FXML
    private ComboBox<String> statSelector;

    @FXML
    private ComboBox<String> typeSelector;

    public void initialize() {
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


        typeSelector.getSelectionModel().selectFirst();
        statSelector.getSelectionModel().selectFirst();

        // Initial load with default selections
        loadChartData(statSelector.getValue());
        loadPieChartData(statSelector.getValue());

        // Add listeners for both selectors
        statSelector.setOnAction(event -> {
            String selectedStat = statSelector.getValue();
            loadChartData(selectedStat);
            loadPieChartData(selectedStat);
        });

        typeSelector.setOnAction(event -> {
            String selectedStat = statSelector.getValue();
            loadChartData(selectedStat);
            loadPieChartData(selectedStat);
        });


        // Set up export buttons
        exportCSVButton.setOnAction(event -> exportToCSV());
        exportPDFButton.setOnAction(event -> exportToPDF());
    }

    private void loadChartData(String selectedMetric) {
        barChart.getData().clear();
        Double maxYValue = 0.0;
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String barColor = "#3D64E1FF";
        series.setName(selectedMetric);

        // Get selected type filter
        String selectedType = typeSelector.getValue();
        boolean filterRestaurant = selectedType.equals("Restaurant");
        boolean filterSupermarkt = selectedType.equals("Supermarkt");

        // Base SQL queries with type filter
        String sql;
        if (selectedMetric.equals("Rückvergütung pro Monat")) {
            sql = """
        WITH monate AS (
            SELECT generate_series(
                date_trunc('month', CURRENT_DATE) - INTERVAL '11 months',
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
            AND r.status = 'ACCEPTED'
            """ + (filterRestaurant ? "AND r.typ = 'RESTAURANT'" : "") +
                    (filterSupermarkt ? "AND r.typ = 'SUPERMARKET'" : "") + """
        GROUP BY
            monate.monat
        ORDER BY
            monate.monat;
        """;
        } else if (selectedMetric.equals("Anzahl Rechnungen pro Monat")) {
            sql = """
        WITH monate AS (
            SELECT generate_series(
                date_trunc('month', CURRENT_DATE) - INTERVAL '11 months',
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
            AND r.status = 'ACCEPTED'
            """ + (filterRestaurant ? "AND r.typ = 'RESTAURANT'" : "") +
                    (filterSupermarkt ? "AND r.typ = 'SUPERMARKET'" : "") + """
        GROUP BY
            monate.monat
        ORDER BY
            monate.monat;
        """;
        } else {
            sql = """
        WITH monate AS (
            SELECT generate_series(
                date_trunc('month', CURRENT_DATE) - INTERVAL '11 months',
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
            WHERE
                r.status = 'ACCEPTED'
            AND r.datum >= date_trunc('month', CURRENT_DATE) - INTERVAL '11 months'
            """ + (filterRestaurant ? "AND r.typ = 'RESTAURANT'" : "") +
                    (filterSupermarkt ? "AND r.typ = 'SUPERMARKET'" : "") + """
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

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String monat = rs.getString("monat");
                double wert = rs.getDouble("wert");

                XYChart.Data<String, Number> data = new XYChart.Data<>(monat, wert);
                series.getData().add(data);

                if (wert > maxYValue) {
                    maxYValue = wert;
                }
            }

            barChart.getData().add(series);

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

    private void loadPieChartData(String selectedMetric) {
        pieChart.getData().clear();

        // Get selected type filter
        String selectedType = typeSelector.getValue();
        boolean filterRestaurant = selectedType.equals("Restaurant");
        boolean filterSupermarkt = selectedType.equals("Supermarkt");

        // Modify SQL query based on filter and stat selector
        String sql = "";
        if (selectedMetric.equals("Rückvergütung pro Monat")) {
            sql = """
    SELECT
        typ,
        SUM(r.refund) AS anzahl_rechnungen
    FROM
        rechnungen r
    WHERE
        r.status = 'ACCEPTED'
        """ + (filterRestaurant ? "AND r.typ = 'RESTAURANT'" : "") +
                    (filterSupermarkt ? "AND r.typ = 'SUPERMARKET'" : "") + """
    GROUP BY
        r.typ;
    """;
        } else if (selectedMetric.equals("Anzahl Rechnungen pro Monat")) {
            sql = """
    SELECT
        typ,
        COUNT(*) AS anzahl_rechnungen
    FROM
        rechnungen r
    WHERE
        r.status = 'ACCEPTED'
        """ + (filterRestaurant ? "AND r.typ = 'RESTAURANT'" : "") +
                    (filterSupermarkt ? "AND r.typ = 'SUPERMARKET'" : "") + """
    GROUP BY
        r.typ;
    """;
        } else {
            sql = """
    SELECT
        typ,
        AVG(anzahl_rechnungen) AS anzahl_rechnungen
    FROM
        (SELECT
            r.typ,
            COUNT(*) AS anzahl_rechnungen
        FROM
            rechnungen r
        WHERE
            r.status = 'ACCEPTED'
            """ + (filterRestaurant ? "AND r.typ = 'RESTAURANT'" : "") +
                    (filterSupermarkt ? "AND r.typ = 'SUPERMARKET'" : "") + """
        GROUP BY
            r.username, r.typ) AS subquery
    GROUP BY
        typ;
    """;
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Define fixed colors for each type
            Map<String, String> typeColors = new HashMap<>();
            typeColors.put("RESTAURANT", "-fx-pie-color: #272498FF;");  // Orange
            typeColors.put("SUPERMARKET", "-fx-pie-color: #1976d2;"); // Blue

            while (rs.next()) {
                String typ = rs.getString("typ");
                double anzahlRechnungen = rs.getDouble("anzahl_rechnungen");

                PieChart.Data data = new PieChart.Data(typ, anzahlRechnungen);
                data.setName(typ + " (" + (int) anzahlRechnungen + ")");
                pieChart.getData().add(data);

                // Apply the predefined color based on type
                if (typ != null && typeColors.containsKey(typ)) {
                    data.getNode().setStyle(typeColors.get(typ) +
                            " -fx-font-size: 14px; -fx-font-weight: bold;");
                }

                Tooltip tooltip = new Tooltip(data.getName());
                Tooltip.install(data.getNode(), tooltip);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetYAxis(double maxYValue) {
        int stepSize = calculateStepSize(maxYValue);
        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        double upperBound = Math.ceil(maxYValue / stepSize) * stepSize;
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit(stepSize);
    }

    private int calculateStepSize(double maxYValue) {
        if (maxYValue <= 50) return 5;
        else if (maxYValue <= 100) return 10;
        else if (maxYValue <= 500) return 20;
        else if (maxYValue <= 1000) return 50;
        else return 100;
    }



    @FXML
    private Button exportCSVButton;

    @FXML
    private Button exportPDFButton;

    // ... existing fields and methods ...

    public void initialize1() {
        // ... existing initialization code ...


    }

    private void exportToCSV() {
        String selectedMetric = statSelector.getValue();
        String selectedType = typeSelector.getValue();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName(selectedMetric.replace(" ", "_") + "_" + selectedType + ".csv");

        File file = fileChooser.showSaveDialog(exportCSVButton.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                // Header mit Semikolon und Anführungszeichen
                writer.println("\"Month\";\"Value\"");

                // Daten mit Semikolon und Komma als Dezimaltrennzeichen
                NumberFormat germanFormat = NumberFormat.getInstance(Locale.GERMAN);
                germanFormat.setMinimumFractionDigits(2);

                for (XYChart.Series<String, Number> series : barChart.getData()) {
                    for (XYChart.Data<String, Number> data : series.getData()) {
                        String formattedValue = germanFormat.format(data.getYValue());
                        writer.println("\"" + data.getXValue() + "\";" + formattedValue);
                    }
                }

                showAlert("Export Successful", "Data exported to CSV successfully!");
            } catch (FileNotFoundException e) {
                showAlert("Export Error", "Failed to export CSV: " + e.getMessage());
            }
        }
    }

    private void exportToPDF() {
        String selectedMetric = statSelector.getValue();
        String selectedType = typeSelector.getValue();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName(selectedMetric.replace(" ", "_") + "_" + selectedType + ".pdf");

        File file = fileChooser.showSaveDialog(exportPDFButton.getScene().getWindow());
        if (file != null) {
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage(PDRectangle.LETTER);
                document.addPage(page);

                // ChartImage -> BufferedImage
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

                // Convert BufferedImage to PDImageXObject
                PDImageXObject pdImage = LosslessFactory.createFromImage(document, bufferedImage);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // Title
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 750);
                    contentStream.showText(selectedMetric + " (" + selectedType + ")");
                    contentStream.endText();

                    // Insert chart image
                    contentStream.drawImage(pdImage, 100, 500, 400, 200); // x, y, width, height

                    // Table Header
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 480);
                    contentStream.showText("Month");
                    contentStream.newLineAtOffset(150, 0);
                    contentStream.showText("Value");
                    contentStream.endText();

                    // Table Content
                    int yPosition = 460;
                    for (XYChart.Series<String, Number> series : barChart.getData()) {
                        for (XYChart.Data<String, Number> data : series.getData()) {
                            contentStream.beginText();
                            contentStream.newLineAtOffset(100, yPosition);
                            contentStream.showText(data.getXValue());
                            contentStream.newLineAtOffset(150, 0);
                            contentStream.showText(data.getYValue().toString());
                            contentStream.endText();
                            yPosition -= 20;
                        }
                    }
                }

                document.save(file);
                showAlert("Export Successful", "Data exported to PDF successfully!");
            } catch (IOException e) {
                showAlert("Export Error", "Failed to export PDF: " + e.getMessage());
            }
        }
    }


    // Hilfsmethode für Tabellenkopf
    private void drawTableHeader(PDPageContentStream contentStream, float x, float y, float width) throws IOException {
        float colWidth = width / 2;
        contentStream.setNonStrokingColor(200, 200, 200); // Grauer Hintergrund

        // Header-Zellen zeichnen
        contentStream.addRect(x, y - 15, colWidth, 20);
        contentStream.addRect(x + colWidth, y - 15, colWidth, 20);
        contentStream.fill();

        contentStream.setNonStrokingColor(0, 0, 0); // Schwarzer Text
        contentStream.beginText();
        contentStream.newLineAtOffset(x + 5, y - 10);
        contentStream.showText("Month");
        contentStream.newLineAtOffset(colWidth, 0);
        contentStream.showText("Value");
        contentStream.endText();
    }

    // Hilfsmethode für Tabellenzeilen
    private void drawTableRow(PDPageContentStream contentStream, float x, float y, float width,
                              String month, String value) throws IOException {
        float colWidth = width / 2;

        contentStream.beginText();
        contentStream.newLineAtOffset(x + 5, y);
        contentStream.showText(month);
        contentStream.newLineAtOffset(colWidth, 0);
        contentStream.showText(value);
        contentStream.endText();

        // Unterstrich für jede Zeile
        contentStream.moveTo(x, y - 5);
        contentStream.lineTo(x + width, y - 5);
        contentStream.stroke();
    }

    // Helper method to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}