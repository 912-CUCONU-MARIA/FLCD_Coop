package scanner;

import util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Scanner {

    private final SymbolTable symbolTable = new SymbolTable(11);
    private final ArrayList<Pair<String, Pair<Integer, Integer>>> pif = new ArrayList<>();

    private String program;
    private final ArrayList<String> tokens = new ArrayList<>();

    private Integer index = 0;

    private Integer currentLine = 1;

    private final FA integerFA = new FA("file_integer.in.txt");
    private final FA identifierFA=new FA("file_identifier.in.txt");

    public Scanner() {
        try {
            readTokens();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readTokens() throws IOException {
        File file = new File("token.in.txt");
        BufferedReader br = Files.newBufferedReader(file.toPath());
        String line;
        while ((line = br.readLine()) != null) {
            tokens.add(line);
        }
        // Sort tokens by length in descending order
        tokens.sort((o1, o2) -> Integer.compare(o2.length(), o1.length()));
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void scanProgram(String programFileName) {
        try {
            Path file = Path.of(programFileName);
            setProgram(Files.readString(file));

            while (index < program.length()) {
                nextToken();
            }

            FileWriter fileWriter = new FileWriter("PIF" + programFileName.replace(".txt", ".out.txt"));
            for (var pair : pif) {
                String formattedString = String.format("%-15s -> (%-3d, %-3d)\n",
                        pair.getFirst(),
                        pair.getSecond().getFirst(),
                        pair.getSecond().getSecond());
                fileWriter.write(formattedString);
            }

            fileWriter.close();
            fileWriter = new FileWriter("ST" + programFileName.replace(".txt", ".out.txt"));
            fileWriter.write(symbolTable.toString());
            fileWriter.close();
            System.out.println("Lexically correct");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void nextToken() throws Exception {
        if (index == program.length()) {
            return;
        }

        // Skipping white spaces and new lines
        while (index < program.length() && Character.isWhitespace(program.charAt(index))) {
            if (program.charAt(index) == '\n') {
                 currentLine++;
            }
            index++;
        }
        if (treatStringConstant()) {
            return;
        }
        if (treatIntConstant()) {
            return;
        }
        if (treatFromTokenList()) {
            return;
        }
        if (treatIdentifier()) {
            return;
        }
        throw new ScannerException("Lexical error: invalid token " + program.charAt(index) + " at line " + currentLine);
    }

    private boolean treatIdentifier() throws Exception {

        int start = index;
        StringBuilder identifierCandidate = new StringBuilder();

        while (index < program.length() && !isSpaceOrNextToken()) {
            identifierCandidate.append(program.charAt(index));
            index++;
        }

        if (identifierCandidate.length() == 0) {
            return false; // No identifier found
        }

        String identifierString = identifierCandidate.toString();
        if (!identifierFA.checkAccepted(identifierString)) {
            index=start;
            return false;
        }

        String identifier = program.substring(start, index);

        Pair<Integer, Integer> sTPosition;
        if (!symbolTable.containsIdentifier(identifier)) {
            sTPosition = symbolTable.addIdentifier(identifier);
        } else {
            sTPosition = symbolTable.getPositionIdentifier(identifier);
        }
        pif.add(new Pair<>("identifier", sTPosition));

        return true;
    }

    private boolean treatStringConstant() throws Exception {
        if (program.charAt(index) != '"') {
            return false;
        }

        int start = index;
        index++;

        while (index < program.length() && program.charAt(index) != '"') {
            index++;
        }

        if (index == program.length()) {
            throw new ScannerException("Unclosed string literal starting at index " + start);
        }

        index++; // skip closing "

        String stringConstant = program.substring(start, index);

        Pair<Integer, Integer> sTPosition;
        if (!symbolTable.containsConstant(stringConstant)) {
            sTPosition = symbolTable.addConstant(stringConstant);
        } else {
            sTPosition = symbolTable.getPositionConstant(stringConstant);
        }
        pif.add(new Pair<>("constant", sTPosition));

        return true;
    }

    private boolean treatIntConstant() throws Exception {
        int start = index;
        StringBuilder intCandidate = new StringBuilder();

        if(program.charAt(index)=='-') {
            intCandidate.append(program.charAt(index));
            index++;
        }

        while (index < program.length() && !isSpaceOrNextToken()) {
            intCandidate.append(program.charAt(index));
            index++;
        }

        if (intCandidate.length() == 0) {
            return false; // No integer found
        }

        String intConstantString = intCandidate.toString();
        if (!integerFA.checkAccepted(intConstantString)) {
            index=start;
            return false;
        }

        String intConstant = program.substring(start , index);

        Pair<Integer, Integer> sTPosition;
        if (!symbolTable.containsConstant(intConstant)) {
            sTPosition = symbolTable.addConstant(intConstant);
        } else {
            sTPosition = symbolTable.getPositionConstant(intConstant);
        }
        pif.add(new Pair<>("constant", sTPosition));

        return true;
    }

    private boolean isSpaceOrNextToken() {
        if (Character.isWhitespace(program.charAt(index))) {
            return true;
        }

        for (String token : tokens) {
            if (program.startsWith(token, index)) {
                return true;
            }
        }
        return false;
    }

    private boolean treatFromTokenList() {
        for (String token : tokens) {
            if (program.startsWith(token, index)) {
                index += token.length();

                pif.add(new Pair<>(token, new Pair<>(-1, -1)));

                return true;
            }
        }
        return false;
    }

    public String getSequenceFromPif(){
        String sequence = "";
        for(var pair: pif){
            sequence = sequence + " " + pair.getFirst();
        }
        return sequence.substring(1);
    }

}
