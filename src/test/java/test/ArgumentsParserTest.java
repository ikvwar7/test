package test;

import org.junit.Test;
import test.domain.action.strategies.ActionStrategy;
import test.utils.ArgumentsParser;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class ArgumentsParserTest {

    @Test
    public void shouldReturnEmptyWhenUnknownCommand() {
        String command = "pUrchase";

        Optional<ActionStrategy> actionStrategy = ArgumentsParser.parse(command);

        assertEquals(false, actionStrategy.isPresent());
    }

    @Test
    public void shouldReturnEmptyWhenWrongAmount() {
        String command = "DEMAND iphone one 1000 01-01-2017";

        Optional<ActionStrategy> actionStrategy = ArgumentsParser.parse(command);

        assertEquals(false, actionStrategy.isPresent());
    }

    @Test
    public void shouldReturnCorrectWhenWrongDateFormat() {
        String command = "DEMAND iphone 1 1000 01.01.2017";

        Optional<ActionStrategy> actionStrategy = ArgumentsParser.parse(command);

        assertEquals(true, actionStrategy.isPresent());
    }

    @Test
    public void shouldReturnCorrectWhenNegativeAmount() {
        String command = "DEMAND iphone -1 1000 01.01.2017";

        Optional<ActionStrategy> actionStrategy = ArgumentsParser.parse(command);

        assertEquals(false, actionStrategy.isPresent());
    }

    @Test
    public void shouldReturnCorrectResult() {
        String command = "DEMAND iphone 1 1000 01";

        Optional<ActionStrategy> actionStrategy = ArgumentsParser.parse(command);

        assertEquals(false, actionStrategy.isPresent());
    }
}