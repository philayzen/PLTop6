package pltop6.java;
public class From {
    private String from;

    public From(String tableName){
        from = tableName;
    }
    public From(){}

    public void join(String name, String tableName1, String tableName2, String join1, String join2){
        from = tableName1 + " join " + tableName2 + " as " + name + " on " + join1 + " = " + join2;
    }

    public String toString(){
        return from;
    }
}
