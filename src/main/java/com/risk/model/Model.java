package com.risk.model;

import com.risk.common.Action;
import com.risk.common.Message;
import com.risk.common.STATE;
import com.risk.exception.InvalidMapException;
import com.risk.validate.MapValidator;
import com.risk.view.*;

import java.io.*;
import java.util.*;

/**
 * Define Observable class
 * ...
 */

public class Model extends Observable implements Serializable{

    public static int cardsValue = 5;
    public static final String[] cards = {"infantry","cavalry","artillery"};
    private static Player currentPlayer;
    private int numOfCountries;
    private int numOfContinents;
    private ArrayList<Player> players;
    private HashMap<String,Country> countries;
    private ArrayList<Continent> continents;
    private int playerCounter;
    private boolean validFile = true;

    //decided whether country view should respond to the event
    public static boolean disable = false;

    //indicate current phaseNumber; startUp0; rPhase1; aPhase2; fPhase3
    public static int phaseNumber = 0;

    private FileInfoMenu fileInfoMenu;
    private NumPlayerMenu numPlayerMenu;


    /**
     * ctor for Model
     */
    public Model(){
        players = new ArrayList<>();
        countries = new HashMap<>();
        continents = new ArrayList<>();
        playerCounter = 0;
    }

    public Model(Model model){
        this.continents = model.continents;
    }

    /**
     * Get fileInfoMenu
     * @return fileInfoMenu the fileInfo menu
     */
    public FileInfoMenu getFileInfoMenu() {
        return fileInfoMenu;
    }

    /**
     * Get numPlayerMenu
     * @return numPlayerMenu the numPlayer menu
     */
    public NumPlayerMenu getNumPlayerMenu() {
        return numPlayerMenu;
    }

    /**
     * set fileInfoMenu, for the test
     * @param fileInfoMenu the observable
     */
    public void setFileInfoMenu(FileInfoMenu fileInfoMenu) {
        this.fileInfoMenu = fileInfoMenu;
    }

    /**
     * set numPlayerMenu, for the test
     * @param numPlayerMenu the observable
     */
    public void setNumPlayerMenu(NumPlayerMenu numPlayerMenu) {
        this.numPlayerMenu = numPlayerMenu;
    }

    /**
     * reset model object before reload map file
     */
    private void reset(){
        players = new ArrayList<>();
        countries = new HashMap<>();
        continents = new ArrayList<>();
        validFile = true;
    }

    /**
     * get current player
     * @return current player
     */
    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * set current player
     * @param  p player
     */
    public static void setCurrentPlayer(Player p) {
        currentPlayer = p;
    }


    /**
     *  notify the view that model state has changed
     * @param message The message to send to the view, may include some important information
     */
    private void notify(Message message) {
        setChanged();
        notifyObservers(message);
    }

    /**
     * Method for start up phase operation
     * @param countryViewHashMap the countries observer
     */

    public void startUp(HashMap<Integer,CountryView> countryViewHashMap){

        int id = 1;
        for (String key:countries.keySet()) {
            countries.get(key).addObserver(countryViewHashMap.get(id));
            countries.get(key).callObservers();
            id ++;
        }
        //send next state message
        Message message = new Message(STATE.PLAYER_NUMBER,null);
        notify(message);

        Phase.getInstance().addObserver(PhaseView.getInstance());
        Phase.getInstance().setCurrentPlayer(currentPlayer);
        Phase.getInstance().setCurrentPhase("Start Up Phase");
        Phase.getInstance().update();

        CardModel.getInstance().addObserver(CardView.getInstance());

    }

    /**
     * button event for  trade button
     * @param cards list of cards
     */
    public void trade(ArrayList<Card> cards){


        String card1 = cards.get(0).cardType.toString().toLowerCase();
        String card2 = cards.get(1).cardType.toString().toLowerCase();
        String card3 = cards.get(2).cardType.toString().toLowerCase();

        if(!validCardExchange(card1,card2,card3)){
            CardModel.getInstance().setInvalidInfo(1);
            CardModel.getInstance().update();
            return;
        }
        CardModel.getInstance().setInvalidInfo(0);
        CardModel.getInstance().update();
        currentPlayer.handleCards(card1, card2, card3);
        currentPlayer.exchangeForArmy();
        CardModel.getInstance().update();
    }

