package jku.se;

import javafx.scene.control.Label;
import jku.se.Controller.SubmitBillController;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;


public class Main {
    public static void main(String[] args) {

        SubmitBillController controller = new SubmitBillController();
        String path = "C:/Users/Lukas/Desktop/Rechnungen/6.jpg";
        Database.invoiceScanUpload(path, controller);

    }
}
