package test.db;

import org.junit.Before;
import org.junit.Test;
import test.utils.ArgumentsParser;
import test.domain.Result;
import test.domain.Action;
import test.domain.action.strategies.ActionStrategy;

import java.util.Optional;

import static org.junit.Assert.*;

public class DBManagerTest {

    @Before
    public void createTables() {
        DBManager dbManager = DBManager.getDBManager();
        dbManager.dropTables();
        dbManager.creteTables();
    }

    @Test
    public void shouldReturnErrorWhenTwoProducts() {
        Optional<ActionStrategy> actionStrategy1 = ArgumentsParser.parse("NEWPRODUCT iphone");
        Result result1 = new Action(actionStrategy1.get()).process();

        Optional<ActionStrategy> actionStrategy2 = ArgumentsParser.parse("NEWPRODUCT iphone");
        Result result2 = new Action(actionStrategy2.get()).process();

        assertEquals(new Result("OK"), result1);
        assertEquals(new Result("ERROR"), result2);
    }

    @Test
    public void shouldReturnErrorWhenNoProducts() {

        Optional<ActionStrategy> actionStrategy = ArgumentsParser.parse("PURCHASE iphone 1 1000 01.01.2017");
        Result result = new Action(actionStrategy.get()).process();

        assertEquals(new Result("ERROR"), result);
    }

    @Test
    public void shouldReturnErrorWhenWrongPurchaseDate() {
        Optional<ActionStrategy> actionStrategy1 = ArgumentsParser.parse("NEWPRODUCT iphone");
        Action action1 = new Action(actionStrategy1.get());
        action1.process();

        Optional<ActionStrategy> actionStrategy2 = ArgumentsParser.parse("PURCHASE iphone 1 1000 01.01.2017");
        Action action2 = new Action(actionStrategy2.get());
        action2.process();

        Optional<ActionStrategy> actionStrategy3 = ArgumentsParser.parse("PURCHASE iphone 1 1000 01.01.2000");
        Action action3 = new Action(actionStrategy3.get());
        Result result = action3.process();

        assertEquals(new Result("ERROR"), result);
    }

    @Test
    public void shouldReturnErrorWhenAmountGreaterThanExisted() {
        Optional<ActionStrategy> actionStrategy1 = ArgumentsParser.parse("NEWPRODUCT iphone");
        Action action1 = new Action(actionStrategy1.get());
        action1.process();

        Optional<ActionStrategy> actionStrategy2 = ArgumentsParser.parse("PURCHASE iphone 1 1000 01.01.2017");
        Action action2 = new Action(actionStrategy2.get());
        action2.process();

        Optional<ActionStrategy> actionStrategy3 = ArgumentsParser.parse("DEMAND iphone 1 1000 01.02.2020");
        Action action3 = new Action(actionStrategy3.get());
        action3.process();

        Optional<ActionStrategy> actionStrategy4 = ArgumentsParser.parse("DEMAND iphone 1 1000 01.02.2020");
        Action action4 = new Action(actionStrategy4.get());
        Result result = action4.process();

        assertEquals(new Result("ERROR"), result);
    }

    @Test
    public void shouldReturnCorrectResultWhenDemandAbsent() {
        Optional<ActionStrategy> actionStrategy1 = ArgumentsParser.parse("NEWPRODUCT iphone");
        Action action1 = new Action(actionStrategy1.get());
        action1.process();

        Optional<ActionStrategy> actionStrategy2 = ArgumentsParser.parse("PURCHASE iphone 1 1000 01.01.2017");
        Action action2 = new Action(actionStrategy2.get());
        action2.process();

        Optional<ActionStrategy> actionStrategy3 = ArgumentsParser.parse("SALESREPORT iphone 02.12.2016");
        Action action3 = new Action(actionStrategy3.get());
        Result result = action3.process();

        assertEquals(new Result("0.0"), result);
    }

    @Test
    public void shouldReturnCorrectResult() {
        Optional<ActionStrategy> actionStrategy1 = ArgumentsParser.parse("NEWPRODUCT iphone");
        Action action1 = new Action(actionStrategy1.get());
        action1.process();

        Optional<ActionStrategy> actionStrategy2 = ArgumentsParser.parse("PURCHASE iphone 1 1000 01.01.2017");
        Action action2 = new Action(actionStrategy2.get());
        action2.process();

        Optional<ActionStrategy> actionStrategy3 = ArgumentsParser.parse("PURCHASE iphone 2 2000 01.01.2017");
        Action action3 = new Action(actionStrategy3.get());
        action3.process();

        Optional<ActionStrategy> actionStrategy4 = ArgumentsParser.parse("DEMAND iphone 2 5000 01.03.2017");
        Action action4 = new Action(actionStrategy4.get());
        action4.process();

        Optional<ActionStrategy> actionStrategy5 = ArgumentsParser.parse("DEMAND iphone 1 5000 01.03.2017");
        Action action5 = new Action(actionStrategy5.get());
        action5.process();

        Optional<ActionStrategy> actionStrategy6 = ArgumentsParser.parse("SALESREPORT iphone 01.03.2017");
        Action action6 = new Action(actionStrategy6.get());
        Result result = action6.process();

        assertEquals(new Result("10000.0"), result);
    }
}