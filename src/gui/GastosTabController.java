package gui;

import java.io.File;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import application.Main;
import entity.Gasto;
import entity.TabInterface;
import entity.Tools;
import entity.Valor;
import enums.Icons;
import enums.TipoDeListagem;
import gui.util.Alerts;
import gui.util.ControllerUtils;
import gui.util.ListenerHandle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import util.IniFile;
import util.MyFile;
import util.MyString;
import util.Timer;

public class GastosTabController implements TabInterface {

  @FXML
  private VBox vBoxMain;
  @FXML
  private Button buttonGastosAdd;
  @FXML
  private Button buttonGastosDel;
  @FXML
  private DatePicker datePickerInicio;
  @FXML
  private DatePicker datePickerFim;
  @FXML
  private ComboBox<TipoDeListagem> comboBoxTipoDeListagem;
  @FXML
  private CheckBox checkBoxListAll;
  @FXML
  private CheckBox checkBoxClipboardMonitor;
  @FXML
  private TextField textFieldReferencias;
  @FXML
  private ComboBox<Integer> comboBoxPeriodoInicialAno;
  @FXML
  private ComboBox<Integer> comboBoxPeriodoInicialMes;
  @FXML
  private TableColumn<Gasto, Date> tableColumnGastosData;
  @FXML
  private TableColumn<Gasto, Valor> tableColumnGastosEntrada;
  @FXML
  private TableColumn<Gasto, Valor> tableColumnGastosSaida;
  @FXML
  private TableColumn<Gasto, Valor> tableColumnGastosDiferenca;
  @FXML
  private TableColumn<Gasto, String> tableColumnGastosRef;
  @FXML
  private TableView<Gasto> tableViewGastos;
  @FXML
  private Button buttonPreviewMonth;
  @FXML
  private Button buttonNextMonth;
  @FXML
  private HBox hBoxData;
 
  private static GastosTabController thiss;

  private IniFile iniFile;
  private Stage stage;
  private List<Gasto> gastos;
  private ListenerHandle<LocalDate> listenerDaterPickerInicio;
  private ListenerHandle<LocalDate> listenerDaterPickerFim;
	private Set<String> autoCompletion;
	private boolean blockRefresh;
  
	public void init(Stage stage, VBox vBox) {
		thiss = this;
		this.stage = stage;
		gastos = new ArrayList<>();
		autoCompletion = new HashSet<>();
		blockRefresh = true;
		initControllers();
		loadFromDisk();
		listenerDaterPickerInicio.attach();
		listenerDaterPickerFim.attach();
		blockRefresh = false;
		refreshTableView();
		Platform.runLater(() -> {
			stage.getScene().setOnKeyPressed(e -> {
				if (e.getCode() == KeyCode.ADD)
					addGasto();
				else if (e.getCode() == KeyCode.DELETE)
					deleteSelecteds();
			});
		});
	}
	