    /**
     * indicate whether the exchange operation is valid
     * @param card1 name of the first card
     * @param card2 name of the second card
     * @param card3 name of the third card
     * @return true if the operation is valid; otherwise return false
     */
    public boolean validCardExchange(String card1, String card2, String card3){
        if(card1 == null || card2 == null || card3 == null){
            return false;
        }
        if(card1.equals(card2) && card2.equals(card3)){
            if(currentPlayer.getCards().get(card1) >= 1 && currentPlayer.getCards().get(card2) >= 1 &&
                    currentPlayer.getCards().get(card3) >= 1){
                return true;
            } else {
                return false;
            }
        }
        if(!card1.equals(card2) && !card2.equals(card3) && !card1.equals(card3)){
            if(currentPlayer.getCards().get(card1) >= 1 && currentPlayer.getCards().get(card2) >= 1 &&
                    currentPlayer.getCards().get(card3) >= 1){
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * button event for cardView quit button
     */
    public void quitCards(){

        if(currentPlayer.getTotalCards() >= 5){
            CardModel.getInstance().setInvalidInfo(3);
            CardModel.getInstance().update();
            return;
        }

        CardModel.getInstance().hide();
        disable = false;
        currentPlayer.reinforcement();
    }

    /**
     * Reinforcement phaseNumber
     * Set new current player
     * Add armies to the player
     * CardModel exchange for armies
     */
   public void reinforcement(){
       PhaseView.getInstance().hide();
       CardModel.getInstance().setCurrentPlayer(currentPlayer);
       CardModel.getInstance().update();

    }

    /**
     * attack phaseNumber method
     * @param attacker Country who start attack
     * @param attackerDiceNum how many dice that attacker use
     * @param attacked Country who defend himself
     * @param attackedDiceNum how many dice that defender use
     * @param isAllOut is all-out or not
     */
    public void attack(Country attacker, String attackerDiceNum, Country attacked, String attackedDiceNum, boolean isAllOut){

        currentPlayer.attack(attacker, attackerDiceNum, attacked, attackedDiceNum, isAllOut);
    }

    public void moveAfterConquer(String num) {
        currentPlayer.moveArmy(num);
    }

    /**
     * Method for fortification operation
     * @param source country move armies from
     * @param target country move armies to
     * @param armyNumber number of moved armies
     */
    public void fortification(Country source, Country target, String armyNumber){

        int moveNumber;
        if(source == null || target == null){
            Phase.getInstance().setActionResult(Action.Invalid_Move);
            Phase.getInstance().setInvalidInfo("must choose a valid country!");
            Phase.getInstance().update();
            return;
        }

        try{
            moveNumber = Integer.parseInt(armyNumber);
        } catch (Exception ex){
            Phase.getInstance().setActionResult(Action.Invalid_Move);
            Phase.getInstance().setInvalidInfo("please enter an integer!!");
            Phase.getInstance().update();
            return;
        }


        if(moveNumber <= 0){
            Phase.getInstance().setActionResult(Action.Invalid_Move);
            Phase.getInstance().setInvalidInfo("please enter an positive integer!");
            Phase.getInstance().update();
            return;
        }

        Phase.getInstance().setCurrentPhase("Fortification Phase");
        Phase.getInstance().update();
        currentPlayer.fortification(source,target,Integer.parseInt(armyNumber));
    }

    
    /**
     * Set current player to the next one according in round robin fashion
     * If a new round starts from the next player, send ROUND_ROBIN STATE to view
     * Considerations:
     * 1. When allocate armies at start up phaseNumber, when the last player finishes army allocation, this method tells view
     * round robin starts;
     * 2. In round robin, when the last player finishes fortification phaseNumber, this method tells view round robin starts
     * again, meanwhile change current player to the first player.
     */
    public void nextPlayer(){

        int nextId = 0;

        while (true) {
            int currentId = currentPlayer.getId();
            //can be achieved by players rather than getNumOfPlayer()
            int numPlayer = players.size();
            //wraps around the bounds of ID
            nextId = (currentId % numPlayer + numPlayer) % numPlayer + 1;
            currentPlayer = players.get(nextId - 1);

            if (!currentPlayer.isGg()) break;
        }

        Phase.getInstance().setCurrentPlayer(currentPlayer);
        Phase.getInstance().update();
    }

    /**
     * allocate one army in a specific country
     * @param country Country reference
     */
    //TODO:change algorithm
    public void allocateArmy(Country country){

        if(disable) {
            Phase.getInstance().setInvalidInfo("Start Up Phase ended!");
            Phase.getInstance().update();
            return;
        }
        if(!currentPlayer.getCountriesOwned().contains(country)){
            Phase.getInstance().setInvalidInfo("Invalid country!");
            Phase.getInstance().update();
            return;
        }

        country.addArmies(1);
        country.getOwner().subArmies(1);


        Phase.getInstance().setActionResult(Action.Allocate_Army);
        Phase.getInstance().update();

        //startUpPhase
        if(phaseNumber == 0){
            //all the armies are allocated
            if(country.getOwner().getArmies() == 0){
                if(!currentPlayer.equals(players.get(players.size() - 1))){
                    nextPlayer();
                } else {
                    disable = true;
                    Phase.getInstance().setActionResult(Action.Show_Next_Phase_Button);
                    Phase.getInstance().update();
                    phaseNumber = 1;
                }
            }
        }
        //rPhase
        else {
            if(country.getOwner().getArmies() == 0){
                disable = true;
                Phase.getInstance().setActionResult(Action.Show_Next_Phase_Button);
                Phase.getInstance().update();
                phaseNumber = 2;
            }
        }

    }

    /**
     * create Player object for every Player, and add the observer
     * Players are allocated a number of initial armies
     * notify CountryView (country info
     * notify PlayerView  (current player)
     * notify View (state and additional info)
     * @param enteredPlayerNum number of players
     */
    public void initiatePlayers(String enteredPlayerNum){

        players.clear();
        playerCounter = Integer.parseInt(enteredPlayerNum);

        if(playerCounter > countries.size() || playerCounter <= 1 || playerCounter > 6){
            numPlayerMenu.setValidationResult(false, "invalid players number!");
            numPlayerMenu.update();
            return;
        }

        numPlayerMenu.setValidationResult(true,"");
        numPlayerMenu.update();

        int initialArmies = getInitialArmies(playerCounter);
        initialArmies=3;


        for (int i = 0; i < playerCounter; i++){

            String strategy = "human";
            Player newPlayer = new Player("Player" + String.valueOf(i), countries.size(), strategy);
            newPlayer.setArmies(initialArmies);
            newPlayer.setTotalStrength(initialArmies);
            //assign each player a different color
            newPlayer.setColor();
            players.add(newPlayer);
        }

        ArrayList<String> shuffle = new ArrayList<>();

        //assign countries to all the players justly
        for (String key:countries.keySet()) {
            shuffle.add(key);
        }
        for(int i = 0; i < shuffle.size(); i ++){
            countries.get(shuffle.get(i)).setPlayer(players.get(i % players.size()));
            players.get(i % players.size()).addCountry(countries.get(shuffle.get(i)));
        }

        //notify view to unpdate information
        for (String key:countries.keySet()) {
            countries.get(key).callObservers();
        }
        //current player notify
        currentPlayer = players.get(0);
        Phase.getInstance().setCurrentPlayer(currentPlayer);
        Phase.getInstance().update();

        //give state to view
        PlayersWorldDomination.getInstance().setPlayers(players);
        PlayersWorldDomination.getInstance().setTotalNumCountries(countries.size());
        PlayersWorldDomination.getInstance().addObserver(PlayersWorldDominationView.getInstance());
        PlayersWorldDomination.getInstance().update();
    }

    /**
     * calculate initial armies
     * @return initial armies
     */
    private int getInitialArmies(int numOfPlayers) {

        switch (numOfPlayers){
            case 2:
                return 40;
            case 3:
                return 35;
            case 4:
                return 30;
            case 5:
                return 25;
            case 6:
                return 20;
        }
        return 0;
    }

    /**
     * load file and initiate model
     * @param filePath The path of the map file
     * @throws IOException the exception that throw
     */
    public void loadFromMapFile(String filePath) throws IOException{
        reset();
        String content = "";
        String line = "";
        String bodies[];
        FileReader fileReader = new FileReader(filePath);
        BufferedReader in = new BufferedReader(fileReader);
        while (line != null){
            content = content + line + "\n";
            line = in.readLine();
        }
        //validate map file
        try {
            bodies = content.split("\n\n");
            initiateContinents(bodies[1]);
            for(int i = 2; i < bodies.length; i ++){
                initiateCountries(bodies[i]);
            }
        } catch (Exception ex){
            validFile = false;
        }
    }


    /**
     * load file path for map editor
     * @param filePath The path of the map file
     * @throws IOException
     */
    public void editorReadFile(String filePath) throws IOException{
        loadFromMapFile(filePath);
        try {
            MapValidator.validateMap(this);
        }
        catch (InvalidMapException ex){
            validFile = false;
            System.out.println(ex.toString());
        }
    }

    /**
     * load the map from a player chose map file
     * validate map file
     * initiate continents,countries and players
     * notify View
     * @param filePath The path of the map file
     * @throws IOException io exceptions
     */
    public void readFile(String filePath) throws IOException {

        loadFromMapFile(filePath);
        Message message;
        if(!validFile){
            fileInfoMenu.setValidationResult(false,"invalid file format!");
            fileInfoMenu.update();
            numPlayerMenu.setVisible(false);
            numPlayerMenu.update();
            return;
        }

        try {
            MapValidator.validateMap(this);
        }
        catch (InvalidMapException ex){

            fileInfoMenu.setValidationResult(false,ex.getMessage());
            fileInfoMenu.update();
            numPlayerMenu.setVisible(false);
            numPlayerMenu.update();

            System.out.println(ex.toString());
            return;
        }
        fileInfoMenu.setValidationResult(true,"valid map");
        fileInfoMenu.update();

        numPlayerMenu.setVisible(true);
        numPlayerMenu.setMaxNumPlayer(countries.size());
        numPlayerMenu.setValidationResult(false,"Total Player: NONE");
        numPlayerMenu.update();
    }

    /**
     * initiate continents
     * @param continentsList The list of continents
     */
    private void initiateContinents(String continentsList){
        int index = continentsList.indexOf("[Continents]");
        continentsList = continentsList.substring(index + 13);
        String[] list = continentsList.split("\n");
        int i = 0;
        for (String s : list) {
            int indexOfCol = s.indexOf('=');
            String continentName = s.substring(0,indexOfCol);
            int controlVal = Integer.parseInt(s.substring(indexOfCol + 1));
            Continent newContinent = new Continent(continentName,controlVal);
            newContinent.setColor();
            this.continents.add(newContinent);
            i ++;
        }
    }

    /**
     * initiate countries and all the neighbours
     * @param countriesList The list of countries
     */
    private void initiateCountries(String countriesList){
        if(countriesList.startsWith("[")){
            int index = countriesList.indexOf(']');
            countriesList = countriesList.substring(index + 2);
        }
        String[] list = countriesList.split("\n");
        for (String s : list) {
            String contents[] = s.split(",");
            String continentName = contents[3];
            int indexOfContinent = -1;
            for(int i = 0; i < continents.size(); i ++){
                if(continents.get(i).getName().equals(continentName)){
                    indexOfContinent = i;
                }
            }
            //current country does not exist in the map
            if(!countries.containsKey(contents[0])){
                Country newCountry = new Country(contents[0]);
                countries.put(contents[0],newCountry);
            }

            countries.get(contents[0]).setX(contents[1]);
            countries.get(contents[0]).setY(contents[2]);
            countries.get(contents[0]).setContinent(continents.get(indexOfContinent));

            //add country to corresponding continents
            continents.get(indexOfContinent).addCountry(countries.get(contents[0]));

            //no adjacent neighbour
            if(contents.length <=  4)
                break;

            for(int i = 4; i < contents.length; i ++){
                //neighbour country does not exist in the map
                if(!countries.containsKey(contents[i])){
                    Country newCountry = new Country(contents[i]);
                    countries.put(contents[i],newCountry);
                }
                //add neighbours to current country
                countries.get(contents[0]).addEdge(countries.get(contents[i]));
            }
        }
    }

    /**
     * link every country in the countries with corresponding CountryView
     * @param countryViewHashMap The CountryView map
     */
    public void linkCountryObservers(HashMap<Integer,CountryView> countryViewHashMap){

        //link every countryView
        int id = 1;
        for (String key:countries.keySet()) {
            countries.get(key).addObserver(countryViewHashMap.get(id));
            id ++;
        }
        //send next state message
        Message message = new Message(STATE.PLAYER_NUMBER,null);
        notify(message);
    }

    /**
     * get continenet list
     * @return continents list
     */
    public ArrayList<Continent> getContinents(){
        return continents;
    }

    /**
     * get country list
     * @return contries list
     */
    public HashMap<String, Country> getCountries(){
        return countries;
    }

    /**
     * get player list
     * @return players list
     */
    public ArrayList<Player> getPlayers(){
        return players;
    }

    /**
     * set continents list of model
     * @param continents  The continents to set
     */
    public void setContinents(List<Continent> continents) { this.continents = (ArrayList<Continent>) continents; }

    /**
     * report if the loaded map file is valid or not
     * @return true if the map file is valid; otherwise return false
     */
    public boolean isValidFile() {
        return validFile;
    }

    /**
     * Receive two menu observer references, bind them with corresponding observable subjects
     * @param fileInfoMenuView displays the general selected file info
     * @param numPlayerMenuView displays the num of players info
     */
    public void setMenuViews(FileInfoMenuView fileInfoMenuView, NumPlayerMenuView numPlayerMenuView) {
        fileInfoMenu = new FileInfoMenu();
        fileInfoMenu.addObserver(fileInfoMenuView);
        numPlayerMenu = new NumPlayerMenu();
        numPlayerMenu.addObserver(numPlayerMenuView);
    }

    /**
     * Add a random card
     */
    public void addRandomCard() {
        Random random = new Random();
        int num = random.nextInt(3);
        String newCard = cards[num];
        currentPlayer.addRandomCard(newCard);
    }

    /**
     * Verify if the Attack possible
     */
    public void isAttackPossible() {
       if (!currentPlayer.isAttackPossible()){
           Phase.getInstance().setActionResult(Action.Attack_Impossible);
           Phase.getInstance().setInvalidInfo("Attack Impossible. You Can Enter Next Phase Now.");
           Phase.getInstance().update();

//           Phase.getInstance().setActionResult(Action.Show_Next_Phase_Button);
//           Phase.getInstance().update();
       }
    }

    /**
     * save the whole game to be loaded later
     * @param fileName the name of file save to
     * @return true if the game is saved successfully; otherwise return false
     */
    public boolean save(String fileName){
        try {
            FileOutputStream fileStream = new FileOutputStream(fileName);
            ObjectOutputStream os = new ObjectOutputStream(fileStream);
            os.writeObject(this);
        } catch (FileNotFoundException ex){
            return false;
        } catch (IOException ex){
            return false;
        }
        return true;
    }

}
