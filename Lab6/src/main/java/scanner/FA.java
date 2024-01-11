package scanner;



import parser.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FA {

    private List<String> initialStates;
    private List<String> finalStates;
    private List<String> all_states;
    private HashMap<String, String> alphabet;
    private HashMap<Pair<String, String>,String> transitions;
    private String filename;

    public FA( String filename) {
        this.initialStates = new ArrayList<>();
        this.finalStates = new ArrayList<>();
        this.all_states = new ArrayList<>();
        this.alphabet = new HashMap<>();
        this.transitions = new HashMap<>();
        this.filename=filename;
        try {
            initFA();
        } catch (Exception e) {
            System.out.println("Error when initializingFA");
        }
    }

    private void initFA() {
        Path path = Paths.get(filename);
        try {
            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                if (line.startsWith("initial_states:")) {
                    String[] states = line.substring("initial_states:".length()).split(",");
                    Collections.addAll(initialStates, states);
                } else if (line.startsWith("final_states:")) {
                    String[] states = line.substring("final_states:".length()).split(",");
                    Collections.addAll(finalStates, states);
                } else if (line.startsWith("all_states:")) {
                    String[] states = line.substring("all_states:".length()).split(",");
                    Collections.addAll(all_states, states);
                } else if (line.startsWith("alphabet:")) {
                    String[] symbols = line.substring("alphabet:".length()).split(",");
                    for (String symbol : symbols) {
                        if ("digit".equals(symbol)) {
                            for (int i = 0; i <= 9; i++) {
                                //add if it doesnt exist
                                if(!alphabet.containsKey(String.valueOf(i)))
                                    alphabet.put(String.valueOf(i),String.valueOf(i));
                            }
                        }
                        else if ("non_zero_digit".equals(symbol)) {
                            for (int i = 0; i <= 9; i++) {
                                //add if it doesnt exist
                                if(!alphabet.containsKey(String.valueOf(i)))
                                    alphabet.put(String.valueOf(i),String.valueOf(i));
                            }
                        }
                        else if ("letter".equals(symbol)){
                            for (char letter = 'a'; letter <= 'z'; letter++) {
                                //add if it doesnt exist
                                if(!alphabet.containsKey(String.valueOf(letter)))
                                {
                                    alphabet.put(String.valueOf(letter), String.valueOf(letter));
                                    alphabet.put(String.valueOf((char) (letter - 'a' + 'A')), String.valueOf((char) (letter - 'a' + 'A')));
                                }
                            }
                        }
                        else {
                            if(!alphabet.containsKey(symbol))
                                alphabet.put(symbol, symbol);
                        }
                    }
                } else if (line.startsWith("transitions:")) {
                    String[] trans = line.substring("transitions:".length()).split("\\),\\(");
                    for (String tran : trans) {
                        String[] parts = tran.replaceAll("[()]", "").split(",");
                        if (parts[1].equals("digit")){
                            for (int i = 0; i <= 9; i++) {
                                transitions.put(new Pair<>(parts[0],Integer.toString(i)), parts[2]);
                            }
                        }
                        else if(parts[1].equals("non_zero_digit"))
                        {
                            for (int i = 1; i <= 9; i++) {
                                transitions.put(new Pair<>(parts[0],Integer.toString(i)), parts[2]);
                            }
                        }
                        else if(parts[1].equals("letter"))
                        {
                            for (char letter = 'a'; letter <= 'z'; letter++) {
                                transitions.put(new Pair<>(parts[0],String.valueOf(letter)), parts[2]);
                                transitions.put(new Pair<>(parts[0],String.valueOf((char) (letter - 'a' + 'A'))), parts[2]);
                            }
                        }
                        else {
                            transitions.put(new Pair<>(parts[0],parts[1]), parts[2]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error when reading from file");
        }
    }

    public boolean checkAccepted(String word) {

        if (word.isEmpty()) {
            return initialStates.stream().anyMatch(finalStates::contains);
        }

        List<String> wordAsList = List.of(word.split(""));

        if (wordAsList.stream().anyMatch(w->!alphabet.containsValue(w)))
            return false;

        for (String currentState : initialStates) {
            for (String c : wordAsList) {
                if(transitions.containsKey(new Pair<>(currentState,c)))
                {
                    currentState=transitions.get(new Pair<>(currentState,c));
                }
                else
                    return false;
            }
            if(finalStates.contains(currentState))
                return true;
        }
        return false;
    }

    public List<String> getInitialStates() {
        return initialStates;
    }

    public List<String> getFinalStates() {
        return finalStates;
    }

    public List<String> getAll_states() {
        return all_states;
    }

    public void printAlphabet(){
        System.out.println(alphabet.values());
    }

    public void printTransitions(){

        for (Map.Entry<Pair<String, String>, String> entry : transitions.entrySet()) {
            Pair<String, String> key = entry.getKey();
            String value = entry.getValue();
            String formattedEntry = key.getFirst() + "->" + key.getSecond() + "->" + value;
            System.out.print(formattedEntry+"    ");
        }
    }
}