	private void initControllers() {
		ControllerUtils.setAutoCompletionOnTextField(textFieldReferencias, 10, autoCompletion);
		textFieldReferencias.textProperty().addListener((o, oldV, newV) -> refreshTableView());
		checkBoxListAll.selectedProperty().addListener((o, oldV, newV) -> {
			hBoxData.setDisable(newV);
			refreshTableView();
		});
		checkBoxClipboardMonitor.selectedProperty().addListener((o, oldV, newV) -> Main.checkClipboard = newV);
		listenerDaterPickerInicio = new ListenerHandle<>(datePickerInicio.valueProperty(), (o, oldV, newV) -> {
    	if (datePickerInicio.getValue().isAfter(datePickerFim.getValue()))
	    	datePickerFim.setValue(datePickerInicio.getValue().withDayOfMonth(datePickerInicio.getValue().lengthOfMonth()));
    	refreshTableView();
    });
		listenerDaterPickerFim = new ListenerHandle<>(datePickerFim.valueProperty(), (o, oldV, newV) -> {
    	if (datePickerFim.getValue().isBefore(datePickerInicio.getValue()))
    		datePickerInicio.setValue(datePickerFim.getValue().withDayOfMonth(1));
    	refreshTableView();
    });
		comboBoxTipoDeListagem.getItems().setAll(TipoDeListagem.values());
		comboBoxTipoDeListagem.valueProperty().addListener((o, oldV, newV) -> refreshTableView());
    ControllerUtils.addIconToButton(buttonGastosAdd, Icons.NEW_ITEM.getValue(), 15, 15);
    ControllerUtils.addIconToButton(buttonGastosDel, Icons.DELETE.getValue(), 15, 15);
    ControllerUtils.addIconToButton(buttonPreviewMonth, Icons.MOVE_MAX_LEFT.getValue(), 15, 15);
    ControllerUtils.addIconToButton(buttonNextMonth, Icons.MOVE_MAX_RIGHT.getValue(), 15, 15);
    buttonPreviewMonth.setOnAction(e -> {
  		listenerDaterPickerFim.detach();
  		listenerDaterPickerFim.detach();
  		datePickerInicio.setValue(datePickerInicio.getValue().minusMonths(1).withDayOfMonth(1));
  		listenerDaterPickerFim.attach();
  		listenerDaterPickerFim.attach();
  		datePickerFim.setValue(datePickerInicio.getValue().withDayOfMonth(datePickerInicio.getValue().lengthOfMonth()));
    });
    buttonNextMonth.setOnAction(e -> {
  		listenerDaterPickerFim.detach();
  		listenerDaterPickerFim.detach();
  		datePickerInicio.setValue(datePickerInicio.getValue().plusMonths(1).withDayOfMonth(1));
  		listenerDaterPickerFim.attach();
  		listenerDaterPickerFim.attach();
  		datePickerFim.setValue(datePickerInicio.getValue().withDayOfMonth(datePickerInicio.getValue().lengthOfMonth()));
    });
    buttonGastosAdd.setOnAction(e -> addGasto());
    buttonGastosDel.setOnAction(e -> deleteSelecteds());
    tableViewGastos.setFixedCellSize(20);
    tableViewGastos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tableViewGastos.setOnMouseClicked(e -> {
			if (tableViewGastos.getSelectionModel().getSelectedIndex() < 0)
				return;
			tableViewGastos.refresh();
			Gasto gasto = tableViewGastos.getSelectionModel().getSelectedItem();
			if (!gasto.isBlank() && !gasto.isTotal() && e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
				editarGasto(gasto);
				refreshTableView();
			}
		});
  	ControllerUtils.setTableColumnCell(tableColumnGastosData, Gasto::getDate, (cell, empty) -> {
  		if (isNotBlank(cell, empty, true))
				cell.setGraphic(new Text(Tools.dateToString(cell.getItem())));
  	});
  	ControllerUtils.setTableColumnCell(tableColumnGastosEntrada, Gasto::getEntrada, (cell, empty) -> {
  		Gasto g = cell.getTableRow().getItem();
  		if (isNotBlank(cell, empty || (!g.isTotal() && !g.getSaida().isZero()), false)) {
				Text text = new Text(cell.getItem().toRealFormat());
				text.setFill(Color.valueOf("006600"));
				cell.setGraphic(text);
			}
  	});
  	ControllerUtils.setTableColumnCell(tableColumnGastosSaida, Gasto::getSaida, (cell, empty) -> {
  		Gasto g = cell.getTableRow().getItem();
  		if (isNotBlank(cell, empty || (!g.isTotal() && !g.getEntrada().isZero()), false)) {
				Text text = new Text(cell.getItem().toRealFormat());
				text.setFill(Color.valueOf("880000"));
				cell.setGraphic(text);
			}
  	});
  	ControllerUtils.setTableColumnCell(tableColumnGastosRef, Gasto::getReferencia, (cell, empty) -> {
  		if (isNotBlank(cell, empty, false))
				cell.setGraphic(new Text(cell.getTableRow().getItem().getReferencia()));
  	});
  	ControllerUtils.setTableColumnCell(tableColumnGastosDiferenca, Gasto::getDiferenca, (cell, empty) -> {
  		BiConsumer<TableCell<Gasto, Valor>, Boolean> applyColor = (cell2, empty2) -> {
    		cell.setAlignment(Pos.CENTER);
    		TableRow<Gasto> row = cell.getTableRow();
    		Gasto gasto = row.getItem();
  			boolean isSel = row.isSelected();
  			boolean isHover = row.isHover();
  			if (empty || gasto == null || !gasto.isTotal()) {
  				cell.setGraphic(null);
  				row.setBackground(Background.fill(Color.valueOf(empty || gasto == null || cell.getIndex() % 2 == 0 ? (isHover ? "FFFFAA" : isSel ? "DDDDFF" : "FFFFFF") : (isHover ? "DDDD88" : isSel ? "BBBBDD" : "DDDDDD"))));
  			}
  			else {
  				row.setBackground(Background.fill(Color.valueOf(gasto.getDiferenca().isPositivo() ? "AACCFF" : "FFCCAA")));
  				Text text = new Text(gasto.getDiferenca().toRealFormat());
  				text.setFill(Color.valueOf(gasto.getDiferenca().isPositivo() ? "006600" : "880000"));
  				cell.setGraphic(text);
  			}
  		};
			applyColor.accept(cell, empty);
			cell.getTableRow().hoverProperty().addListener((o, wasHovered, isHovered) -> applyColor.accept(cell, empty));
  	});
	}
	
