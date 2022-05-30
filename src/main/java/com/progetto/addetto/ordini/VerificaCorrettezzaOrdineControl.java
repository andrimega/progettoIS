package com.progetto.addetto.ordini;

import com.progetto.entity.Farmacia;
import com.progetto.entity.Farmaco;
import com.progetto.entity.Lotto;
import com.progetto.entity.Ordine;
import com.progetto.interfacciaDatabase.InterfacciaAddetto;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * classe che si occupa di verificare la correttezza di un ordine e registrarlo nel database dell'Azienda
 */
public class VerificaCorrettezzaOrdineControl {

    private FormOrdine formOrdine;  // riferimento al form ordine
    private Farmacia farmacia;

    private ArrayList<Farmaco> farmaci;  //farmaci richiesti
    private LinkedList<Lotto> lotti;  //lotti presentin nel magazzino azienda

    private ArrayList<Farmaco> farmaciParzialmenteDisponibili; //farmaci con disponibilità parziale
    private ArrayList<Lotto> lottiParzialmenteDisponibili; //lotti relativi a farmaci con disponibilità parziale
    private ArrayList<Farmaco> farmaciDisponibili;  //farmaci disponibili
    private ArrayList<Lotto> lottiDisponibili;  //lotti relativi a farmaci disponibili
    private ArrayList<Farmaco> farmaciNonDisponibili;  //farmaci non disponibili

    /**
     * Costruttore di un oggetto di classe {@code VerificaCorrettezzaOrdine} che prende in input la lista di farmaci per
     * cui bisogna fare il controllo, la farmacia che ha richiesto il controllo e il form ordine da cui si proviene.
     * @param farmaci farmaci da controllare
     * @param farmacia farmacia che ha richiesto il controllo
     * @param formOrdine form da cui si proviene
     */
    public VerificaCorrettezzaOrdineControl(ArrayList<Farmaco> farmaci,Farmacia farmacia, FormOrdine formOrdine){
        this.setFarmaci(farmaci);
        this.setFarmacia(farmacia);
        this.farmaciParzialmenteDisponibili = new ArrayList<>();
        this.lottiParzialmenteDisponibili = new ArrayList<>();
        this.farmaciDisponibili = new ArrayList<>();
        this.lottiDisponibili = new ArrayList<>();
        this.farmaciNonDisponibili = new ArrayList<>();
        this.setFormOrdine(formOrdine);
    }

    private void setFormOrdine(FormOrdine formOrdine){
        if(formOrdine == null){
            throw new NullPointerException("Form Ordine = null");
        }
        this.formOrdine = formOrdine;
    }
    private void setFarmacia(Farmacia farmacia){
        if(farmacia == null){
            throw new NullPointerException("farmacia ? null");
        }
        this.farmacia = farmacia;
    }
    private void setFarmaci(ArrayList<Farmaco> farmaci){
        if(farmaci == null){
            throw new NullPointerException("farmaci == null");
        }
        this.farmaci = farmaci;
    }

    private void setLotti(LinkedList<Lotto> lotti) {
        if(lotti == null){
            throw new NullPointerException("lotti == null");
        }
        this.lotti = lotti;
    }

    private void ottieniLotti(){
        InterfacciaAddetto db = new InterfacciaAddetto();
        this.setLotti(db.getLotti());
    }

    private void verificaQuantita() {
        ArrayList<Lotto> lottiRichiesti = new ArrayList<>(); //lotti che hanno i farmaci richiesti

        for (Farmaco farmaco : this.farmaci) {  //memorizzo i lotti che hanno i farmaci richiesti
            for (Lotto lotto : this.lotti) {
                if (lotto.getNomeFarmaco().compareTo(farmaco.getNome()) == 0 && (lotto.getQuantitaContenuta() - lotto.getQuantitaOrdinata()) > 0) {
                    lottiRichiesti.add(lotto);
                }
            }
        }

        if(lottiRichiesti.size() == 0){  //se non ci sono lotti con i farmaci richiesti con farmaci disponibili
            for(Farmaco farmaco : this.farmaci){
                this.farmaciNonDisponibili.add(farmaco);
            }
        }

        for (Farmaco farmaco : this.farmaci) {  //verifico le quantità
            int numeroFarmaci = 0;
            ArrayList<Lotto> lottiTemp = new ArrayList<>();
            for (int i = 0; i < lottiRichiesti.size(); i++) {
                if (farmaco.getNome().compareTo(lottiRichiesti.get(i).getNomeFarmaco()) == 0) {
                    lottiTemp.add(lottiRichiesti.get(i));
                    numeroFarmaci += lottiRichiesti.get(i).getQuantitaContenuta() - lottiRichiesti.get(i).getQuantitaOrdinata();
                }
                if (numeroFarmaci >= farmaco.getQuantita()) {  //ci sono tutti i farmaci
                    this.farmaciDisponibili.add(farmaco);
                    for (Lotto lottoTemp : lottiTemp) {
                        this.lottiDisponibili.add(lottoTemp);
                    }
                    break;
                }
                if (i == lottiRichiesti.size() - 1 && numeroFarmaci > 0 && numeroFarmaci < farmaco.getQuantita()) {  //non ci sono abbastanza farmaci
                    this.farmaciParzialmenteDisponibili.add(farmaco);
                    for (Lotto lottoTemp : lottiTemp) {
                        this.lottiParzialmenteDisponibili.add(lottoTemp);
                    }
                    break;
                }
                if(i == lottiRichiesti.size() - 1 && numeroFarmaci == 0) {  //non c'è nessun farmaco in magazzino
                    this.farmaciNonDisponibili.add(farmaco);
                }
            }
        }
    }

