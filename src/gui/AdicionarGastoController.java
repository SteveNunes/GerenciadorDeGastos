package gui;

import java.sql.Date;
import java.util.Arrays;
import java.util.Set;

import entity.Gasto;
import entity.Valor;
import enums.Icons;
import gui.util.ControllerUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdicionarGastoController {
	
  @FXML
  private VBox vBoxMain;
  @FXML
  private Button buttonSalvar;
  @FXML
  private DatePicker datePicker;
  @FXML
  private TextField textFieldRef;
  @FXML
  private TextField textFieldEntrada;
  @FXML
  private TextField textFieldSaida;
  @FXML
  private TextField textFieldParcelas;
  @FXML
  private Label labelError;
  
  private int parcelas;
  
  public void load(Gasto gasto, Set<String> autoCompletion) {
		datePicker.setValue(gasto.getDate().toLocalDate());
		datePicker.valueProperty().addListener((o, oldV, newV) -> gasto.setDate(Date.valueOf(datePicker.getValue())));
		textFieldRef.setText(gasto.getReferencia());
		textFieldRef.textProperty().addListener((o, oldV, newV) -> gasto.setReferencia(newV));
		textFieldEntrada.setText(gasto.getEntrada().toString());
		textFieldEntrada.textProperty().addListener((o, oldV, newV) -> gasto.setEntrada(stringToValor(gasto.getEntrada(), newV)));
		textFieldSaida.setText(gasto.getSaida().toString());
		textFieldSaida.textProperty().addListener((o, oldV, newV) -> gasto.setSaida(stringToValor(gasto.getSaida(), newV)));
		ControllerUtils.setAutoCompletionOnTextField(textFieldRef, 10, autoCompletion);
  }
  
	public void init(Stage stage) {
		parcelas = 1;
		buttonSalvar.setOnAction(e -> stage.close());
		Platform.runLater(() -> textFieldRef.requestFocus());
		textFieldParcelas.textProperty().addListener((o, oldV, newV) -> {
			try {
				int v = newV.isBlank() ? 1 : Integer.parseInt(newV);
				if (v < 1)
					textFieldParcelas.setText(oldV);
				parcelas = v;
			}
			catch (Exception e) {
				textFieldParcelas.setText(oldV);
			}
		});
    ControllerUtils.addIconToButton(buttonSalvar, Icons.SAVE.getValue(), 15, 15);
    for (TextField tf : Arrays.asList(textFieldEntrada, textFieldSaida, textFieldRef))
			tf.setOnKeyPressed(e -> {
				if (e.getCode() == KeyCode.ENTER)
					stage.close();
			});
	}
	
	public int getParcelas() {
		return parcelas;
	}
	
	private Valor stringToValor(Valor valor, String text) {
		try {
			Valor v = new Valor(text.replace(",", "."));
			labelError.setText("");
			return v;
		}
		catch (Exception e) {
			try {
				new Valor(textFieldEntrada.getText().replace(",", "."));
				labelError.setText("Valor inválido no campo de SAÍDA");
			}
			catch (Exception e2) {
				labelError.setText("Valor inválido no campo de ENTRADA");
			}
			return valor;
		}
	}

}