	private <B> boolean isNotBlank(TableCell<Gasto, B> cell, Boolean empty, boolean isDateColumn) {
		cell.setAlignment(Pos.CENTER);
		Gasto g = cell.getTableRow().getItem();
		if (empty || g.isBlank() || (g.isTotal() && isDateColumn)) {
			cell.setGraphic(null);
			return false;
		}
		return true;
	}

	private void addGasto() {
		Object[] o = editarGasto(new Gasto(Date.valueOf(datePickerInicio.getValue())));
		int parcelas = (int)o[1];
		Gasto gasto = (Gasto)o[0];
		if (gasto.getReferencia().isBlank() && gasto.getEntrada().isZero() && gasto.getSaida().isZero())
			return;
		Date date = gasto.getDate();
		List<Gasto> novosGastos = new ArrayList<>();
		for (int n = 0; n < parcelas; n++) {
			Gasto g = new Gasto(gasto);
			if (parcelas > 1)
				g.setReferencia(g.getReferencia() + " (Parcela " + (n + 1) + ")");
			g.setDate(date);
			date = Tools.incMonth(date);
			novosGastos.add(g);
		}
		if (gasto.getDate().toLocalDate().isBefore(datePickerInicio.getValue()))
			datePickerInicio.setValue(gasto.getDate().toLocalDate());
		if (parcelas > 1 && date.toLocalDate().isAfter(datePickerFim.getValue()))
				datePickerFim.setValue(date.toLocalDate());
		gastos.addAll(novosGastos);
		refreshTableView();
	}

	private void deleteSelecteds() {
		if (tableViewGastos.getSelectionModel().getSelectedItems().isEmpty())
			return;
		if (Alerts.confirmation("Confirmação de exclusão", "Deseja mesmo excluir o(s) gasto(s) selecionado(s)?")) {
			for (Gasto gasto : new ArrayList<>(tableViewGastos.getSelectionModel().getSelectedItems()))
				gastos.remove(gasto);
			refreshTableView();
		}
	}

	private Object[] editarGasto(Gasto gasto) {
  	try {
  		Stage stage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/AdicionarGastoView.fxml"));
			VBox vBox = (VBox)loader.load();
			AdicionarGastoController c = (AdicionarGastoController)loader.getController();
			c.init(stage);
			c.load(gasto, autoCompletion);
			stage.setScene(new Scene(vBox));
			stage.setTitle("Adicionar gasto");
			stage.showAndWait();
			return new Object[] {gasto, c.getParcelas()};
  	}
  	catch (Exception ex) {
  		ex.printStackTrace();
  		return null;
  	}
	}

	private void refreshTableView() {
		if (blockRefresh)
			return;
		tableViewGastos.getItems().clear();
		Gasto gastoMes = null;
		Gasto gastoAno = null;
		Gasto gastoTotal = new Gasto("TOTAL");
		Gasto[] previewGasto = { null };
		List<Gasto> gastos2 = new ArrayList<>();
		Predicate<Gasto> passFilter = gasto -> {
			if (!checkBoxListAll.isSelected() &&
				 (gasto.getDate().toLocalDate().isBefore(datePickerInicio.getValue()) ||
				  gasto.getDate().toLocalDate().isAfter(datePickerFim.getValue())))
						return false;
			if (textFieldReferencias.getText().trim().isBlank())
				return true;
			String s = MyString.removeAccents(gasto.getReferencia().trim().toLowerCase());
			String s2 = MyString.removeAccents(textFieldReferencias.getText().trim().toLowerCase());
			return s2.isBlank() || s.contains(s2);
		};
		gastos.sort((g1, g2) -> g1.getDate().compareTo(g2.getDate()));
		autoCompletion.clear();
		BiConsumer<Gasto, TipoDeListagem> encerraMes = (gasto, tipoDeListagem) -> {
			gasto.setDate(new Date(previewGasto[0].getDate().getTime() + 1000));
			gasto.setReferencia(Tools.dateToString(gasto.getDate(), tipoDeListagem == TipoDeListagem.RESUMO_MES ? "'Total do mês de 'MMMM" : "'Total do ano de 'yyyy"));
			gastos2.add(gasto.asTotal());
		};
		for (Gasto gasto : gastos) {
			autoCompletion.add(gasto.getReferencia());
			if (passFilter.test(gasto)) {
				if (gastoMes == null) {
					gastoMes = new Gasto(gasto.getDate());
					gastoAno = new Gasto(gasto.getDate());
				}
				if (!Tools.isSameMonth(gastoMes.getDate(), gasto.getDate())) {
					if (getTipoListagem() != TipoDeListagem.RESUMO_ANO)
						encerraMes.accept(gastoMes, TipoDeListagem.RESUMO_MES);
					gastoMes = new Gasto(gasto.getDate());
					if (!Tools.isSameYear(gastoAno.getDate(), gasto.getDate())) {
						encerraMes.accept(gastoAno, TipoDeListagem.RESUMO_ANO);
						gastoAno = new Gasto(gasto.getDate());
					}
					gastos2.add(new Gasto().asBlank());
				}
				if (getTipoListagem() == TipoDeListagem.TUDO)
					gastos2.add(gasto);
				gastoMes.getEntrada().incValor(gasto.getEntrada());
				gastoMes.getSaida().incValor(gasto.getSaida());
				gastoAno.getEntrada().incValor(gasto.getEntrada());
				gastoAno.getSaida().incValor(gasto.getSaida());
				gastoTotal.getEntrada().incValor(gasto.getEntrada());
				gastoTotal.getSaida().incValor(gasto.getSaida());
				previewGasto[0] = new Gasto(gasto);
			}
		}
		if (!gastos2.isEmpty()) {
			boolean addEmpty = false;
			if (!gastoMes.getDiferenca().isZero()&& getTipoListagem() != TipoDeListagem.RESUMO_ANO) {
				encerraMes.accept(gastoMes, TipoDeListagem.RESUMO_MES);
				addEmpty = true;
			}
			if (!gastoAno.getDiferenca().isZero()) {
				encerraMes.accept(gastoAno, TipoDeListagem.RESUMO_ANO);
				addEmpty = true;
			}
			if (addEmpty)
				gastos2.add(new Gasto().asBlank());
			gastos2.add(gastoTotal.asTotal());
			tableViewGastos.getItems().setAll(gastos2);
		}
		Timer.createTimer("save", Duration.ofSeconds(10), 0, () -> saveToDisk());
	}
	
