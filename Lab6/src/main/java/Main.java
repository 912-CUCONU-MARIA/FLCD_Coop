import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        try{
            Parser parser = new Parser("simpleGrammar.txt");
            parser.parse("aacbc");
            //parser.parse("aacbbc");
            //parser.parse("aacbcd");
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
