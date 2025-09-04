package pltop6.java;
public class Where {
    private String where;

    public Where(){
        where = " TRUE ";
    }
    public Where(String columnName, String value){
        where = " " + columnName + " = '" + value + "' ";
    }
    public Where(String columnName, int value){

        where = " " + columnName + " = " + value + " ";
    }

    public void addEquals(String columnName, String value){
        where += " AND " + columnName + " = '" + value + "' ";
    }
    public void addEquals(String columnName, int value){
        where += " AND " + columnName + " = " + value + " ";
    }
    @Override
    public String toString(){
        return where;
    }
}
