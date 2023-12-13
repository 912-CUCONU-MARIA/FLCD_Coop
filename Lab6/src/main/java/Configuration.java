import java.util.*;

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
                List<List<String>> productions = grammar.getProductions().get(nonterminal);
                if (productions != null && !productions.isEmpty()) {
                    List<String> firstProduction = new ArrayList<>();
                    firstProduction.addAll(productions.get(0));
                    //List<String> firstProduction = productions.get(0); // expand with the first production
                    workingStack.push(new Pair<>(nonterminal,1));
//                    for (int i = firstProduction.length() - 1; i >= 0; i--) {
//                        if(firstProduction.charAt(i) != ' ')
//                            inputStack.push(String.valueOf(firstProduction.charAt(i)));
//                    }
                    Collections.reverse(firstProduction);
                    for(String elem: firstProduction){
                        inputStack.push(elem);
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

    public void anotherTry() throws MoveException, GrammarException{
        if(!workingStack.isEmpty()){
            Pair<String,Integer> production = workingStack.peek();
            String headOfWorkingStack = production.getFirst();
            if(grammar.getNonterminals().contains(headOfWorkingStack)){
                Integer prodNumber = production.getSecond();

                List<List<String>> allProd = grammar.getProductionsForNonterminal(headOfWorkingStack);

                // still have a next production to move to
                if(prodNumber < allProd.size()){
                    state = State.NORMAL;
                    prodNumber++;
                    List<String> nextProd = new ArrayList<>();
                    nextProd.addAll(allProd.get(prodNumber-1));
                    //List<String> nextProd = allProd.get(prodNumber-1);
                    workingStack.pop();
                    workingStack.push(new Pair<>(headOfWorkingStack,prodNumber));
                    List<String> currentProd = allProd.get(prodNumber-2);
                    for(String elem: currentProd)
                        inputStack.pop();

                    Collections.reverse(nextProd);
                    for(String elem: nextProd)
                        inputStack.push(elem);
                }
                // reached last production for non-terminal, move back
                else if(prodNumber == allProd.size()){
                    state = State.BACK;
                    workingStack.pop();
                    inputStack.pop();
                    inputStack.push(headOfWorkingStack);
                }
                // error state
                else if(position == 0 && Objects.equals(headOfWorkingStack, "S")){
                    state = State.ERROR;
                    workingStack.pop();
                    inputStack.pop();
                    throw new MoveException("Entered error state with working stack " + workingStack + " and input stack " + inputStack);
                }
            }
        }
    }

    public int noTerminalsInWorkingStack(){
        int counter = 0;
        for(Pair<String,Integer> elem: workingStack){
            if(grammar.getTerminals().contains(elem.getFirst()))
                counter++;
        }
        return counter;
    }

    public void success(){
        if(state == State.NORMAL)
            if(position == noTerminalsInWorkingStack())
                if(inputStack.size() == 0){
                    state = State.FINAL;
                }
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
