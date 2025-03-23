package gui;

import java.sql.Date;
import java.util.Arrays;
import java.util.Set;

import entity.Produto;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AdicionarProdutoController {
	
  @FXML
  private VBox vBoxMain;
  @FXML
  private Button buttonSalvar;
  @FXML
  private DatePicker datePicker;
  @FXML
  private TextField textFieldProduto;
  @FXML
  private TextField textFieldValor;
  @FXML
  private VBox vBoxError;
  
  private Stage stage;
  
	public void init(Stage stage, Produto produto, boolean novo, Set<String> autoCompletion) {
		this.stage = stage;
		datePicker.setValue(produto.getPrecoMinimo().getDate().toLocalDate());
		datePicker.valueProperty().addListener((o, oldV, newV) -> produto.getPrecoMinimo().setDate(Date.valueOf(datePicker.getValue())));
		textFieldProduto.setDisable(!novo);
		textFieldProduto.setText(produto.getNome());
		if (novo)
			textFieldProduto.textProperty().addListener((o, oldV, newV) -> {
				produto.setNome(newV);
				for (String s : autoCompletion)
					if (s.trim().toLowerCase().equals(newV.trim().toLowerCase())) {
						setErrors("O produto já se encontra na lista.\nO preço informado será adicionado á lista de preços do produto.");
						return;
					}
				clearErrors();
			});
		textFieldValor.setText(produto.getPrecoMinimo().toString());
		textFieldValor.textProperty().addListener((o, oldV, newV) -> produto.getPrecoMinimo().setValor(stringToValor(produto.getPrecoMinimo(), newV)));
		buttonSalvar.setOnAction(e -> stage.close());
		Platform.runLater(() -> textFieldProduto.requestFocus());
    ControllerUtils.addIconToButton(buttonSalvar, Icons.SAVE.getValue(), 15, 15);
    for (TextField tf : Arrays.asList(textFieldValor, textFieldProduto))
			tf.setOnKeyPressed(e -> {
				if (e.getCode() == KeyCode.ENTER)
					stage.close();
			});
		ControllerUtils.setAutoCompletionOnTextField(textFieldProduto, 10, autoCompletion);
	}
	
	private Valor stringToValor(Valor valor, String text) {
		try {
			Valor v = new Valor(text.replace(",", "."));
			clearErrors();
			return v;
		}
		catch (Exception e) {
			setErrors("Valor inválido no campo de VALOR");
			return valor;
		}
	}
	
	private void setErrors(String string) {
		clearErrors();
		for (String s : string.split("\n")) {
			Label label = new Label(s);
			label.setTextFill(Color.RED);
			vBoxError.getChildren().add(label);
		}
		stage.sizeToScene();
	}
	
	private void clearErrors() {
		vBoxError.getChildren().clear();
	}

}
