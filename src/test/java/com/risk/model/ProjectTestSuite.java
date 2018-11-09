package com.risk.model; /**
 * @program: risk
 * @description: This is the project test suite.
 * @author: Zhijing Ling
 * @create: 2018-11-08 20:42
 **/

import com.risk.validate.MapValidatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ContinentTest.class,
        ModelTest.class,
        CountryTest.class,
        PlayerTest.class,
        MapValidatorTest.class})

public class ProjectTestSuite {

}