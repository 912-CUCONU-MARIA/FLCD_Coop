import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Parser {

    Grammar grammar;
    Configuration config;

    String logFileName = "transitionLogFile";
    ParsingTree parsingTree;

    public Parser(String grammarFile) throws GrammarException {
        this.grammar = new Grammar(grammarFile);
        this.config = new Configuration(this.grammar);
        this.parsingTree = new ParsingTree(this.grammar);
    }


    public boolean parse(String word) throws MoveException, GrammarException, IOException {

        FileWriter fileWriter = new FileWriter(logFileName);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        Configuration.State state = config.getState();
        while (state != Configuration.State.FINAL && state != Configuration.State.ERROR) {
            bufferedWriter.write(config.toString() + " ");
            if (state == Configuration.State.NORMAL) {
                if (config.getPosition() == word.length() && config.getInputStack().isEmpty()) {
                    config.success();
                    bufferedWriter.write("success\n");
                } else {
                    String headOfInputStack = config.getInputStack().peek();
                    if (grammar.getNonterminals().contains(headOfInputStack)) {
                        config.expand();
                        bufferedWriter.write("expand\n");
                    } else {
                        if (config.getPosition() <= word.length() - 1 && headOfInputStack.equals(String.valueOf(word.charAt(config.getPosition())))) {
                            config.advance();
                            bufferedWriter.write("advance\n");
                        } else {
                            config.momentaryInsuccess();
                            bufferedWriter.write("momentary insuccess\n");
                        }
                    }
                }
            } else {
                if (state == Configuration.State.BACK) {
                    String headOfWorkingStack = config.getWorkingStack().peek().getFirst();
                    if (grammar.getTerminals().contains(headOfWorkingStack)) {
                        config.back();
                        bufferedWriter.write("back\n");
                    } else {
                        config.anotherTry();
                        bufferedWriter.write("another try\n");
                    }
                }
            }
            state = config.getState();
        }
        bufferedWriter.write(config.toString() + " ");
        bufferedWriter.close();

        if (state == Configuration.State.FINAL) {
            System.out.println(word + " is accepted");

            System.out.println("Building parsing tree...");
            parsingTree.buildTree(config.getWorkingStack());

            System.out.println("Parsing tree in table form:");

            System.out.println("as it was built:");
            parsingTree.printTreeAsItWasBuilt();
            System.out.println("on levels:");
            parsingTree.printTreeOnLevels();
            System.out.println("visually:");
            parsingTree.printTreeVisually();
            return true;
        } else {
            System.out.println(word + " is not accepted");
            return false;
        }
    }


}
