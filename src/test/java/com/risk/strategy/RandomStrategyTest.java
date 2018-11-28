package com.risk.strategy;

import com.risk.common.Action;
import com.risk.model.Continent;
import com.risk.model.Country;
import com.risk.model.Phase;
import com.risk.model.Player;
import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class RandomStrategyTest {

    public static Continent asia;
    public static Country china;
    public static Country thailand;
    public static Country singapore;

    public static Continent northAmerica;
    public static Country canada;
    public static Country usa;

    private Player player;
    private Player defender;

    @SuppressWarnings("Duplicates")
    @BeforeClass
    public static void before() throws Exception {
        asia = new Continent("Asia", 5);
        china = new Country("China", asia);
        thailand = new Country("Thailand", asia);
        singapore = new Country("Singapore", asia);

        china.addEdge(thailand);
        china.addEdge(singapore);
        thailand.addEdge(china);
        singapore.addEdge(china);

        asia.addCountry(china);
        asia.addCountry(thailand);
        asia.addCountry(singapore);

        china.setContinent(asia);
        thailand.setContinent(asia);
        singapore.setContinent(asia);

        northAmerica = new Continent("NorthAmerica", 6);

        canada = new Country("canada", northAmerica);
        usa = new Country("usa", northAmerica);

        canada.addEdge(usa);
        usa.addEdge(canada);

        northAmerica.addCountry(canada);
        northAmerica.addCountry(usa);

        canada.setContinent(northAmerica);
        usa.setContinent(northAmerica);
    }

    @SuppressWarnings("Duplicates")
    @Before
    public void setUp() throws Exception {
        player = new Player("Ann", 3, "random");

        ArrayList<Country> countries = new ArrayList<>();
        singapore.setPlayer(player);
        singapore.setArmies(1);
        player.addCountry(singapore);
        canada.setPlayer(player);
        canada.setArmies(1);
        player.addCountry(canada);
        usa.setPlayer(player);
        usa.setArmies(8);
        player.addCountry(usa);
        player.setTotalStrength(10);

        defender = new Player("Mike", 2, "random");
        defender.setTotalStrength(5);
        china.setPlayer(defender);
        china.setArmies(4);
        defender.addCountry(china);
        thailand.setPlayer(defender);
        thailand.setArmies(1);
        defender.addCountry(thailand);
    }

    @Test
    public void reinforcement() {
        // original totalStrength = 5
        // china = 4, thailand = 1
        // addedArmies = 3
        defender.reinforcement();
        assertEquals(7, china.getArmies());
        assertEquals(1, thailand.getArmies());
        assertEquals(0, defender.getArmies());
        assertEquals(8, defender.getTotalStrength());
        Assert.assertEquals(Action.Show_Next_Phase_Button, Phase.getInstance().getActionResult());
    }

    @Test
    public void attack() {
    }

    @Test
    public void moveArmy() {
    }

    @Test
    public void fortification() {
    }
}