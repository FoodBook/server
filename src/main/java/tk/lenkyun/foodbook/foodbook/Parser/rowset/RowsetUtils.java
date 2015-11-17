package tk.lenkyun.foodbook.foodbook.Parser.rowset;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lenkyun on 17/11/2558.
 */
public class RowsetUtils {
    public static String generateKeyQuery(String[] name){
        return String.join(", ", Arrays.asList(name));
    }

    public static String generateParamQuery(String[] name){
        ArrayList<String> strQuestion = new ArrayList<>();
        for(int i = 0; i < name.length; i++){
            strQuestion.add("?");
        }
        return String.join(", ", strQuestion);
    }

    public static String generateInsertQuery(String db, String[] name){
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append(db);
        builder.append(" (");

        builder.append(generateKeyQuery(name));
        builder.append(") VALUES (");

        builder.append(generateParamQuery(name));
        builder.append(")");

        return builder.toString();
    }
}
