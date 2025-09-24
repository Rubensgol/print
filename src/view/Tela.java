package view;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.update4j.Configuration;

import controler.interfaces.IAtualiza;
import controler.interfaces.IImprimir;
import controler.interfaces.ITrataArquivo;
import controler.business.TrataArquivo;
import controler.business.atualizar.update4j.AtualizaUpdate4j;
import controler.business.comunicaTiny.Comunica;
import controler.business.imprimir.ImprimirDesktop;
import model.EnumRetorno;
import model.LinkEtiqueta;


public class Tela
{
	private Comunica comunica;
	private IImprimir imprimir;
	private List<LinkEtiqueta> links;
	private final AtomicBoolean buscando = new AtomicBoolean(false);
	private ITrataArquivo aTrataArquivo;
	private IAtualiza atualiza;

	// UI components
	private TextField tokenField;
	private Button startButton;
	private Button stopButton;
	private Button atualizaButton;
	private ProgressBar progressBar;

	public Tela(List<Integer> lidas, Configuration config)
	{
		// initialize business objects now
		comunica = new Comunica(lidas);
		imprimir = new ImprimirDesktop();
		aTrataArquivo = new TrataArquivo();

		if (config != null)
			atualiza = new AtualizaUpdate4j(config);
		else
			atualiza = null;

		// Ensure JavaFX toolkit started, then build UI on FX thread
		ensureFxInitialized();
		Platform.runLater(() -> createAndShowStage());
	}

	private void ensureFxInitialized() {
		try {
			// Platform.startup can only be called once; if already started it throws
			Platform.startup(() -> {
				// no-op - toolkit initialized
			});
		} catch (IllegalStateException e) {
			// already started
		}
	}

	private void createAndShowStage() 
	{
		Stage stage = new Stage(StageStyle.DECORATED);
		stage.setTitle("Print Etiqueta - App");

		Label tokenLabel = new Label("Digite seu Token de Acesso:");
		tokenLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

		tokenField = new TextField();
		tokenField.setPromptText("Seu token aqui");
		tokenField.setMaxWidth(300);

		startButton = new Button("Iniciar Busca");
		startButton.setStyle("-fx-background-color: #228B22; -fx-text-fill: white;");
		stopButton = new Button("Parar Busca");
		stopButton.setStyle("-fx-background-color: #DC143C; -fx-text-fill: white;");
		stopButton.setDisable(true);

		atualizaButton = new Button("Atualizar");

		progressBar = new ProgressBar(0);
		progressBar.setPrefWidth(300);
		progressBar.setProgress(0);

		HBox buttonBox = new HBox(10, startButton, stopButton, atualizaButton);

		VBox root = new VBox(12, tokenLabel, tokenField, buttonBox, progressBar);
		root.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #F4F4F4;");

		// Actions
		startButton.setOnAction(evt -> {
			String token = tokenField.getText();
			if (token == null || token.trim().isEmpty()) {
				showWarning("Token Vazio", "Por favor, insira um token antes de iniciar.");
				return;
			}
			startSearch(token.trim());
		});

		stopButton.setOnAction(evt -> stopSearch());

		atualizaButton.setOnAction(evt -> {
			try {
				if (atualiza == null) {
					showWarning("Atualização indisponível", "Configuração de atualização não carregada; não é possível verificar atualizações.");
					return;
				}
				if (atualiza.temAtualizacao()) {
					atualiza.atualiza();
				} else {
					showInfo("Atualização", "Aplicativo já está atualizado.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				showWarning("Erro", "Erro ao verificar/realizar atualização: " + e.getMessage());
			}
		});

		Scene scene = new Scene(root, 420, 200);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	private void startSearch(String token)
	{
		buscando.set(true);
		updateButtons();
		progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

		Thread worker = new Thread(() -> {
			try {
				while (buscando.get()) {
					try {
						EnumRetorno retorno = comunica.verificaConexao(token);

						if (retorno == EnumRetorno.SUCESSO || retorno == EnumRetorno.SUCESSO_EM_BRANCO) {
							links = comunica.getEtiquetas(token);
							if (links != null) {
								for (LinkEtiqueta link : links) {
									try {
										imprimir.imprimir(link.getLink());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}

							aTrataArquivo.salvaTxt(comunica.getSeparacoesLidas());
						} else {
							Platform.runLater(() -> showWarning("Erro de Token", retorno.getDescricao()));
							buscando.set(false);
							Platform.runLater(this::updateButtons);
							break;
						}

						Thread.sleep(5000);
					} catch (Exception e) {
						e.printStackTrace();
						Thread.sleep(5000);
					}
				}
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			} finally {
				buscando.set(false);
				Platform.runLater(() -> {
					progressBar.setProgress(0);
					updateButtons();
				});
			}
		}, "Tela-Worker");
		worker.setDaemon(true);
		worker.start();
	}

	private void stopSearch() 
	{
		buscando.set(false);
		aTrataArquivo.salvaTxt(comunica.getSeparacoesLidas());
		updateButtons();
		Platform.runLater(() -> progressBar.setProgress(0));
	}

	private void updateButtons() {
		boolean running = buscando.get();
		if (startButton != null && stopButton != null && tokenField != null) {
			startButton.setDisable(running);
			stopButton.setDisable(!running);
			tokenField.setDisable(running);
		}
	}

	private void showWarning(String title, String msg) {
		Alert a = new Alert(Alert.AlertType.WARNING);
		a.setTitle(title);
		a.setHeaderText(null);
		a.setContentText(msg);
		a.showAndWait();
	}

	private void showInfo(String title, String msg) {
		Alert a = new Alert(Alert.AlertType.INFORMATION);
		a.setTitle(title);
		a.setHeaderText(null);
		a.setContentText(msg);
		a.showAndWait();
	}
}