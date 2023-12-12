import java.util.List;
import java.util.Stack;

public class Configuration {

    enum State {
        NORMAL, BACK, FINAL, ERROR
    }

    private State state;
    private int position;
    private Stack<Pair<String, Integer>> workingStack;
    private Stack<String> inputStack;
    private Grammar grammar;

    public Configuration(Grammar grammar) {
        this.state=State.NORMAL;
        this.grammar = grammar;
        this.workingStack = new Stack<>();
        this.inputStack = new Stack<>();
        this.position = 0;
    }

    public void advance() throws MoveException {
        if (!inputStack.isEmpty()) {
            String headOfInputStack = inputStack.peek();

            if (grammar.getTerminals().contains((headOfInputStack))) {
                inputStack.pop();
                workingStack.push(new Pair<>(headOfInputStack,0));
                position++;
                return;
            }
        }
        throw new MoveException("Head of input stack is not a terminal");
    }

    public void expand() throws MoveException {
        if (!inputStack.isEmpty()) {
            String nonterminal = inputStack.peek();
            if (grammar.getNonterminals().contains(nonterminal)) {
                inputStack.pop();
                List<String> productions = grammar.getProductions().get(nonterminal);
                if (productions != null && !productions.isEmpty()) {
                    String firstProduction = productions.get(0); // expand with the first production
                    workingStack.push(new Pair<>(nonterminal,1));
                    for (int i = firstProduction.length() - 1; i >= 0; i--) {
                        if(firstProduction.charAt(i) != ' ')
                            inputStack.push(String.valueOf(firstProduction.charAt(i)));
                    }
                }
                return;
            }
        }
        throw new MoveException("Head of input stack is not a nonterminal");

    }

    public void momentaryInsuccess() throws MoveException {
        if (!inputStack.isEmpty()) {
            String headOfInputStack = inputStack.peek();
            if (grammar.getTerminals().contains((headOfInputStack))) {
                state = State.BACK;
                return;
            }
        }
        throw new MoveException("Head of input stack is not a terminal");
    }

    public void back() throws MoveException {
        if (!workingStack.isEmpty()) {
            String headOfWorkingStack = workingStack.peek().getFirst();
            if (grammar.getTerminals().contains((headOfWorkingStack))) {
                String terminal = workingStack.pop().getFirst();
                inputStack.push(terminal);
                position--;
                return;
            }
        }
        throw new MoveException("Head of working stack is not a terminal");

    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Stack<Pair<String, Integer>> getWorkingStack() {
        return workingStack;
    }

    public void setWorkingStack(Stack<Pair<String, Integer>> workingStack) {
        this.workingStack = workingStack;
    }

    public Stack<String> getInputStack() {
        return inputStack;
    }

    public void setInputStack(Stack<String> inputStack) {
        this.inputStack = inputStack;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
    }
}
