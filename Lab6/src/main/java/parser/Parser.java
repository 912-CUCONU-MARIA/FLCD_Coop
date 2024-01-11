package parser;

import parser.Configuration;
import parser.Grammar;
import parser.GrammarException;
import parser.MoveException;
import scanner.Scanner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Parser {

    Grammar grammar;
    Configuration config;

    String logFileName = "transitionLogFile";
    ParsingTree parsingTree;

    String outputFile;

    public Parser(String grammarFile, String outputFile) throws GrammarException {
        this.grammar = new Grammar(grammarFile);
        this.config = new Configuration(this.grammar);
        this.parsingTree = new ParsingTree(this.grammar);
        this.outputFile = outputFile;
    }


    private boolean checkHeadOfInputEqualsCurrentPositionInSequence(String word, String headOfInputStack){
        String[] tokens = word.split(" ");
        if(tokens.length == 1){
            if(config.getPosition() <= word.length()-1 &&
                    headOfInputStack.equals(String.valueOf(word.charAt(config.getPosition())))){
                return true;
            }
        }
        else if(tokens.length > 1){
            if(config.getPosition() <= word.length()-1 &&
                    headOfInputStack.equals(tokens[config.getPosition()])){
                return true;
            }
        }
        return false;
    }

    private boolean checkIfSuccess(String word){
        String[] tokens = word.split(" ");
        if(tokens.length == 1) {
            if (config.getPosition() == word.length() && config.getInputStack().isEmpty()) {
                return true;
            }
        }
        else if(tokens.length > 1) {
            if (config.getPosition() == tokens.length && config.getInputStack().isEmpty()) {
                return true;
            }
        }
        return false;
    }


    public boolean parse(String word) throws MoveException, GrammarException, IOException {
        try(FileWriter fileWriter = new FileWriter(logFileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)){

            Configuration.State state = config.getState();
            while (state != Configuration.State.FINAL && state != Configuration.State.ERROR) {
                bufferedWriter.write(config.toString() + " ");
                if (state == Configuration.State.NORMAL) {
                    if (checkIfSuccess(word)) {
                        config.success();
                        bufferedWriter.write("success\n");
                    } else {
                        if(config.getInputStack().isEmpty()){
                            break;
                        }
                        else {
                            String headOfInputStack = config.getInputStack().peek();
                            if (grammar.getNonterminals().contains(headOfInputStack)) {
                                config.expand();
                                bufferedWriter.write("expand\n");
                            } else {
                                if(checkHeadOfInputEqualsCurrentPositionInSequence(word, headOfInputStack)){
                                    config.advance();
                                    bufferedWriter.write("advance\n");
                                }
                                else {
                                    config.momentaryInsuccess();
                                    bufferedWriter.write("momentary insuccess\n");
                                }
                            }
                        }
                    }
                } else {
                    if (state == Configuration.State.BACK) {
                        if(config.getWorkingStack().isEmpty()){
                            break;
                        }
                        else {
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
                }
                state = config.getState();
            }
            bufferedWriter.write(config.toString() + " ");
            bufferedWriter.close();

            if (state == Configuration.State.FINAL) {
                System.out.println(word + " is accepted");

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                    writer.write(word + " is accepted\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

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

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                    writer.write(word + " is not accepted\n");
                }
                return false;
            }

        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Configuration getConfig(){
        return config;
    }


}
