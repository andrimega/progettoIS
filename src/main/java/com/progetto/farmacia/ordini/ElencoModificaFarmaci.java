package com.progetto.farmacia.ordini;

import com.progetto.entity.EntryFormOrdine;
import com.progetto.entity.EntryListaOrdini;
import com.progetto.entity.Farmacia;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * classe che mostra l'elenco dei farmaci ordinabili dalla farmacia
 */
public class ElencoModificaFarmaci extends Application implements Initializable {

    @FXML
    private Text usernameLabel;

    private static ArrayList<EntryFormOrdine> farmaci;

    private static Farmacia farmacia;

    private static ModificaOrdineControl control;

    private EntryListaOrdini entry;

    private static TableView<EntryFormOrdine> ref;  // riferimento per poter apportare modifiche dall'esterno
    @FXML
    private TableView<EntryFormOrdine> lista;

    @FXML
    private TableColumn<EntryFormOrdine, String> nomeFarmaco;

    @FXML
    private TableColumn<EntryFormOrdine, String> principioAttivo;

    @FXML
    private TableColumn<EntryFormOrdine, FlowPane> strumenti;

    public ElencoModificaFarmaci(){
        super();
    }

    public ElencoModificaFarmaci(ModificaOrdineControl control, Farmacia farmacia, ArrayList<EntryFormOrdine> farmaci) {
        super();
        this.setFarmacia(farmacia);
        this.setEntryElencoFarmaci(farmaci);
        this.setControl(control);
    }

    private void setEntryElencoFarmaci(ArrayList<EntryFormOrdine> entryElencoFarmaci) {
        if(entryElencoFarmaci == null){
            throw new NullPointerException("entryElencoFarmaci = null");
        }
        ElencoModificaFarmaci.farmaci = entryElencoFarmaci;
    }

    private void setFarmacia(Farmacia farmacia){
        if(farmacia == null){
            throw new NullPointerException("farmacia = null");
        }
        ElencoModificaFarmaci.farmacia = farmacia;
    }

    public void setControl(ModificaOrdineControl control) {
        if(control == null){
            throw new NullPointerException("control = null");
        }
        ElencoModificaFarmaci.control = control;
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("elencoModificaFarmaci.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 400);

        double stageWidth = 800;
        double stageHeight = 400;

        Stage newStage = new Stage();

        //centra la schermata
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        newStage.setX((screenBounds.getWidth() - stageWidth) / 2);
        newStage.setY((screenBounds.getHeight() - stageHeight) / 2);

        newStage.setTitle("Elenco farmaci");
        newStage.setScene(scene);
        newStage.setHeight(stageHeight);
        newStage.setWidth(stageWidth);
        newStage.setMinWidth(stageWidth);
        newStage.setMinHeight(stageHeight);
        newStage.initOwner(stage); //imposto come proprietario dello stage dell'errore lo stage della schermata di login passato in input
        newStage.initModality(Modality.WINDOW_MODAL);  //blocco il focus sulla schermata delle'errore
        newStage.show();
    }

    /**
     * Permette di tornare indietro e visualizzare il {@code FormOrdine}
     * @param event evento associato alla pressione del {@code button} indietro
     */
    @FXML
    private void conferma(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();  // prendo lo stage corrente
        ElencoModificaFarmaci.control.clickSuConferma(stage);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.usernameLabel.setText(ElencoModificaFarmaci.farmacia.getNome());
        this.nomeFarmaco.setCellValueFactory(new PropertyValueFactory<>("nomeFarmaco"));
        this.principioAttivo.setCellValueFactory(new PropertyValueFactory<>("principioAttivo"));
        this.strumenti.setCellValueFactory(new PropertyValueFactory<>("strumenti"));
        for(EntryFormOrdine entry : ElencoModificaFarmaci.farmaci) {
            this.lista.getItems().add(entry);
        }
        ElencoModificaFarmaci.ref = this.lista;
    }

    public void aggiungiFarmaco(EntryFormOrdine entry) {
        if(entry == null) {
            throw new NullPointerException("Entry in aggiungi farmaco = null");
        }
        if(!ElencoModificaFarmaci.farmaci.contains(entry)) {
            ElencoModificaFarmaci.farmaci.add(entry);
            ElencoModificaFarmaci.ref.getItems().add(entry);
        }
    }
    public void rimuoviFarmaco(EntryFormOrdine entry) {
        if(entry == null) {
            throw new NullPointerException("Entry in rimuovi farmaco di ElencoFarmaci = null");
        }
        ElencoModificaFarmaci.farmaci.remove(entry);
        ElencoModificaFarmaci.ref.getItems().remove(entry);
    }
}