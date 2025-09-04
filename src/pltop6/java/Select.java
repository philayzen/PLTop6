package pltop6.java;
public class Select {
    private String select;

    public Select(){
        select = "*";
    }
    public Select(String columnName){
        select = columnName;
    }
    public Select(String[] columnNames){
        select = columnNames[0];
        for(int i = 1; i < columnNames.length; i++){
            select += "," + columnNames;
        }
    }
    @Override
    public String toString(){
        return select;
    }
}
