package driver;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Model;
import view.View;


/**
* The entrence of the application, main() method
*/
public class Driver extends Application {

    private Model model;
    private View view;

    @Override
    public void start(javafx.stage.Stage primaryStage) throws Exception {
        model = new Model();
        view = new View();
        model.addObserver(view);

        // This is necessary because when the selecting map action happens,
        // View need to pass file path to the Model, but View don't have the
        // Model reference
        view.setModel(model);

        view.showMenuStage();
    }

    /**
     * Called when the game quits
     */
    public void close() {
        this.close();
    }


}
