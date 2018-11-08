package model;

import common.Action;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Define class of a player
 * The following is only base code, further features need to be added
 */
public class Player extends Observable {

    //Unique Id for each player, starts from 1
    private int Id;
    //Counter to assign unique Id
    private static int cId=0;
    private String name;
    private int armies;
    private ArrayList<Country> countriesOwned;
    private ArrayList<Continent> continentsOwned;
    private String color = "#4B0082";
    private int totalStrength;

    private Country attacker;
    private int attackerDiceNum;
    private Country defender;
    private int defenderDiceNum;
    private int countriesSize;
    private int cardsArmy;

    private HashMap<String,Integer> cards;
    private int numberOccupy;
    private List<Card> playerCardList;


    /**
     * Constructor of player
     * @param name player name
     */
    public Player(String name, int countriesSize){
        this.Id=++cId;
        this.name= name;
        this.countriesSize = countriesSize;
        armies = 0;
        countriesOwned = new ArrayList<Country>();
        continentsOwned = new ArrayList<Continent>();
        totalStrength = 0;
        cards = new HashMap<>();
        cards.put("infantry",0);
        cards.put("cavalry",0);
        cards.put("artillery",0);
        numberOccupy = 0;
        this.playerCardList = new ArrayList<>();
    }

    /**
     * return total number of cards owned by the player
     * @return n the total number of cards owned by the player
     */
    public int getTotalCards(){
        int n = 0;
        for (String key : cards.keySet()){
            n += cards.get(key);
        }
        return n;
    }

    public HashMap<String,Integer> getCards(){
        return cards;
    }

    /**
     * Get Player Card List
     * @return playerCardList
     */
    public List<Card> getPlayerCardList() {
        return playerCardList;
    }

    /**
     * Getter for color of player
     * @return Color of this player
     */
    public String getColor() {
        return color;
    }

    /**
     * Setter for color of player
     */
    public void setColor() {
        Random rand = new Random(Id*Id*Id);

        // Will produce only bright / light colours:
        float r = (float) (rand.nextFloat() / 2f + 0.4);
        float g = (float) (rand.nextFloat() / 2f + 0.4);
        float b = (float) (rand.nextFloat() / 2f + 0.4);

        Color randomColor = new Color(r, g, b);
        String hex = "#"+Integer.toHexString(randomColor.getRGB()).substring(2);

        this.color=hex;
    }

    /**
    * Getter to get Id
    * @return Id of the player
    */ 
    public int getId() {
        return Id;
    }

    /**
     * Getter to get name
     * @return  player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter to get the number of armies
     * @return The number of armies that the player has currently
     */
    public int getArmies() {
        return armies;
    }

    /**
     * Getter to get the number of continents owned by this player
     * @return The number of continents that the player has currently
     */
    public ArrayList<Continent> getContinentsOwned() { return continentsOwned; }

    /**
     * Getter to get CountriesOwned
     * @return  The list of countries object that the player has currently
     */
    public ArrayList<Country> getCountriesOwned() {
        return countriesOwned;
    }

    /**
     * set the countries owned
     * @param  countriesOwned the countries owned
     */
    public void setCountriesOwned(ArrayList<Country> countriesOwned) {
        this.countriesOwned = countriesOwned;
    }

    /**
    * set the number of armies
    * @param  armies the number of armies
    */
    public void setArmies(int armies) {
        this.armies = armies;
    }

    /**
     * Getter to get totalStrength
     * @return  The num of totalStrength that the player has currently
     */
    public int getTotalStrength() {
        return totalStrength;
    }

    /**
     * set the number of totalStrength
     * @param totalStrength the number of totalStrength
     */
    public void setTotalStrength(int totalStrength) {
        this.totalStrength = totalStrength;
    }

    /**
    * Add armies in the very first of the reinforcement phase
    * The number of armies added is computed based on the number of countries and cards it has
    */
    public void addRoundArmies(){

        int newArmies = getArmiesAdded();
        setArmies(newArmies);
        setTotalStrength(totalStrength + newArmies);
    }

