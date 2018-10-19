package view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.Country;


/**
 * Handle event when user interact with the map, pass it to View
 */
public class MapController {

    @FXML private Button skipReinforcementPhaseButton;
    @FXML private TextField numArmiesMoveTextField;
    @FXML private AnchorPane currentPlayerPane;
    @FXML private Label currentPlayerLabel;
    @FXML private Label numArmiesMoveLabel;
    @FXML private Label armiesInHandLabel;
    @FXML private Button nextPhaseButton;
    @FXML private Label invalidMoveLabel;
    @FXML private Label countryALabel;
    @FXML private Label countryBLabel;
    @FXML private Label countryAName;
    @FXML private Label countryBName;
    @FXML private AnchorPane mapPane;
    @FXML private Label phaseLabel;

    private View view;


    /**
     * Get View reference, add event listener
     * @param view the View reverence
     */
    public void initialize(View view) {
        skipReinforcementPhaseButton.setVisible(false);
        numArmiesMoveTextField.setVisible(false);
        numArmiesMoveLabel.setVisible(false);
        invalidMoveLabel.setVisible(false);
        nextPhaseButton.setVisible(false);
        countryALabel.setVisible(false);
        countryBLabel.setVisible(false);
        countryAName.setVisible(false);
        countryBName.setVisible(false);
        addEventListener();
        this.view = view;
    }


    /**
     * Add event listener to the countryPane
     */
    public void addEventListener() {
        mapPane.setOnMouseClicked((e) -> { if (e.getEventType() == MouseEvent.MOUSE_CLICKED) { view.clickedMap(); } });
        // TODO: for draw arrow purpose
//        mapPane.setOnMousePressed((e) -> { if (e.getEventType() == MouseEvent.MOUSE_PRESSED) { view.pressedMap(e.getX(), e.getY()); } });
//        mapPane.setOnMouseDragged((e) -> { if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) { view.draggedMap(e.getX(), e.getY()); } });
//        mapPane.setOnMouseReleased((e) -> { if (e.getEventType() == MouseEvent.MOUSE_RELEASED) { view.releasedMap(e.getX(), e.getY()); } });
    }


    /**
     * Called when user entered number of armies moved value and press enter button, pass event to View
     */
    public void enteredNumArmiesMoved() { view.fortification(numArmiesMoveTextField.getText()); }


    /**
     * Display/Hide the invalid move label, update the invalid info
     * Called by View.*()
     * @param show decides whether the invalid move label need to be displayed
     * @param invalidInfo is the invalid move info
     */
    public void showInvalidMoveLabelInfo(boolean show, String invalidInfo) {
        numArmiesMoveTextField.clear(); // TODO: could be removed?
        invalidMoveLabel.setVisible(show);
        invalidMoveLabel.setText(invalidInfo);
    }


    /**
     * Display/Hide the from-to countries info pane between different phase
     * @param show decides whether the from-to countries info need to be displayed
     */
    public void showFromToCountriesInfoPane(boolean show) {
        countryALabel.setVisible(show);
        countryAName.setVisible(show);
        countryBLabel.setVisible(show);
        countryBName.setVisible(show);
        numArmiesMoveLabel.setVisible(show);
        numArmiesMoveTextField.setVisible(show);
    }

    public void resetFromToCountriesInfo() {
        countryAName.setText("NONE");
        countryAName.setStyle("-fx-border-color: red; -fx-border-width: 3");
        countryBName.setText("NONE");
        countryBName.setStyle("-fx-border-color: red; -fx-border-width: 3");
    }

    public void showNextPhaseButton(String nextPhase) {
        nextPhaseButton.setText(nextPhase);
        nextPhaseButton.setVisible(true);
        hidePhaseLabel();
    }

    public void setFromCountryInfo(Country country) {
        countryAName.setText(country.getName());
        countryAName.setStyle("-fx-border-color: #00ff00; -fx-border-width: 3");
    }

    public void setToCountryInfo(Country country) {
        countryBName.setText(country.getName());
        countryBName.setStyle("-fx-border-color: #00ff00;  -fx-border-width: 3");
    }

    public void skipReinforcementPhase() {
        showPlayerViewPane(false);
        view.skipFortificationPhase();
    }

    public void backToMenu() { view.showMenuStage(); }

    public void setPhaseLabel(String phase) { phaseLabel.setText(phase); }

    public void hidePhaseLabel() { phaseLabel.setVisible(false); }

    public void showPhaseLabel() { phaseLabel.setVisible(true); }

    public void hideNextPhaseButton() { nextPhaseButton.setVisible(false); }

    public void showPlayerViewPane(boolean show) {
        if (show) {
            currentPlayerPane.setVisible(true);
        } else {
            skipReinforcementPhaseButton.setVisible(false);
            numArmiesMoveTextField.clear();
            numArmiesMoveTextField.setVisible(false);
        }
    }

    public void startNextPhase() { view.startNextPhase(); }

    public void showReinforcementPhaseButton(boolean show) { skipReinforcementPhaseButton.setVisible(show); }

    // give the reference to PlayerView for updating info
    public Label getCurrentPlayerLabel() { return currentPlayerLabel; }

    public Label getArmiesInHandLabel() { return armiesInHandLabel; }

    public String getNextPhaseButtonTest() { return nextPhaseButton.getText(); }






}
