package gui;

import java.sql.Date;

import entity.Gasto;
import entity.TabInterface;
import entity.Valor;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EstatisticasTabController implements TabInterface {

  @FXML
  private VBox vBoxMain;
  @FXML
  private VBox vBoxTexts;

	public void init(Stage stage, VBox vBox) {
		Gasto gastoTotal = new Gasto();
		Gasto mesMenorGasto = new Gasto(new Date(System.currentTimeMillis()), "", new Valor("999999999999"), new Valor("999999999999"));
		Gasto mesMaiorGasto = new Gasto();
		int meses = 0;
		/*
		 * Calcular:
		 * GASTO TOTAL
		 * MES COM MENOR ENTRADA/SAIDA
		 * MES COM MAIOR ENTRADA/SAIDA
		 * MEDIA DE ENTRADA/SAIDA MENSAL
		 */
		for (Gasto g : GastosTabController.getTodosOsGastos()) {
			
		}
	}
  
}