	private TipoDeListagem getTipoListagem() {
		return comboBoxTipoDeListagem.getSelectionModel().getSelectedItem();
	}

	public static void addGasto(Gasto gasto) {
		thiss.gastos.add(gasto);
		Platform.runLater(() -> thiss.refreshTableView());
	}
	
	private void loadFromDisk() {
		List<String> list = MyFile.readAllLinesFromFile(Main.FILE_GASTOS);
		if (list != null)
			for (String s : list) {
				Gasto g = Tools.stringToGasto(s);
				if (g != null)
					gastos.add(g);
			}
		iniFile = IniFile.getNewIniFileInstance("configs.ini");
		checkBoxClipboardMonitor.setSelected(iniFile.readAsBoolean("CONFIG", "checkBoxClipboardMonitor", false));
		checkBoxListAll.setSelected(iniFile.readAsBoolean("CONFIG", "checkBoxListAll", false));
		Main.checkClipboard = checkBoxClipboardMonitor.isSelected(); 
		long inicio = LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
		LocalDate dateInicio = Tools.longToLocalDate(iniFile.readAsLong("CONFIG", "datePickerInicio", inicio));
    datePickerInicio.setValue(dateInicio);
		long fim = YearMonth.now().atEndOfMonth().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
		LocalDate dateFim = Tools.longToLocalDate(iniFile.readAsLong("CONFIG", "datePickerFim", fim));
    datePickerFim.setValue(dateFim);
    comboBoxTipoDeListagem.getSelectionModel().select(iniFile.readAsEnum("CONFIG", "comboBoxTipoDeListagem", TipoDeListagem.class, TipoDeListagem.TUDO));
	}
	
	public void saveToDisk() {
		List<String> list = new ArrayList<>();
		for (Gasto gasto : gastos)
			list.add(Tools.dateToString(gasto.getDate()) + " " + gasto.getEntrada().toString() + " " + gasto.getSaida().toString() + " " + gasto.getReferencia());
		File file = new File(Main.FILE_GASTOS);
		if (file.exists())
			file.delete();
		MyFile.writeAllLinesOnFile(list, Main.FILE_GASTOS);
		long inicio = datePickerInicio.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
		long fim = datePickerFim.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
		iniFile.write("CONFIG", "checkBoxClipboardMonitor", checkBoxClipboardMonitor.isSelected());
		iniFile.write("CONFIG", "checkBoxListAll", checkBoxListAll.isSelected());
		iniFile.write("CONFIG", "datePickerInicio", inicio);
		iniFile.write("CONFIG", "datePickerFim", fim);
		iniFile.write("CONFIG", "comboBoxTipoDeListagem", getTipoListagem().name());
	}
	
	public static List<Gasto> getTodosOsGastos() {
		return thiss.gastos;
	}

	public static List<Gasto> getGastosFiltrados() {
		return thiss.tableViewGastos.getItems();
	}

}
