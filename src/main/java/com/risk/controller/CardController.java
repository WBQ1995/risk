package com.risk.controller;

import com.risk.model.Card;
import com.risk.model.CardModel;
import com.risk.model.Model;
import com.risk.model.Player;
import com.risk.view.CardView;

import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Handle event when user interact with CardView
 */
public class CardController {

    @FXML private Button trade;
    @FXML private Button cancelCardView;
    @FXML private Label currentPlayerName;
    @FXML private Label textToShow;
    @FXML private VBox cardVbox;

    private List<Card> playerCards;
    private CheckBox[] cbs;
    private Model model;
    private CardView card;
    private MapController mapController;
    private Player currentPlayer;


    /**
     * Get corresponding reference of Model, CardView and MapController
     * @param model is the reference of Model
     * @param card is the reference of CardView
     * @param mapController is the reference of the MapController
     */
    public void init(Model model, CardView card, MapController mapController) {
        this.model = model;
        this.card = card;
        this.mapController = mapController;
    }

    /**
     * Handle cancelling CardView event
     * @param event the Action event
     */
    @FXML
    private void cancelCardView(ActionEvent event) {
        model.quitCards();
        if(CardModel.getInstance().readyToQuit()) {
            Stage stage = (Stage) cancelCardView.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * if the exchange operation is valid
     * @param event the Action event
     */
    @FXML
    private void checkTrade(ActionEvent event) {
        trade.setDisable(false);
        textToShow.setText(null);
        currentPlayer = CardModel.getInstance().getCurrentPlayer();
        List<Card> selectedCards = CardModel.getInstance().retrieveSelectedCardsFromCheckbox
                (currentPlayer.getPlayerCardList(),cbs);
        if (selectedCards.size() == 3) {
        model.trade((ArrayList<Card>) selectedCards);
        }
    }

    /**
     * initiate CardView
     */
    public void autoInitializeController() {
        cardVbox.getChildren().clear();
        currentPlayer = CardModel.getInstance().getCurrentPlayer();
        currentPlayerName.setText("All Cards Of Player : " + currentPlayer.getName());
        textToShow.setStyle("-fx-text-fill: red");
        if(CardModel.getInstance().finishExchange()){
            textToShow.setStyle("-fx-text-fill: green");
        }
        textToShow.setText(CardModel.getInstance().getInvalidInfo());
        playerCards = currentPlayer.getPlayerCardList();

        if (playerCards.size() < 3) {
            trade.setDisable(true);
        } else {
            trade.setDisable(false);
        }
        loadAllCards();
    }

    /**
     * show all the cards on the CardView
     */
    public void loadAllCards() {
        int numberOfCards = playerCards.size();
        cbs = new CheckBox[numberOfCards];
        for (int i = 0; i < numberOfCards; i++) {
            cbs[i] = new CheckBox(
                    playerCards.get(i).getCardType().toString());
        }
        cardVbox.getChildren().addAll(cbs);
    }
}