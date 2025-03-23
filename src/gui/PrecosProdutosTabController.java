package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import application.Main;
import entity.Produto;
import entity.TabInterface;
import entity.Tools;
import entity.Valor;
import enums.Icons;
import gui.util.ControllerUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.IniFile;

public class PrecosProdutosTabController implements TabInterface {


  @FXML
  private VBox vBoxMain;
  @FXML
  private Button buttonAdd;
  @FXML
  private Button buttonDel;
  @FXML
  private TableColumn<Produto, String> tableColumnProdutos;
  @FXML
  private TableColumn<Produto, String> tableColumnMinPrice;
  @FXML
  private TableColumn<Produto, String> tableColumnMinPriceDate;
  @FXML
  private TableColumn<Produto, String> tableColumnMaxPrice;
  @FXML
  private TableColumn<Produto, String> tableColumnMaxPriceDate;
  @FXML
  private TableColumn<Produto, String> tableColumnAvgPrice;
  @FXML
  private TableColumn<Produto, ComboBox<Valor>> tableColumnPriceList;
  @FXML
  private TableView<Produto> tableView;
  
  private IniFile iniFile;

	public void init(Stage stage, VBox vBox) {
		iniFile = IniFile.getNewIniFileInstance(Main.FILE_PRODUTOS);
		tableView.setFixedCellSize(32);
		tableView.setOnMouseClicked(e -> {
			if (tableView.getSelectionModel().getSelectedIndex() < 0)
				return;
			tableView.refresh();
			Produto produto = tableView.getSelectionModel().getSelectedItem();
			if (e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
				editarProduto(produto);
				tableView.refresh();
			}
		});
		buttonAdd.setOnAction(e -> {
			Produto produto = editarProduto(new Produto("Novo produto", new Valor()));
			for (Produto p : tableView.getItems())
				if (p.getNome().trim().toLowerCase().equals(produto.getNome().trim().toLowerCase())) {
					p.addPreco(produto.getPrecoMinimo());
					tableView.refresh();
					return;
				}
			tableView.getItems().add(produto);
		});
    ControllerUtils.addIconToButton(buttonAdd, Icons.NEW_ITEM.getValue(), 15, 15);
    ControllerUtils.addIconToButton(buttonDel, Icons.DELETE.getValue(), 15, 15);
    ControllerUtils.changeHowTableColumnDisplayItems(tableColumnProdutos, Pos.CENTER, e -> e.getNome());
    ControllerUtils.changeHowTableColumnDisplayItems(tableColumnMinPrice, Pos.CENTER, e -> e.getPrecoMinimo().toRealFormat());
    ControllerUtils.changeHowTableColumnDisplayItems(tableColumnMinPriceDate, Pos.CENTER, e -> e.getPrecoMinimo().getDateString());
    ControllerUtils.changeHowTableColumnDisplayItems(tableColumnMaxPrice, Pos.CENTER, e -> e.getPrecoMaximo().toRealFormat());
    ControllerUtils.changeHowTableColumnDisplayItems(tableColumnMaxPriceDate, Pos.CENTER, e -> e.getPrecoMaximo().getDateString());
    ControllerUtils.changeHowTableColumnDisplayItems(tableColumnAvgPrice, Pos.CENTER, e -> e.getPrecoMedio().toRealFormat());
    ControllerUtils.setTableColumnCell(tableColumnPriceList, c -> new ComboBox<Valor>(), (cell, empty) -> {
			cell.setAlignment(Pos.CENTER);
			if (empty)
				cell.setGraphic(null);
			else {
        ComboBox<Valor> cb = new ComboBox<>();
        ControllerUtils.changeHowComboBoxDisplayItems(cb, v -> v.toRealFormat() + "\t\t\t(" + v.getDateString() + ")");
        cb.getItems().addAll(cell.getTableRow().getItem().getPrecos());
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setPromptText("Lista de valores");
        cb.setOnAction(e -> Platform.runLater(() -> cb.setPromptText("Lista de valores")));
        VBox vbox = new VBox(cb);
        vbox.setFillWidth(true);
        VBox.setVgrow(cb, Priority.ALWAYS);
        cell.setGraphic(vbox);
        cell.setAlignment(Pos.CENTER);
			}
		});
    loadFromDisk();
	}
	
	private void loadFromDisk() {
		List<Produto> produtos = new ArrayList<>();
		for (String key : iniFile.getSectionList()) {
			Produto produto = new Produto(iniFile.read(key, "Nome"), Tools.stringToDatedValor(iniFile.read(key, "Valor0")));
			for (int n = 1; iniFile.read(key, "Valor" + n) != null; n++)
				produto.addPreco(Tools.stringToDatedValor(iniFile.read(key, "Valor0")));
			produtos.add(produto);
		}
		tableView.getItems().addAll(produtos);
	}

	private Produto editarProduto(Produto produto) {
  	try {
  		boolean novo = !tableView.getItems().contains(produto);
  		Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AdicionarProdutoView.fxml"));
			VBox vBox = (VBox)loader.load();
			AdicionarProdutoController c = (AdicionarProdutoController)loader.getController();
			c.init(stage, produto, novo, tableView.getItems().stream().map(Produto::getNome).collect(Collectors.toSet()));
			stage.setScene(new Scene(vBox));
			stage.setTitle(novo ? "Adicionar produto" : "Adicionar valor ao produto");
			stage.showAndWait();
			return produto;
  	}
  	catch (Exception ex) {
  		ex.printStackTrace();
  		return null;
  	}
	}
	
	public void saveToDisk() {
		iniFile.clearFile();
		int section = 0;
		for (Produto p : tableView.getItems()) {
			iniFile.write("" + section, "Nome", p.getNome());
			for (int n = 0; n < p.getPrecos().size(); n++) {
				Valor preco = p.getPrecos().get(n);
				iniFile.write("" + section, "Valor" + n, Tools.dateToString(preco.getDate()) + " " + preco.toString());
			}
		}
	}
  
}
