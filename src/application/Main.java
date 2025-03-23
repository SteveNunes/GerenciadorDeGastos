package application;

import java.time.Duration;

import entity.Gasto;
import entity.Tools;
import gui.GastosTabController;
import gui.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.IniFile;
import util.Misc;
import util.Timer;

public class Main extends Application {
	
	public static final String FILE_GASTOS = ".\\gastos.txt";
	public static final String FILE_PRODUTOS = ".\\produtos.ini";
	public static boolean checkClipboard = false;

	@Override
	public void start(Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainWindowView.fxml"));
			VBox vBox = loader.load();
			((MainWindowController) loader.getController()).init(stage);
			Scene scene = new Scene(vBox);
			stage.setScene(scene);
			stage.setTitle("Gerenciador de gastos");
			stage.setOnCloseRequest(e -> {
				Timer.close();
				MainWindowController.gastosController.saveToDisk();
				MainWindowController.precosProdutosController.saveToDisk();
				IniFile.close();
			});
			stage.setResizable(false);
			stage.show();
			Timer.createTimer("checkClipboard", Duration.ofMillis(200), 0, () -> checkClipboard());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String lastClipboard = "";
	private void checkClipboard() {
		if (checkClipboard) {
			String s = Misc.getTextFromClipboard();
			if (s != null && (lastClipboard == null ||  !lastClipboard.equals(s))) {
				lastClipboard = s;
				Gasto gasto = Tools.stringToGasto(s);
				if (gasto != null)
					GastosTabController.addGasto(gasto);
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
