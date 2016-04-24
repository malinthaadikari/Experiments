package solution.longestrecurringpattern;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by malintha on 4/10/16.
 */
public class Utils {

    public static char[] getCharArrayFromDataFile(String fileName) throws IOException {
        return appendUniqueString(FileUtils.readFileToString(new File(fileName)).toCharArray());
    }

    public static char[] appendUniqueString(char[] dataArr){
        char[] finalDataArray= new char[dataArr.length+1];
        for (int i = 0; i < dataArr.length; i++) {
            finalDataArray[i] = dataArr[i];
        }
        finalDataArray[dataArr.length] = '$';
        return finalDataArray;
    }

}
