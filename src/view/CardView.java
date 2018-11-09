package view;

import controller.CardController;
import controller.MapController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Model;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class CardView implements Observer{

    private static CardView instance;

    private AnchorPane mainCardPane;
    private CardController cardController;
    private Stage cardStage;

    private CardView() {
        FXMLLoader menuFxmlLoader = new FXMLLoader(getClass().getResource("Card.fxml"));
        try {
            mainCardPane = menuFxmlLoader.load();
        } catch (IOException exception) {
            System.out.println("CardView.ctor(): " + exception.getMessage());
        }
        cardController = menuFxmlLoader.getController();
        cardStage = new Stage();
        cardStage.setTitle("Card Exchange");
        cardStage.setScene(new Scene(mainCardPane,500,300));
        cardStage.setResizable(false);
        cardStage.sizeToScene();
    }

    static CardView getInstance() {
        if (null == instance) instance = new CardView();
        return instance;
    }

    CardController getCardController() { return cardController; }

    // TODO: get some controllers
    void init (Model model, CardView card, MapController mapController) {


    }

    public void show() { cardStage.show(); }

    public void hide() { cardStage.hide(); }

    public void close() { cardStage.close(); }

    @Override
    public void update(Observable obs, Object obj) {
        cardController.autoInitializeController();

    }
    }