    private void verificaScadenza(){
        if(this.farmaciNonDisponibili.size() == 0 && this.farmaciParzialmenteDisponibili.size() == 0){  //ci sono abbastanza farmaci per soddisfare l'ordine
            ArrayList<Farmaco> farmaciDisponibiliAvvisoScadenza = new ArrayList<>();
            ArrayList<Lotto> lottiDisponibiliAvvisoScadenza = new ArrayList<>();
            for(Farmaco farmacoDisponibile : this.farmaciDisponibili){
                for(Lotto lottoDisponibile :this.lottiDisponibili){
                    if(lottoDisponibile.getNomeFarmaco().compareTo(farmacoDisponibile.getNome()) == 0){
                        if(Period.between(LocalDate.now(), lottoDisponibile.getDataScadenza()).getMonths() < 2){
                            farmaciDisponibiliAvvisoScadenza.add(farmacoDisponibile);
                            lottiDisponibiliAvvisoScadenza.add(lottoDisponibile);
                        }
                    }
                }
            }
            //se ci sono farmaci che scdranno fra meno di 2 mesi
            if(farmaciDisponibiliAvvisoScadenza.size() != 0) {
                String farmaciPerAvviso = "";
                for (Farmaco farmacoInScadenza : farmaciDisponibiliAvvisoScadenza) {
                    farmaciPerAvviso += (farmacoInScadenza.getNome() + "\t" + farmacoInScadenza.getPrincipioAttivo() + "\n");
                }
                AvvisoScadenza avvisoScadenza = new AvvisoScadenza(farmaciPerAvviso, this.farmacia, this);
                try {
                    avvisoScadenza.start(new Stage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                this.effettuaOrdine();
            }
        }
        else {//non ci sono abbastanza farmaci per soddisfare l'ordine
            SchermataErroreQuantita schermataErroreQuantita = new SchermataErroreQuantita(this);
            try {
                schermataErroreQuantita.start(this.formOrdine.getStage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void effettuaOrdineParziale(){
        ArrayList<Farmaco> farmaciParzialmenteDisponibiliAvvisoScadenza = new ArrayList<>();
        ArrayList<Lotto> lottiParzialmenteDisponibiliAvvisoScadenza = new ArrayList<>();
        for (Farmaco farmacoParzialmenteDisponibile : this.farmaciParzialmenteDisponibili) {
            for (Lotto lottoParzialmenteDisponibile : this.lottiParzialmenteDisponibili) {
                if (lottoParzialmenteDisponibile.getNomeFarmaco().compareTo(farmacoParzialmenteDisponibile.getNome()) == 0) {
                    if (Period.between(LocalDate.now(), lottoParzialmenteDisponibile.getDataScadenza()).getMonths()<2) {
                        farmaciParzialmenteDisponibiliAvvisoScadenza.add(farmacoParzialmenteDisponibile);
                        lottiParzialmenteDisponibiliAvvisoScadenza.add(lottoParzialmenteDisponibile);
                    }
                }
            }
        }

        //se ci sono farmaci che scadranno fra meno di 2 mesi
        if(farmaciParzialmenteDisponibiliAvvisoScadenza.size() != 0) {
            String farmaciPerAvviso = "";
            for (Farmaco farmacoInScadenza : farmaciParzialmenteDisponibiliAvvisoScadenza) {
                farmaciPerAvviso += (farmacoInScadenza.getNome() + "\t" + farmacoInScadenza.getPrincipioAttivo() + "\n");
            }

            AvvisoScadenza avvisoScadenza = new AvvisoScadenza(farmaciPerAvviso, this.farmacia, this);
            try {
                avvisoScadenza.start(this.formOrdine.getStage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            this.effettuaOrdine();
        }
    }

    private void effettuaOrdine(){
        InterfacciaAddetto db = new InterfacciaAddetto();
        if(this.farmaciDisponibili.size() != 0) {
            Ordine ordine = new Ordine(1, 1, this.farmaciDisponibili, 2, 1, LocalDate.now().plusDays(7), this.farmacia.getNome(), this.farmacia.getIndirizzo());
            db.aggiornaLotti(this.lottiDisponibili, this.farmaciDisponibili);
            db.elaboraOrdineNonPeriodico(ordine, this.lottiDisponibili, this.farmaciDisponibili, this.farmacia.getIdFarmacia());
        }
        if(this.farmaciParzialmenteDisponibili.size() != 0){
            Ordine ordine = new Ordine(1,1, this.farmaciParzialmenteDisponibili,2,1,LocalDate.now().plusDays(7), this.farmacia.getNome(), this.farmacia.getIndirizzo());
            db.aggiornaLotti(this.lottiParzialmenteDisponibili, this.farmaciParzialmenteDisponibili);
            db.elaboraOrdineNonPeriodico(ordine, this.lottiParzialmenteDisponibili, this.farmaciParzialmenteDisponibili, this.farmacia.getIdFarmacia());
            for(Farmaco farmaco : this.farmaciParzialmenteDisponibili){
                db.prenotaOrdineNonPeriodico(farmaco, this.farmacia.getIdFarmacia());
            }
        }
        if(this.farmaciNonDisponibili.size() != 0){
            for(Farmaco farmaco : this.farmaciNonDisponibili){
                db.prenotaOrdineNonPeriodico(farmaco, this.farmacia.getIdFarmacia());
            }
        }
        MessaggioConfermaOrdine messaggioConfermaOrdine = new MessaggioConfermaOrdine(this);
        try {
            messaggioConfermaOrdine.start(this.formOrdine.getStage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo utilizzato per comunicare alla control {@code VerificaCorrettezzaOrdineControl} che vi è stato un click
     * sul pulsante {@code confermaOrdine} di un {@code AvvisoScadenza}.
     * Il metodo è stato creato senza modificatore di visibilità affinché possa essere invocato soltanto da classi
     * che si trovano nello stesso package.
     * @param event evento sul pulsante {@code confermaOrdine}
     */
     void clickSuConfermaOrdine(ActionEvent event){
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();  // chiudo l'avviso
        this.effettuaOrdine();
    }

    /**
     * Metodo utilizzato per comunicare alla control {@code VerificaCorrettezzaOrdineControl} che vi è stato un click
     * sul pulsante {@code annullaOrdine} di un {@code AvvisoScadenza}.
     * Il metodo è stato creato senza modificatore di visibilità affinché possa essere invocato soltanto da classi
     * che si trovano nello stesso package.
     * @param event evento sul pulsante {@code annullaOrdine}
     */
    void clickSuAnnullaOrdine(ActionEvent event){
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();  // chiudo l'avviso
    }

    /**
     * Metodo utilizzato per comunicare alla control {@code VerificaCorrettezzaOrdineControl} che vi è stato un click
     * sul pulsante {@code conferma} di una {@code SchermataErroreQuantita}.
     * Il metodo è stato creato senza modificatore di visibilità affinché possa essere invocato soltanto da classi
     * che si trovano nello stesso package.
     * @param event evento sul pulsante {@code conferma}
     */
    void clickSuConferma(ActionEvent event){
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();  // chiudo l'avviso
        this.effettuaOrdineParziale();

    }

    /**
     * Metodo utilizzato per comunicare alla control {@code VerificaCorrettezzaOrdineControl} che vi è stato un click
     * sul pulsante {@code chiudi} di una {@code MessaggioConfermaOrdine}.
     * Il metodo è stato creato senza modificatore di visibilità affinché possa essere invocato soltanto da classi
     * che si trovano nello stesso package.
     * @param event evento sul pulsante {@code chiudi}
     */
    void clickSuChiudi(ActionEvent event) {
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();  // chiudo l'avviso
        this.formOrdine.indietro();
    }

    /**
     * Metodo di avvio di una {@code VerificaCorrettezzaOrdineControl}
     */
    public void start(){
        if(this.farmaci.size() != 0){
            this.ottieniLotti();
            this.verificaQuantita();
            this.verificaScadenza();
        }
    }
}