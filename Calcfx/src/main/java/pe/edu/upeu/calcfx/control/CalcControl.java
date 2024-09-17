package pe.edu.upeu.calcfx.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upeu.calcfx.modelo.CalcTO;
import pe.edu.upeu.calcfx.servicio.CalcServiceI;

import java.util.List;

@Service
public class CalcControl {

    @Autowired
    private CalcServiceI serviceI;

    @FXML
    private TextField txtResultado;

    @FXML
    private TableView<CalcTO> tableView;

    @FXML
    private TableColumn<CalcTO, String> cVal1, cVal2, cRes;

    @FXML
    private TableColumn<CalcTO, Character> cOpe;

    @FXML
    private TableColumn<CalcTO, Void> cOpc;

    private ObservableList<CalcTO> calcTOList;
    private int indexEdit = -1;

    @FXML
    public void accionButton(ActionEvent event) {
        Button button = (Button) event.getSource();
        switch (button.getId()) {
            case "btn7":
            case "btn8":
            case "btn9":
            case "btn6":
            case "btn5":
            case "btn4":
            case "btn3":
            case "btn2":
            case "btn1":
            case "btn0":
                escribirNumeros(button.getText());
                break;
            case "btnSum":
            case "btnMul":
            case "btnRest":
            case "btnDiv":
            case "btnCuadrado":
            case "btnRaiz":
            case "btnPi":
                operador(button.getText());
                break;
            case "btnIgual":
                calcularResultado();
                break;
            case "btnBorrar":
                txtResultado.clear();
                break;
            case "btnBorrar1":
                borrarUltimoCaracter();
                break;
        }
    }

    private void escribirNumeros(String valor) {
        txtResultado.appendText(valor);
    }

    private void operador(String valor) {
        if (valor.equals("√") || valor.equals("^2") || valor.equals("π")) {
            txtResultado.setText(txtResultado.getText() + valor);
        } else {
            txtResultado.appendText(" " + valor + " ");
        }
    }

    private void borrarUltimoCaracter() {
        String texto = txtResultado.getText();
        if (texto != null && !texto.isEmpty()) {
            String nuevoTexto = texto.substring(0, texto.length() - 1).trim();
            txtResultado.setText(nuevoTexto);
        }
    }

    private void calcularResultado() {
        String texto = txtResultado.getText();
        try {
            texto = texto.replace("π", String.valueOf(Math.PI));
            double resultado = 0.0;

            if (texto.contains("√")) {
                String[] partes = texto.split("√");
                if (partes.length == 2) {
                    double valor = Double.parseDouble(partes[1].trim());
                    resultado = Math.sqrt(valor);
                }
            } else if (texto.contains("^2")) {
                String[] partes = texto.split("\\^2");
                if (partes.length == 1) {
                    double valor = Double.parseDouble(partes[0].trim());
                    resultado = valor * valor;
                }
            } else {
                String[] valores = texto.split(" ");
                if (valores.length == 3) {
                    double val1 = Double.parseDouble(valores[0]);
                    double val2 = Double.parseDouble(valores[2]);
                    switch (valores[1]) {
                        case "+": resultado = val1 + val2; break;
                        case "-": resultado = val1 - val2; break;
                        case "/":
                            if (val2 != 0) {
                                resultado = val1 / val2;
                            } else {
                                txtResultado.setText("Error: División por cero");
                                return;
                            }
                            break;
                        case "*": resultado = val1 * val2; break;
                    }
                }
            }
            txtResultado.setText(String.valueOf(resultado));

            // Crear y guardar el objeto CalcTO
            CalcTO to = new CalcTO();
            String[] valores = texto.split(" ");
            if (valores.length == 3) {
                to.setNum1(valores[0]);
                to.setNum2(valores[2]);
                to.setOperador(valores[1].charAt(0));
                to.setResultado(String.valueOf(resultado));

                if (indexEdit != -1) {
                    serviceI.actualizarResultados(to, indexEdit);
                } else {
                    serviceI.guardarResultados(to);
                }
                indexEdit = -1;
                listaOper();
            }

        } catch (NumberFormatException e) {
            txtResultado.setText("Error: Entrada no válida");
        }
    }

    private void editOperCalc(CalcTO cal, int index) {
        System.out.println("Editing: " + cal.getNum1() + " Index:" + index);
        txtResultado.setText(cal.getNum1() + " " + cal.getOperador() + " " + cal.getNum2());
        indexEdit = index;
    }

    private void deleteOperCalc(CalcTO cal, int index) {
        System.out.println("Deleting: " + cal.getNum2());
        serviceI.eliminarResultados(index);
        listaOper();
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<CalcTO, Void>, TableCell<CalcTO, Void>> cellFactory = param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.getStyleClass().setAll("btn", "btn-success");
                editButton.setOnAction(event -> {
                    CalcTO cal = getTableView().getItems().get(getIndex());
                    editOperCalc(cal, getIndex());
                });

                deleteButton.getStyleClass().setAll("btn", "btn-danger");
                deleteButton.setOnAction(event -> {
                    CalcTO cal = getTableView().getItems().get(getIndex());
                    deleteOperCalc(cal, getIndex());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(editButton, deleteButton);
                    buttons.setSpacing(10);
                    setGraphic(buttons);
                }
            }
        };
        cOpc.setCellFactory(cellFactory);
    }

    public void listaOper() {
        List<CalcTO> lista = serviceI.obtenerResultados();
        for (CalcTO to : lista) {
            System.out.println(to.toString());
        }

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        cVal1.setCellValueFactory(new PropertyValueFactory<>("val1"));
        cVal1.setCellFactory(TextFieldTableCell.forTableColumn());

        cVal2.setCellValueFactory(new PropertyValueFactory<>("val2"));
        cVal2.setCellFactory(TextFieldTableCell.forTableColumn());

        cOpe.setCellValueFactory(new PropertyValueFactory<>("operador"));
        cOpe.setCellFactory(ComboBoxTableCell.forTableColumn('+', '-', '/', '*'));

        cRes.setCellValueFactory(new PropertyValueFactory<>("resultado"));
        cRes.setCellFactory(TextFieldTableCell.forTableColumn());

        addActionButtonsToTable();

        calcTOList = FXCollections.observableArrayList(lista);
        tableView.setItems(calcTOList);

        AnchorPane.setLeftAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);

        cOpe.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        cRes.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
        cOpc.prefWidthProperty().bind(tableView.widthProperty().multiply(0.25));
    }
}
