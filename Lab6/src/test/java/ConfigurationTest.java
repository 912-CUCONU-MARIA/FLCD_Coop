import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {
    private Grammar grammar;
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        String fileName = "simpleGrammar.txt";
        grammar = new Grammar(fileName);
        configuration = new Configuration(grammar);
    }

    @Test
    public void testExpand() throws MoveException {
        String nonTerminal = grammar.getNonterminals().get(0);
        configuration.getInputStack().push(nonTerminal);
        configuration.expand();
        assertEquals("a", configuration.getInputStack().pop());
        assertEquals("S", configuration.getInputStack().pop());
        assertEquals("b", configuration.getInputStack().pop());
        assertEquals("S", configuration.getInputStack().pop());
        assertEquals(new Pair<>("S", 1), configuration.getWorkingStack().pop());
    }

    @Test(expected = MoveException.class)
    public void testExpandWithTerminal() throws MoveException {
        configuration.getInputStack().push("c");
        configuration.expand();
    }

    @Test
    public void testAdvance() throws MoveException {
        int initialPosition = configuration.getPosition();
        configuration.getInputStack().push("c");
        configuration.advance();
        assertEquals("c", configuration.getWorkingStack().peek().getFirst());
        assertTrue(configuration.getInputStack().isEmpty());
        assertEquals(initialPosition + 1, configuration.getPosition());
    }

    @Test(expected = MoveException.class)
    public void testAdvanceWithNonTerminal() throws MoveException {
        configuration.getInputStack().push("S");
        configuration.advance();
    }

    @Test
    public void testBack() throws MoveException {
        int initialPosition = configuration.getPosition();
        configuration.getWorkingStack().push(new Pair<>("c", 0));
        configuration.back();
        //'c' moved back to the input stack
        assertEquals("c", configuration.getInputStack().peek());
        assertEquals(initialPosition - 1, configuration.getPosition());
    }

    @Test(expected = MoveException.class)
    public void testBackWithNonTerminal() throws MoveException {
        configuration.getWorkingStack().push(new Pair<>("S", 1));
        configuration.back();
    }

    @Test
    public void testMomentaryInsuccess() throws MoveException {
        Configuration.State initialState = configuration.getState();
        assertEquals(initialState, Configuration.State.NORMAL);
        configuration.getInputStack().push("c");
        configuration.momentaryInsuccess();
        assertEquals(configuration.getState(), Configuration.State.BACK);
    }

    @Test(expected = MoveException.class)
    public void testMomentaryInsuccessWithNonTerminal() throws MoveException {
        configuration.getInputStack().push("S");
        configuration.momentaryInsuccess();
    }
}
