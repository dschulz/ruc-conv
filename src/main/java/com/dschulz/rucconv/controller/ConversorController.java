package com.dschulz.rucconv.controller;

import com.dschulz.rucconv.model.Contribuyente;
import com.dschulz.rucconv.task.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.TaskProgressView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;


public class ConversorController {

    private Stage stage;

    @FXML
    private Button cargarButton, vaciarButton;

    @FXML
    private TextField recordCountTextField;

    @FXML
    private MenuButton exportarButton, seleccionButton;

    @FXML
    private TableView<Contribuyente> rucsTableView;

    @FXML
    TableColumn<Contribuyente, String> rucColumn;

    @FXML
    TableColumn<Contribuyente, String> denominacionColumn;

    @FXML
    TableColumn<Contribuyente, String> denominacionCorregidaColumn;

    @FXML
    TableColumn<Contribuyente, Integer> dvColumn;

    @FXML
    TableColumn<Contribuyente, String> rucAnteriorColumn;

    @FXML
    TableColumn<Contribuyente, String> estadoColumn;

    @FXML
    TableColumn<Contribuyente, Boolean> activoColumn;

    @FXML
    TableColumn<Contribuyente, Boolean> notasColumn;

    TableView.TableViewSelectionModel<Contribuyente> selectionModel;