    /**
    * Compute the armiesAdded based on the number of countries continent and cards it has
    * @return armies need to be added
    */
    private int getArmiesAdded() {

        int armiesAdded = 0;

        // based on countries num
        if (countriesOwned.size() > 0) {
            armiesAdded = countriesOwned.size() / 3;
        }

        //based on continent
        armiesAdded += getArmiesAddedFromContinent();

        //TODO:based on card
        //need to implement next phase

        armiesAdded += cardsArmy;
        cardsArmy = 0;

        // the minimal number of reinforcement armies is 3
        if (armiesAdded < 3) {
            armiesAdded = 3;
        }

        return armiesAdded;

    }

    /**
    * Compute the armiesAdded based on the continents it has
    * @return the number of armies need to be added based on the continents it has
    */
    private int getArmiesAddedFromContinent() {

//        // record the number of countries in a continent
//        HashMap<Continent, Integer> continentStatics = new HashMap<Continent, Integer>();
//
//        for (Country country : countriesOwned) {
//
//            Continent continent = country.getContinent();
//
//            if (continentStatics.containsKey(continent)){
//
//                int newValue = continentStatics.get(continent) + 1;
//                continentStatics.put(continent, newValue);
//
//            } else {
//                continentStatics.put(continent, 1);
//            }
//        }
//
//        //
        int armiesAdded = 0;
//        for (Continent c : continentStatics.keySet()) {
//
//            if (c.getSize() == continentStatics.get(c)) {
//                armiesAdded += c.getControlVal();
//            }
//        }
        for (Continent continent : continentsOwned) {
            armiesAdded += continent.getControlVal();
        }


        return armiesAdded;
    }


    /**
     * Substract one for armies when allocated army in the initArmy() or the reinforcements phase
     * @param num remove army from player
     */
    public void subArmies(int num){

        int newArmies = armies - num;
        setArmies(newArmies);
    }

    /**
     * Substract one for armies when allocated army in the initArmy() or the reinforcements phase
     * @param num remove army from player
     */
    public void subTotalStrength(int num){

        int newStrength = totalStrength - num;
        setTotalStrength(newStrength);
    }

    /**
    * Verify if the armies is empty
    * @return  True if armies == 0, else False
    */
    public boolean isEmptyArmy() { return armies == 0 ? true : false; }

    /**
    * Add a country in the countriesOwned list
    * @param  c country need to be added
    */
    public void addCountry(Country c){
        //verify if the country is exist in the countriesOwned??
        countriesOwned.add(c);
        Continent continent = c.getContinent();
        for (Country country : continent.getCountry()) {
            if (country.getOwner() == null || !country.getOwner().equals(this)) return;
        }
        addContinent(continent);
    }

