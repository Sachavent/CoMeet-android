package today.comeet.android.comeet.helper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Annick on 06/01/2017.
 */

public class ConverterHelper {

    public ConverterHelper() {}

    public  String strSeparator = ",";
    public  String convertArrayToString(ArrayList<String> array){
        String str = "";
        for (int i = 0;i<array.size(); i++) {
            str = str+array.get(i);
            // Do not append comma at the end of last element
            if(i<(array.size())-1){
                str = str+strSeparator;
            }
        }
        return str;
    }
    public  ArrayList<String> convertStringToArray(String str){
        ArrayList<String>  arr = new ArrayList<>( Arrays.asList(str.split(strSeparator)));
        return arr;
    }
}
