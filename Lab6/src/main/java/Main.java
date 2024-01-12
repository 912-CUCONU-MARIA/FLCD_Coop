import parser.Parser;
import scanner.Scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try{
            //Parser parser = new Parser("simpleGrammar.txt", "out1.txt");
            //parser.parse("aacbc");
            //parser.parse("aacbbc");
            //parser.parse("aacbcd");


            Scanner scanner = new Scanner();
            scanner.scanProgram("test.txt");
            String sequence = scanner.getSequenceFromPif();
            Parser parser2 = new Parser("syntaxRules_Mara.txt", "out2.txt");
            parser2.parse(sequence);
            System.out.println(parser2.getConfig().getGrammar().getTerminals());
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