    ObservableList<Contribuyente> selectedItems;


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    protected void onCargarButtonClick() {
        cargarButton.setDisable(true);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivos zip");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos Zip", "ruc*.zip"));
        fileChooser.setInitialDirectory(getHomePath());

        List<File> files = fileChooser.showOpenMultipleDialog(stage);

        cargarButton.setDisable(false);

        if (files != null && files.size() > 0) {
            procesarArchivos(files);
        }
    }

    @FXML
    protected void onVaciarButtonClick() {
        rucsTableView.getItems().clear();
    }


    private MenuItem nuevoItem(String textoMenu, Runnable callback) {
        MenuItem item = new MenuItem(textoMenu);
        item.setOnAction(e -> {
            callback.run();
        });
        return item;
    }

    private File getOutputFile(String nombrePreferido){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(getHomePath());
        fileChooser.setInitialFileName(nombrePreferido);
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Archivos SQL (*.sql)", "*.sql"));

        return fileChooser.showSaveDialog(stage);

    }

    private MenuItem[] exportarMenuItems() {

        var exportarSqliteSimple = nuevoItem("SQL para SQLite3 (simple)", () -> {

            File salida = getOutputFile("rucs-sqlite3-simple.sql");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (SqliteSimpleRucPrinter rucPrinter = new SqliteSimpleRucPrinter(salida.getAbsolutePath(), StandardCharsets.UTF_8)) {
                    rucPrinter.export(rucsTableView.getItems());
                    rucPrinter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados.")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });

        var exportarGzippedSqliteSimple = nuevoItem("SQL.gz para SQLite3 (simple)", () -> {

            File salida = getOutputFile("rucs-sqlite3-simple.sql.gz");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (
                    GZIPOutputStream fos = new GZIPOutputStream(new FileOutputStream(salida));
                    SqliteRucPrinter rucPrinter = new SqliteRucPrinter(fos,true)
                ) {
                    rucPrinter.export(rucsTableView.getItems());
                    rucPrinter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados.")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });


        var exportarSqlite = nuevoItem("SQL para SQLite3", () -> {

            File salida = getOutputFile("rucs-sqlite3.sql");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (SqliteRucPrinter rucPrinter = new SqliteRucPrinter(salida.getAbsolutePath(), StandardCharsets.UTF_8)) {
                    rucPrinter.export(rucsTableView.getItems());
                    rucPrinter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados.")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });

        var exportarGzippedSqlite = nuevoItem("SQL.gz para SQLite3", () -> {

            File salida = getOutputFile("rucs-sqlite3.sql.gz");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (
                    GZIPOutputStream fos = new GZIPOutputStream(new FileOutputStream(salida));
                    SqliteRucPrinter rucPrinter = new SqliteRucPrinter(fos,true)
                ) {
                    rucPrinter.export(rucsTableView.getItems());
                    rucPrinter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados.")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });


        var exportarMsSqlServer = nuevoItem("SQL para MS SqlServer", () -> {

            File salida = getOutputFile("rucs-mssqlserver.sql");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (MSSqlServerRucPrinter rucPrinter = new MSSqlServerRucPrinter(salida.getAbsolutePath(), StandardCharsets.UTF_8)) {
                    rucPrinter.export(rucsTableView.getItems());
                    rucPrinter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados.")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });

        var exportarGzippedMsSqlServer = nuevoItem("SQL.gz para MS SqlServer", () -> {

            File salida = getOutputFile("rucs-mssqlserver.sql.gz");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (
                    GZIPOutputStream fos = new GZIPOutputStream(new FileOutputStream(salida));
                    MSSqlServerRucPrinter rucPrinter = new MSSqlServerRucPrinter(fos,true)
                ) {
                    rucPrinter.export(rucsTableView.getItems());
                    rucPrinter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados.")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });


        var exportarOracle = nuevoItem("SQL para Oracle", () -> {

            File salida = getOutputFile("rucs-oracle.sql");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (OracleDialectRucPrinter exporter = new OracleDialectRucPrinter(salida.getAbsolutePath(), StandardCharsets.UTF_8)) {
                    exporter.export(rucsTableView.getItems());
                    exporter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados.")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });


        var exportarGzippedOracle = nuevoItem("SQL.gz para Oracle", () -> {

            File salida = getOutputFile("rucs-oracle.sql.gz");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (
                    GZIPOutputStream fos = new GZIPOutputStream(new FileOutputStream(salida));
                    OracleDialectRucPrinter exporter = new OracleDialectRucPrinter(fos,true)
                ) {
                    exporter.export(rucsTableView.getItems());
                    exporter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados.")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });


        var exportarPostgres = nuevoItem("SQL para PostgreSQL", () -> {

            File salida = getOutputFile("rucs-postgresql.sql");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (PostgresDialectRucPrinter exporter = new PostgresDialectRucPrinter(salida.getAbsolutePath(), StandardCharsets.UTF_8)) {
                    exporter.export(rucsTableView.getItems());
                    exporter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados.")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });

        var exportarGzippedPostgres = nuevoItem("SQL.gz para PostgreSQL", () -> {

            File salida = getOutputFile("rucs-postgresql.sql.gz");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (GZIPOutputStream fos = new GZIPOutputStream(new FileOutputStream(salida));
                     PostgresDialectRucPrinter exporter = new PostgresDialectRucPrinter(fos,true)
                ) {
                    exporter.export(rucsTableView.getItems());
                    exporter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados.")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });

        var exportarH2 = nuevoItem("SQL para H2", () -> {

            File salida = getOutputFile("rucs-h2db.sql");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (H2DialectRucPrinter exporter = new H2DialectRucPrinter(salida.getAbsolutePath(), StandardCharsets.UTF_8)) {
                    exporter.export(rucsTableView.getItems());
                    exporter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados.")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });


        var exportarGzippedH2 = nuevoItem("SQL.gz para H2", () -> {

            File salida = getOutputFile("rucs-h2db.sql.gz");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (
                    GZIPOutputStream fos = new GZIPOutputStream(new FileOutputStream(salida));
                    H2DialectRucPrinter exporter = new H2DialectRucPrinter(fos, true)) {
                    exporter.export(rucsTableView.getItems());
                    exporter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados.")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });


        var exportarJson = nuevoItem("JSON", () -> {

            File salida = getOutputFile("rucs.json");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (JsonRucPrinter exporter = new JsonRucPrinter(salida.getAbsolutePath(), StandardCharsets.UTF_8)) {
                    exporter.export(rucsTableView.getItems());
                    exporter.flush(); // escribir
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados a JSON")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });

        var exportarGzippedJson = nuevoItem("JSON.gz", () -> {

            File salida = getOutputFile("rucs.json.gz");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (
                    GZIPOutputStream fos = new GZIPOutputStream(new FileOutputStream(salida));
                     JsonRucPrinter exporter = new JsonRucPrinter(fos,true)
                ) {
                    exporter.export(rucsTableView.getItems());
                    exporter.flush(); // escribir
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados a JSON")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });

        var exportarCsv =  nuevoItem("CSV", () -> {

            File salida = getOutputFile("rucs.csv");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (CsvRucPrinter exporter = new CsvRucPrinter(salida.getAbsolutePath(), StandardCharsets.UTF_8)) {
                    exporter.export(rucsTableView.getItems());
                    exporter.flush(); // escribir
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados a CSV")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });


        var exportarGzippedCsv =  nuevoItem("CSV.gz", () -> {

            File salida = getOutputFile("rucs.csv.gz");
            if(null==salida)
                return;

            Thread exportThread = new Thread(() -> {

                try (GZIPOutputStream fos = new GZIPOutputStream(new FileOutputStream(salida));
                     CsvRucPrinter exporter = new CsvRucPrinter(fos,true)
                ){
                    exporter.export(rucsTableView.getItems());
                    exporter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    Notifications.create()
                        .title("Finalizado")
                        .text("Registros exportados a CSV")
                        .position(Pos.TOP_CENTER)
                        .showInformation();
                });
            });
            exportThread.start();
        });


        return new MenuItem[]{
            // Plain
            exportarJson,
            exportarCsv,
            exportarSqliteSimple,
            exportarSqlite,
            exportarMsSqlServer,
            exportarPostgres,
            exportarH2,
            exportarOracle,
            // Gzipped
            exportarGzippedJson,
            exportarGzippedCsv,
            exportarGzippedSqliteSimple,
            exportarGzippedSqlite,
            exportarGzippedMsSqlServer,
            exportarGzippedPostgres,
            exportarGzippedH2,
            exportarGzippedOracle
         };
    }

    private MenuItem[] seleccionMenuItems() {

        var eliminarSeleccion = nuevoItem("Eliminar", () -> {
            rucsTableView.setDisable(true);
            rucsTableView.getItems().removeAll(selectedItems);
            selectionModel.clearSelection();
            rucsTableView.setDisable(false);


//            var nuevalista = FXCollections.observableArrayList(rucsTableView.getItems());
//            var seleccionABorrar = FXCollections.observableArrayList(selectionModel.getSelectedItems());
//            Thread eliminarRegistros = new Thread(() -> {
//                nuevalista.removeAll(seleccionABorrar);
//                Platform.runLater(() -> {
//                    rucsTableView.setItems(nuevalista);
//                    selectionModel.clearSelection();
//                    rucsTableView.setDisable(false);
//                });
//            });
//            rucsTableView.setDisable(true);
//            eliminarRegistros.start();

        });

        var copiarPlantillaSeleccion = nuevoItem("Copiar plantilla para sustitución", () -> {
            final ClipboardContent content = new ClipboardContent();

            if (selectedItems.size() == 1) {
                Contribuyente c = selectedItems.get(0);
                content.putString(String.format("%s %s-%d", c.getDenominacion(), c.getRuc(), c.getVerificador()));
                Clipboard.getSystemClipboard().setContent(content);
            }

        });

        var serrSeleccion = nuevoItem("Volcar a la salida de error", () -> {
            selectedItems.forEach((item) -> {
                System.err.println("REGISTRO: " + item.getRuc() + " | " + item.getDenominacion());
            });

        });

        return new MenuItem[]{
            serrSeleccion,
            copiarPlantillaSeleccion,
            eliminarSeleccion,

        };
    }

    @FXML
    public void initialize() {

        exportarButton.getItems().addAll(exportarMenuItems());
        seleccionButton.getItems().addAll(seleccionMenuItems());

        rucsTableView.setPlaceholder(getPlaceholderCargarButton());
        selectionModel = rucsTableView.getSelectionModel();

        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        selectedItems = selectionModel.getSelectedItems();
        rucColumn.setCellValueFactory(new PropertyValueFactory<>("ruc"));
        denominacionColumn.setCellValueFactory(new PropertyValueFactory<>("denominacion"));
        denominacionCorregidaColumn.setCellValueFactory(new PropertyValueFactory<>("denominacionCorregida"));
        dvColumn.setCellValueFactory(new PropertyValueFactory<>("verificador"));
        rucAnteriorColumn.setCellValueFactory(new PropertyValueFactory<>("rucAnterior"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        notasColumn.setCellValueFactory(new PropertyValueFactory<>("notas"));

        activoColumn.setCellValueFactory(cellData -> new ReadOnlyBooleanWrapper(cellData.getValue().isActivo()));
        activoColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activoColumn));

        // Activar estos botones solamente cuando la tabla tenga registros
        vaciarButton.disableProperty().bind(Bindings.size(rucsTableView.getItems()).lessThan(1));
        exportarButton.disableProperty().bind(Bindings.size(rucsTableView.getItems()).lessThan(1));
        seleccionButton.disableProperty().bind(Bindings.size(selectedItems).lessThan(1));

        recordCountTextField.textProperty().bind(Bindings.size((rucsTableView.getItems())).asString());
        recordCountTextField.setDisable(true);



    }

    private Button getPlaceholderCargarButton() {
        Button cargar = new Button("Cargar archivos zip");
        cargar.setOnAction(actionEvent -> onCargarButtonClick());
        return cargar;
    }

    private File getHomePath() {
        String homePath = System.getProperty("user.home");
        return new File(homePath);
    }

    private void procesarArchivos(List<File> files) {

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        //ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        TaskProgressView<ObtenerContribuyentesDesdeZipTask> taskProgressView = new TaskProgressView<>();
        List<ObtenerContribuyentesDesdeZipTask> tasks = new ArrayList<>();

        cargarButton.setDisable(true);


        for (File f : files) {
            ObtenerContribuyentesDesdeZipTask task = getTask(f.getAbsolutePath());
            executorService.submit(task);
            tasks.add(task);
        }

        taskProgressView.getTasks().addAll(tasks);

        final Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setConsumeAutoHidingEvents(true);
        popup.setAutoHide(false);
        popup.setHideOnEscape(false);
        popup.getContent().add(taskProgressView);
        popup.show(stage);

        executorService.shutdown();

        new Thread(() -> {
            try {
                boolean ok = executorService.awaitTermination(1, TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
               // executorService.close();
                Platform.runLater(popup::hide);
                Platform.runLater(() -> {
                    ObservableList<Contribuyente> colectados = FXCollections.observableArrayList();

                    tasks.forEach(t -> {
                        if (t.getValue() != null) {
                            colectados.addAll(t.getValue());
                        }
                    });

                    rucsTableView.getItems().addAll(colectados);
                    cargarButton.setDisable(false);
                });
            }
        }).start();
    }

    private ObtenerContribuyentesDesdeZipTask getTask(String zipFilePath) {
        return new ObtenerContribuyentesDesdeZipTask(zipFilePath);
    }
}
