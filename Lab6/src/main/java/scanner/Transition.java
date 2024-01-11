package scanner;

public class Transition {

    private String from;
    private String to;
    private String by;

    public Transition(String from, String to, String by) {
        this.from = from;
        this.to = to;
        this.by = by;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    @Override
    public String toString() {
        return from+"->"+by+"->"+to;
    }
}
