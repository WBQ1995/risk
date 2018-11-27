package com.risk.strategy;

import com.risk.common.Action;
import com.risk.model.Country;
import com.risk.model.Model;
import com.risk.model.Phase;
import com.risk.model.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CheaterStrategy implements PlayerBehaviorStrategy {

    private Player player;
    private Phase phase;

    /**
     * Constructor
     * @param player the correspinding player
     */
    public CheaterStrategy(Player player){

        this.player = player;
        phase = Phase.getInstance();
    }

    /**
     *  reinforcement method
     *  doubles the number of armies on all its countries
     */
    @Override
    public void reinforcement() {

        // correct display current phase
        phase.setCurrentPhase("Reinforcement Phase");
        phase.update();

        // double armies in owned country
        player.doubleArmies(c -> true);

        // update phase
        phase.setActionResult(Action.Show_Next_Phase_Button);
        phase.update();
        Model.phaseNumber = 2;

    }

    /**
     * Automatically conquers all the neighbors of a country
     * @param country a country who attack others
     */
    private void conquerAdj(Country country) {

        country.getAdjCountries().stream()
                .filter(c -> !c.getOwner().equals(player))
                .forEach(defender -> {

                    // change the ownership of the defender country
                    defender.getOwner().delCountry(defender);
                    defender.setPlayer(country.getOwner());
                    country.getOwner().addCountry(defender);

                    // set phase info
                    phase.setActionResult(Action.Move_After_Conquer);
                    phase.setInvalidInfo("Successfully Conquered Country : "+ defender.getName());

                    // if attacker win the game
                    if (player.isWin()) {
                        phase.setActionResult(Action.Win);
                        // give the name of winner
                        phase.setInvalidInfo("Congratulations, You Win!");
                        phase.update();
                    }
                    phase.update();

                });

    }


    /**
     * Attack method
     * automatically conquers all the neighbors of all its countries
     * @param attacker  null
     * @param attackerNum 0
     * @param defender null
     * @param defenderNum 0
     * @param isAllOut false
     */
    @Override
    public void attack(Country attacker, String attackerNum, Country defender, String defenderNum, boolean isAllOut) {

        player.getCountriesOwned().stream()
                .collect(Collectors.toSet())
                .forEach(c -> conquerAdj(c));

    }


    /**
     * moveArmy method
     * doesn't do anything in cheater strategy
     * @param num 0
     */
    @Override
    public void moveArmy(String num) {

        phase.setActionResult(Action.Show_Next_Phase_Button);
        phase.setInvalidInfo("Army Movement Finish. You Can Start Another Attack Or Enter Next Phase Now");
        phase.update();

    }

    /**
     * fortification method
     * doubles the number of armies on its countries that have neighbors that belong to other players.
     * @param source null
     * @param target null
     * @param armyNumber 0
     */
    @Override
    public void fortification(Country source, Country target, int armyNumber) {

        // double armies in owned country
        player.doubleArmies(c -> c.hasAdjEnemy());

        // update phase
        Phase.getInstance().setActionResult(Action.Show_Next_Phase_Button);
        Phase.getInstance().update();

    }
}
