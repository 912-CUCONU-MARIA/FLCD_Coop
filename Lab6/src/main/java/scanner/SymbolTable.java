package scanner;

import util.Pair;

public class SymbolTable {

    private Integer size;
    private HashTable<String> identifiers;
    private HashTable<String> constants;
    public SymbolTable(Integer size) {
        this.size = size;
        this.identifiers=new HashTable<>(size);
        this.constants =new HashTable<>(size);
    }

    public Pair<Integer, Integer> addIdentifier(String identifier) throws Exception {
        return identifiers.add(identifier);
    }

    public Pair<Integer, Integer> addConstant(String constant) throws Exception {
        return constants.add(constant);
    }

    public Pair<Integer, Integer> getPositionIdentifier(String name) {
        return identifiers.getPosition(name);
    }

    public Pair<Integer, Integer> getPositionConstant(String constant) {
        return constants.getPosition(constant);
    }

    public boolean containsIdentifier(String name) {
        return identifiers.contains(name);
    }

    public boolean containsConstant(String constant) {
        return constants.contains(constant);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("identifiers\n");
        sb.append("-----------------------------------------\n");
        sb.append(identifiers.toString());

        sb.append("\nconstants\n");
        sb.append("-----------------------------------------\n");
        sb.append(constants.toString());

        return sb.toString();
    }


}
