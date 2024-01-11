package scanner;

import util.Pair;

import java.util.ArrayList;

public class HashTable<T> {

    private ArrayList<ArrayList<T>> buckets;
    private Integer size;

    public HashTable(Integer size) {
        this.size = size;
        this.buckets=new ArrayList<>();
        for(int i=0;i<size; i++)
        {
            this.buckets.add(new ArrayList<>());
        }
    }

    public Integer getSize() {
        return size;
    }

    private Integer hash(Integer key) {
        return Math.abs(key) % size;
    }

    public Integer hash(String key){
        int sum=0;
        for(int i=0; i<key.length();i++)
            sum+=key.charAt(i);
        return sum % size;
    }
    private Integer getHashValue(T key){
        if (key instanceof Integer) {
            return hash((Integer) key);
        } else if (key instanceof String) {
            return hash((String) key);
        }
        return -1;
    }

    public Pair<Integer, Integer> add(T key) throws Exception {
        int hashValue = getHashValue(key);
        if (!buckets.get(hashValue).contains(key)) {
            buckets.get(hashValue).add(key);
            return new Pair<>(hashValue,buckets.get(hashValue).indexOf(key));
        }
        throw new Exception("Key " + key + " is already in the table!");
    }

    public boolean contains(T key) {
        int hashValue = getHashValue(key);
        return buckets.get(hashValue).contains(key);
    }

    public Pair<Integer, Integer> getPosition(T key) {
        if (this.contains(key)) {
            int hashValue = getHashValue(key);
            return new Pair<>(hashValue, buckets.get(hashValue).indexOf(key));
        }
        return new Pair<>(-1, -1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buckets.size(); i++) {
            for (int j = 0; j < buckets.get(i).size(); j++) {
                String formattedString = String.format("%-30s -> (%-3d, %-3d)\n",
                        buckets.get(i).get(j),
                        i,
                        j);
                sb.append(formattedString);
            }
        }
        return sb.toString();
    }



}
