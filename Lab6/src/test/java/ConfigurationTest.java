import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.Optional;

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

    @Test
    public void testAnotherTry() throws MoveException, GrammarException{
        configuration.getInputStack().push("S");

        configuration.expand();
        configuration.advance();
        configuration.expand();
        configuration.advance();
        configuration.expand();
        configuration.momentaryInsuccess();

        Configuration.State currentState = configuration.getState();
        assertEquals(currentState, Configuration.State.BACK);

        int currentPosition = configuration.getPosition();
        assertEquals(currentPosition, 2);

        Pair<String,Integer> currentHeadOfWorkingStack = configuration.getWorkingStack().peek();
        assertEquals(currentHeadOfWorkingStack.getFirst(),"S");
        assertEquals(currentHeadOfWorkingStack.getSecond(),(Integer)1);

        List<String> currentProdForNonTerminal = grammar.getProductionsForNonterminal("S").get(0);

        int counter = configuration.getInputStack().size()-1;
        for(String elem: currentProdForNonTerminal){
            String currentHeadOfInputStack = configuration.getInputStack().get(counter);
            assertEquals(currentHeadOfInputStack, elem);
            counter--;
        }

        configuration.anotherTry();

        currentState = configuration.getState();
        assertEquals(currentState, Configuration.State.NORMAL);

        currentPosition = configuration.getPosition();
        assertEquals(currentPosition,2);

        currentHeadOfWorkingStack = configuration.getWorkingStack().peek();
        assertEquals(currentHeadOfWorkingStack.getFirst(),"S");
        assertEquals(currentHeadOfWorkingStack.getSecond(),(Integer)2);

        currentProdForNonTerminal = grammar.getProductionsForNonterminal("S").get(1);
        counter = configuration.getInputStack().size()-1;
        for(String elem: currentProdForNonTerminal){
            String currentHeadOfInputStack = configuration.getInputStack().get(counter);
            assertEquals(currentHeadOfInputStack, elem);
            counter--;
        }
    }

    @Test
    public void anotherTryLastProductionReached() throws MoveException, GrammarException{
        configuration.getInputStack().push("S");

        configuration.expand();
        configuration.advance();
        configuration.expand();
        configuration.advance();
        configuration.expand();
        configuration.momentaryInsuccess();
        configuration.anotherTry();
        configuration.momentaryInsuccess();
        configuration.anotherTry();
        configuration.advance();
        configuration.advance();
        configuration.expand();
        configuration.momentaryInsuccess();
        configuration.anotherTry();
        configuration.momentaryInsuccess();
        configuration.anotherTry();
        configuration.advance();
        configuration.momentaryInsuccess();
        configuration.back();

        Configuration.State state = configuration.getState();
        assertEquals(state,Configuration.State.BACK);

        int position = configuration.getPosition();
        assertEquals(position, 4);

        Pair<String, Integer> headOfWorkingStack = configuration.getWorkingStack().peek();
        assertEquals(headOfWorkingStack.getFirst(),"S");
        assertEquals(headOfWorkingStack.getSecond(), (Integer) 3);


        List<String> currentProdForNonTerminal = grammar.getProductionsForNonterminal("S").get(headOfWorkingStack.getSecond()-1);

        int counter = configuration.getInputStack().size()-1;
        for(String elem: currentProdForNonTerminal){
            String currentHeadOfInputStack = configuration.getInputStack().get(counter);
            assertEquals(currentHeadOfInputStack, elem);
            counter--;
        }

        configuration.anotherTry();


        state = configuration.getState();
        assertEquals(state,Configuration.State.BACK);

        position = configuration.getPosition();
        assertEquals(position, 4);

        headOfWorkingStack = configuration.getWorkingStack().peek();
        assertEquals(headOfWorkingStack.getFirst(),"b");

        String currentHeadOfInputStack = configuration.getInputStack().peek();
        assertEquals(currentHeadOfInputStack,"S");
    }


//    @Test(expected = MoveException.class)
//    public void anotherTryWithException()
//    {
//
//    }


    @Test
    public void success() throws MoveException, GrammarException
    {
        configuration.getInputStack().push("S");

        configuration.expand();
        configuration.advance();
        configuration.expand();
        configuration.advance();
        configuration.expand();
        configuration.momentaryInsuccess();
        configuration.anotherTry();
        configuration.momentaryInsuccess();
        configuration.anotherTry();
        configuration.advance();
        configuration.advance();
        configuration.expand();
        configuration.momentaryInsuccess();
        configuration.anotherTry();
        configuration.momentaryInsuccess();
        configuration.anotherTry();
        configuration.advance();
        configuration.momentaryInsuccess();
        configuration.back();
        configuration.anotherTry();
        configuration.back();
        configuration.back();
        configuration.anotherTry();
        configuration.back();
        configuration.anotherTry();
        configuration.advance();
        configuration.expand();
        configuration.momentaryInsuccess();
        configuration.anotherTry();
        configuration.momentaryInsuccess();
        configuration.anotherTry();
        configuration.advance();
        configuration.advance();
        configuration.expand();
        configuration.momentaryInsuccess();
        configuration.anotherTry();
        configuration.momentaryInsuccess();
        configuration.anotherTry();
        configuration.advance();



        Configuration.State state = configuration.getState();
        assertEquals(state,Configuration.State.NORMAL);

        int position = configuration.getPosition();
        assertEquals(position, 5);

        assertEquals(configuration.getInputStack().size(), 0);


        configuration.success();


        state = configuration.getState();
        assertEquals(state,Configuration.State.FINAL);

        position = configuration.getPosition();
        assertEquals(position, 5);

        assertEquals(configuration.getInputStack().size(), 0);


    }
}
