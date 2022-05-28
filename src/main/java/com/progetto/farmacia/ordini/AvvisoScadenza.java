package com.progetto.farmacia.ordini;

import com.progetto.entity.Farmacia;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * rapprenta l'avviso relativo ai farmaci che scadono tra meno di 2 mesi al momento della creazione di un ordine
 */
public class AvvisoScadenza extends Application implements Initializable {

    private static Farmacia farmacia;
    private static String farmaciInScadenza;
    @FXML
    private Text usernameLabel;

    @FXML
    private TextArea farmaciInScadenzaText;

    /**
     * costruire un oggetto {@code AvvisoScadenza}
     */
    public AvvisoScadenza(){
        super();
    }

    /**
     * costruisce un oggetto {@code AvvisoScadenza} dati in input i farmaci in scadenza e la farmacia che ha effettuato l'ordine
     * @param farmaciInScadenza farmaci in scadenza
     * @param farmacia farmaica che ha effettuato l'ordine
     */
    public AvvisoScadenza(String farmaciInScadenza, Farmacia farmacia){
        this.setFarmaciInScadenza(farmaciInScadenza);
        this.setFarmacia(farmacia);
    }

    @FXML
    private void confermaOrdine(ActionEvent event){
        VerificaCorrettezzaOrdineControl.clickSuConfermaOrdine(event);
    }

    @FXML
    private void annullaOrdine(ActionEvent event){
        VerificaCorrettezzaOrdineControl.clickSuAnnullaOrdine(event);
    }

    private void setFarmaciInScadenza(String farmaciInScadenza){
        if(farmaciInScadenza == null){
            throw new NullPointerException("farmaci in scadenza = null");
        }
        AvvisoScadenza.farmaciInScadenza = farmaciInScadenza;

    }

    private void setFarmacia(Farmacia farmacia){
        if(farmacia == null){
            throw new NullPointerException("farmacia = null");
        }
        AvvisoScadenza.farmacia = farmacia;
    }

    /**
     * Metodo utilizzato per visualizzare un oggetto di tipo {@code AvvisoScadenza} a schermo
     * @param stage stage del form ordine
     * @throws IOException se il caricamento del file {@code fxml} non è andato a buon fine
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("avvisoScadenza.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);

        double stageWidth = 600;
        double stageHeight = 600;

        //centra la schermata
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(((screenBounds.getWidth() - stageWidth) / 2 + stageWidth));
        stage.setY((screenBounds.getHeight() - stageHeight) / 2);

        stage.setTitle("Avviso Scadenza");
        stage.setScene(scene);
        stage.setMinWidth(stageWidth);
        stage.setMinHeight(stageHeight);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    /**
     * Metodo utilizzato per personalizzare un oggetto di tipo {@code AvvisScadenza} in base ai farmaci in scadenza
     *  e alla farmacia che ha effettuato l'ordine
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.farmaciInScadenzaText.setText(AvvisoScadenza.farmaciInScadenza);
        this.usernameLabel.setText(AvvisoScadenza.farmacia.getNome());
    }
}