    /**
    * Remove a country from the countriesOwned list
    * @param  c country need to be deleted
    * @return true delete success, false delete failed
    */
    public boolean delCountry(Country c){

        Iterator<Country> it = countriesOwned.iterator();
        while(it.hasNext())
        {
            if (c.equals(it.next())){
                it.remove();
                if (continentsOwned.contains(c.getContinent())) {
                    delContinent(c.getContinent());
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Add a continent in the continentsOwned list
     * @param  continent continent need to be added
     */
    public void addContinent(Continent continent){
        //verify if the country is exist in the countriesOwned??
        continentsOwned.add(continent);
    }

    /**
     * Remove a continent from the continentsOwned list
     * @param  continent continent need to be deleted
     * @return true delete success, false delete failed
     */
    public boolean delContinent(Continent continent){

        Iterator<Continent> it = continentsOwned.iterator();
        while(it.hasNext())
        {
            if (continent.equals(it.next())){
                it.remove();
                return true;
            }
        }
        return false;
    }

    public boolean isContain(Country c) {

        for (Country each : countriesOwned) {
            if (c.equals(each)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verify if two countries is connected
     * @param start country 1
     * @param end country 2
     * @return true, connected; false unconnected
     */
    public boolean isConnected(Country start, Country end) {

        // if doesn't contain both of countries
        if (!isContain(start) ||  !isContain(end)) {
            return false;
        }

        //both contain, BFS find a path
        for (Country c : countriesOwned) {
            if (!c.equals(start)) {
                c.setProcessed(false);
            }
        }

        Queue<Country> queue = new PriorityQueue<Country>();
        queue.add(start);
        start.setProcessed(true);

        while (queue.isEmpty() == false) {
            Country c = queue.poll();

            ArrayList<Country> adjCountries = c.getAdjCountries();

            if ( adjCountries != null && adjCountries.size() != 0) {

                for(Country each : adjCountries) {

                    if (each.equals(end))  return true;

                    if (countriesOwned.contains(each) && !each.isProcessed()) {
                        each.setProcessed(true);
                        queue.add(each);
                    }
                }
            }
        }
        return false;
    }

    /**
     * verify if two users is equal
     * @param p Player need to be compare
     * @return true when comparing the same player false otherwise
     */
    public boolean equals(Player p) { return this.getId() == p.getId(); }

    /**
     * Test if attack is valid
     * @param attacker The country who start the attack
     * @param attackerNum how many dise the attacker will use in this attack
     * @param defender The country who defend the attack
     * @param defenderNum how many dise the defender will use in this attack
     * @return if two country is adjacent, and their dice is less the armies they owned, return true, else false
     */
    private boolean isValidAttack(Country attacker, String attackerNum, Country defender, String defenderNum){

        Phase phase = Phase.getInstance();

        // if any of countries is none
        if (attacker == null || defender == null) {
            phase.setActionResult(Action.Invalid_Move);
            phase.setInvalidInfo("Countries can not be none");
            phase.update();
            return false;
        }

        // if int valid
        int attackerDiceNum = 0;
        int defenderDiceNum = 0;
        try{
            attackerDiceNum = Integer.valueOf(attackerNum);
            defenderDiceNum = Integer.valueOf(defenderNum);
        } catch (Exception e){
            phase.setActionResult(Action.Invalid_Move);
            phase.setInvalidInfo("Input error, invalid dice number.");
            phase.update();
            return false;
        }

        if (!attacker.getOwner().equals(this)) {
            Phase.getInstance().setActionResult(Action.Invalid_Move);
            Phase.getInstance().setInvalidInfo("Invalid attack, This is not your country.");
            Phase.getInstance().update();
            return false;
        }

        //if valid attack
        if (attacker.getOwner().equals(defender.getOwner())) {
            phase.setActionResult(Action.Invalid_Move);
            phase.setInvalidInfo("Invalid attack, cannot attack a country owned by player himself.");
            phase.update();
            return false;
        }

        // if attacker's dice valid
        if (!attacker.isValidAttacker(attackerDiceNum)) {
            phase.setActionResult(Action.Invalid_Move);
            phase.setInvalidInfo("Invalid attacker dice number, armies in attacker must more than two, and the dice must less than armies");
            phase.update();
            return false;
        }

        // if defender's dice valid
        if (!defender.isValidDefender(defenderDiceNum)) {
            phase.setActionResult(Action.Invalid_Move);
            phase.setInvalidInfo("Invalid defender's dice number, the dice must less than armies");
            phase.update();
            return false;
        }

        // if two countries adjacent
        if (!attacker.isAdjacent(defender)){
            phase.setActionResult(Action.Invalid_Move);
            phase.setInvalidInfo("Two countries is not adjacent");
            phase.update();
            return false;
        }

        // update the attack info
        this.attacker = attacker;
        this.attackerDiceNum = attackerDiceNum;
        this.defender = defender;
        this.defenderDiceNum = defenderDiceNum;
        return true;
    }

    /**
     * Get a sorted list of random dices
     * @param num how many dices needed
     */
    public ArrayList<Integer> getRandomDice(int num){

        ArrayList<Integer> dices = new ArrayList<Integer>();
        Random random = new Random();

        for (int i=0; i<num; i++){
            dices.add(random.nextInt(6)+1);
        }
        Collections.sort(dices, Collections.reverseOrder());
        return dices;
    }

    /**
     * Battle only run once time
     */
    private void attackOnce() {

        // roll the dices to battle
        ArrayList<Integer> dicesAttacker = getRandomDice(attackerDiceNum);
//        System.out.println(dicesAttacker);
        ArrayList<Integer> diceDefender = getRandomDice(defenderDiceNum);
//        System.out.println(diceDefender);

//        System.out.println("before roll:");
//        System.out.println("attacker:");
//        System.out.println("country " + attacker.getArmies());
//        System.out.println("player " + attacker.getOwner().getTotalStrength());
//        System.out.println("defender:");
//        System.out.println("country " + defender.getArmies());
//        System.out.println("player " + defender.getOwner().getTotalStrength());

        // compare the rolling result
        int range = attackerDiceNum < defenderDiceNum? attackerDiceNum : defenderDiceNum;
        for (int i=0; i<range; i++){

            if (diceDefender.get(i) >= dicesAttacker.get(i)) {
                attacker.setArmies(attacker.getArmies()-1);
                attacker.getOwner().subTotalStrength(1);

            } else {
                defender.setArmies(defender.getArmies()-1);
                defender.getOwner().subTotalStrength(1);

                //if defender's armies == 0, attacker victory
                if (isDefenderLoose()) return;
            }

//            System.out.println("after roll:");
//            System.out.println("attacker:");
//            System.out.println("country " + attacker.getArmies());
//            System.out.println("player " + attacker.getOwner().getTotalStrength());
//            System.out.println("defender:");
//            System.out.println("country " + defender.getArmies());
//            System.out.println("player " + defender.getOwner().getTotalStrength());
//            System.out.println("_________________________________________________________");
        }

        return;

    }

    /**
     * Battle until the defender be occupied or the attacker consume its armies
     */
    private void allOut() {

        Phase phase = Phase.getInstance();
        while (true) {
            attackerDiceNum = attacker.getArmies() > 3? 3 : attacker.getArmies();
            defenderDiceNum = defender.getArmies() > 2? 2 : defender.getArmies();

            attackOnce();
            // if defender is occupied by attacker
            if(attacker.getOwner().equals(defender.getOwner())) break;
            // if attacker exhaust all its armies
            if(attacker.getArmies() == 0) break;
        }
        return;
    }

    /**
     * Verify if defender loose the country
     * @return
     */
    private boolean isDefenderLoose() {

        Phase phase = Phase.getInstance();
        if (defender.getArmies() == 0) {

            if (defender.getOwner().countriesOwned.size() == 1) {

                // TODO: add all cards form defender's owner
                phase.setInvalidInfo(defender.getOwner().getName() + " lost all the countries!");
                phase.update();
                getDefenderCards(attacker.getOwner(),defender.getOwner());
            }

            // change the ownership of the defender country
            attacker.getOwner().addCountry(defender);
            defender.getOwner().delCountry(defender);
            defender.setPlayer(attacker.getOwner());

            // add
            numberOccupy++;

            // if attacker win the game
            if (attacker.getOwner().getCountriesOwned().size() == countriesSize) {
                phase.setCurrentPhase("Game Over");
                // give the name of winner
                phase.setInvalidInfo(attacker.getOwner().getName());
            }

            // if nobody win, ask for move armies to new occupied country
            phase.setActionResult(Action.Move_After_Conquer);

            // if attack possible
            if (!isAttackPossible()) {
                phase.setInvalidInfo("Attack Impossible");
            }
            return true;
        }
        return false;
    }

    /**
     * Move number of armies to the new conquered country
     * @param num the number of armies need to be move
     * @return 1 success, -1 somehow wrong
     */
    public void moveArmy(String num){

        Phase phase = Phase.getInstance();
        int numArmies = 0;
        try{
            numArmies = Integer.valueOf(num);
        } catch (Exception e){
            phase.setActionResult(Action.Invalid_Move);
            phase.setInvalidInfo("Please input a number");
            phase.update();
            return;
        }

        if (this.isContain(attacker) && this.isContain(defender) && attacker.getArmies() >= numArmies && numArmies >= attackerDiceNum) {
            attacker.setArmies(attacker.getArmies() - numArmies);
            defender.setArmies(defender.getArmies() + numArmies);

            phase.setActionResult(Action.Show_Next_Phase_Button);
            phase.update();
            return;
        }
        phase.setActionResult(Action.Invalid_Move);
        phase.setInvalidInfo("Number armies to move must more than the number of dice, and less than armies the country has");
        phase.update();
        return;
    }

    /**
     * Verify if attack is possible
     * @return true, if has at least one country to attack; else false
     */
    public boolean isAttackPossible() {

        for (Country c : countriesOwned) {
            if (c.getArmies() < 2) continue;
            for (Country adj : c.getAdjCountries()) {
                if (!adj.getOwner().equals(c.getOwner())){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isGg() {
        return this.countriesOwned.size() == 0;
    }

    /**
     *  Implementation of reinforcement
     */
    public void reinforcement(){

        Phase.getInstance().setCurrentPhase("Reinforcement Phase");
        addRoundArmies();
        Phase.getInstance().update();
    }

    public void handleCards(String card1, String card2, String card3){
        cards.put(card1,cards.get(card1) - 1);
        cards.put(card2,cards.get(card2) - 1);
        cards.put(card3,cards.get(card3) - 1);
        CardModel.getInstance().update();
    }

    public void exchangeForArmy(){
        cardsArmy = Model.cardsValue;
        Model.cardsValue += 5;
    }

    public void addRandomCard(String newCard) {

        if (numberOccupy > 0) {
            int value = cards.get(newCard) + 1;
            cards.put(newCard, value);
        }
        //reset the number of occupy
        numberOccupy = 0;
    }

    public void getDefenderCards(Player attacker, Player defender){
        for(String key : defender.cards.keySet()){
            attacker.cards.put(key, defender.cards.get(key) + 1);
            defender.cards.put(key,0);
        }
    }


    /**
     * Method for attack operation
     * @param attacker The country who start an attack
     * @param attackerNum how many dice the attacker choose
     * @param defender The country who defend
     * @param defenderNum how many dice the defender choose
     * @param isAllOut true, if the attacker want to all-out; else false
     */
    public void attack(Country attacker, String attackerNum, Country defender, String defenderNum, boolean isAllOut){

        Phase phase = Phase.getInstance();

        if (!isValidAttack(attacker, attackerNum, defender, defenderNum)) {
            return;
        }

        // if defender country doesn't has army
        if (isDefenderLoose()) {
            phase.setActionResult(Action.Move_After_Conquer);
            phase.update();
            return;
        }

        if (isAllOut) {
            // dice number depend by computer
            allOut();
        } else {
            // players choose how many dice need to put
            attackOnce();
        }

        //update phase info
        if (!phase.getCurrentPhase().equals("Game Over") || phase.getActionResult() != Action.Move_After_Conquer) {
            phase.setActionResult(Action.Show_Next_Phase_Button);
            phase.update();
        }
        phase.update();

        return;
    }

    /**
     * Method for fortification operation
     * @param source The country moves out army
     * @param target The country receives out army
     * @param armyNumber Number of armies to move
     */
    public void fortification(Country source, Country target, int armyNumber){
        //return no response to view if source country's army number is less than the number of armies on moving,
        //or the source and target countries aren't connected through the same player's countries
        if (!source.getOwner().equals(this) || !target.getOwner().equals(this)) {
            Phase.getInstance().setActionResult(Action.Invalid_Move);
            Phase.getInstance().setInvalidInfo("Invalid move, This is not your country.");
            Phase.getInstance().update();
            return;
        }

        if(source.getArmies()<armyNumber || !source.getOwner().isConnected(source,target)) {
            Phase.getInstance().setActionResult(Action.Invalid_Move);
            Phase.getInstance().setInvalidInfo("invalid move");
            Phase.getInstance().update();
            return;
        }

        source.setArmies(source.getArmies()-armyNumber);
        target.setArmies(target.getArmies()+armyNumber);

        Phase.getInstance().setActionResult(Action.Show_Next_Phase_Button);
        Phase.getInstance().update();
    }

}
