@echo off
set PATH_TO_FX="<Pfad zu javafxsdk>"
set PATH_TO_JAR="<Pfad zur .jar-Datei>" 
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -jar %PATH_TO_JAR%
pause