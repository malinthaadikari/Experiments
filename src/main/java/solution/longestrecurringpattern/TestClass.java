package solution.longestrecurringpattern;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by malintha on 4/10/16.
 */
public class TestClass {


    public static void main(String args[]) throws IOException {

        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the data file location");
        String location=sc.nextLine();

        char[] data = Utils.getCharArrayFromDataFile(location);
        SuffixTree suffixtree = new SuffixTree(data);
        suffixtree.createSuffixTree();

        System.out.print("Longest recurrent patter is: ");
        SuffixTree.getLongestRecurringPattern(suffixtree, data);
    }

}
