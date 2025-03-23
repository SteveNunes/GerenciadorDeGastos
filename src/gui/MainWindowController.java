package gui;

import entity.TabInterface;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainWindowController {

	@FXML
	private Tab tabGastos;
	@FXML
	private Tab tabPrecosProdutos;
	@FXML
	private Tab tabEstatisticas;
	@FXML
	private Tab tabOpcoes;

	public static GastosTabController gastosController;
	public static PrecosProdutosTabController precosProdutosController;
	public static EstatisticasTabController estatisticasController;
	public static OpcoesTabController opcoesController;
	
	private TabInterface loadTab(Stage stage, Tab tab, TabInterface controller, String fxml) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/" + fxml + ".fxml"));
			VBox vBox = (VBox)loader.load();
			tab.setContent(vBox);
			vBox.prefWidthProperty().bind(stage.widthProperty().add(-16));
			vBox.prefHeightProperty().bind(stage.heightProperty().add(-38));
			controller = loader.getController();
			controller.init(stage, vBox);
			return controller;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Falha ao carregar arquivo FXML \"" + fxml + "\"");
		}
	}
	
	public void init(Stage stage) {
		gastosController = (GastosTabController)loadTab(stage, tabGastos, gastosController, "GastosTabView");
		precosProdutosController = (PrecosProdutosTabController)loadTab(stage, tabPrecosProdutos, precosProdutosController, "PrecosProdutosTabView");
		estatisticasController = (EstatisticasTabController)loadTab(stage, tabEstatisticas, estatisticasController, "EstatisticasTabView");
		opcoesController = (OpcoesTabController)loadTab(stage, tabOpcoes, opcoesController, "OpcoesTabView");
	}
	
}
