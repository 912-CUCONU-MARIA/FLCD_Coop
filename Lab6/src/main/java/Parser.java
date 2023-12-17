import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

public class Parser {

    Grammar grammar;
    Configuration config;

    String logFileName = "transitionLogFile";

    public Parser(String grammarFile) throws GrammarException {
        this.grammar = new Grammar(grammarFile);
        this.config = new Configuration(this.grammar);
    }


    public void parse(String word) throws MoveException, GrammarException, IOException {

        FileWriter fileWriter = new FileWriter(logFileName);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        Configuration.State state = config.getState();
        while(state != Configuration.State.FINAL && state != Configuration.State.ERROR){
            if(state == Configuration.State.NORMAL){
                if(config.getPosition() == word.length() && config.getInputStack().isEmpty()){
                    config.success();
                    bufferedWriter.write("success\n");
                }
                else{
                    String headOfInputStack = config.getInputStack().peek();
                    if(grammar.getNonterminals().contains(headOfInputStack)){
                        config.expand();
                        bufferedWriter.write("expand\n");
                    }
                    else{
                        if(config.getPosition() <= word.length()-1 && headOfInputStack.equals(String.valueOf(word.charAt(config.getPosition())))){
                            config.advance();
                            bufferedWriter.write("advance\n");
                        }
                        else{
                            config.momentaryInsuccess();
                            bufferedWriter.write("momentary insuccess\n");
                        }
                    }
                }
            }
            else{
                if(state == Configuration.State.BACK){
                    String headOfWorkingStack = config.getWorkingStack().peek().getFirst();
                    if(grammar.getTerminals().contains(headOfWorkingStack)){
                        config.back();
                        bufferedWriter.write("back\n");
                    }
                    else {
                        config.anotherTry();
                        bufferedWriter.write("another try\n");
                    }
                }
            }
            state = config.getState();
        }
        bufferedWriter.close();
        System.out.println(word + " is accepted");
        //buildParsingTable(config.getWorkingStack());
    }

    public void buildParsingTable(Stack<Pair<String, Integer>> workingStack){

    }


}
