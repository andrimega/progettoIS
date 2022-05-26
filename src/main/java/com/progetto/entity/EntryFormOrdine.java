package com.progetto.entity;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;

/**
 * classe che rappresenta una entry nella tabella del form Ordine
 */
public class EntryFormOrdine {
    private String nomeFarmaco;
    private String principioAttivo;
    private FlowPane strumenti;

    /**
     * Costruttore per istanziare una entry della {@code FormOrdine}
     * @param nomeFarmaco nome del farmaco
     * @param principioAttivo principio attivo del farmaco
     */
    public EntryFormOrdine(String nomeFarmaco, String principioAttivo) {
        this.setNomeFarmaco(nomeFarmaco);
        this.setPrincipioAttivo(principioAttivo);
        Button rimuovi = new Button("RIMUOVI");
        rimuovi.setBackground(Background.fill(Color.rgb(255, 79, 66)));
        rimuovi.setStyle("-fx-text-fill: white");
        Spinner<Integer> spinner = new Spinner<Integer>();
        spinner.setEditable(true);
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,Integer.MAX_VALUE,1);
        spinner.setValueFactory(valueFactory);
        spinner.setMaxWidth(100);
        FlowPane flow = new FlowPane();
        flow.getChildren().addAll(spinner, rimuovi);
        flow.setAlignment(Pos.CENTER);
        flow.setHgap(10); // dae8fc
        this.strumenti = flow;
    }

    private void setNomeFarmaco(String nomeFarmaco) {
        if (nomeFarmaco == null) {
            throw new NullPointerException("Nome del Farmaco = null");
        }
        this.nomeFarmaco = nomeFarmaco;
    }

    private void setPrincipioAttivo(String principioAttivo) {
        if (principioAttivo == null) {
            throw new NullPointerException("Principio attivo del Farmaco = null");
        }
        this.principioAttivo = principioAttivo;
    }

    /**
     * getter per ottenere il nome del farmaco
     * @return {@code String} contenente il nome del farmaco
     */
    public String getNomeFarmaco() {
        return nomeFarmaco;
    }

    /**
     * getter per ottenere il principio attivo del farmaco
     * @return {@code String} contenente il principio attivo del farmaco
     */
    public String getPrincipioAttivo() {
        return principioAttivo;
    }

    /**
     * getter per ottenere il pulsante rimuovi e lo spinner inerenti al farmco
     * @return {@code FlowPane} contenente il pulsante rimuovi e lo spinner
     */
    public FlowPane getStrumenti() {
        return strumenti;
    }
}