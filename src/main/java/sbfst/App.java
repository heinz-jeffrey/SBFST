package sbfst;

import com.github.steveash.jopenfst.*;
import com.github.steveash.jopenfst.io.*;
import java.util.ArrayList;

/**
 * Main class.
 */
public class App {
  /*
  *****************************
  * IMPORTANT!!!! will need to change
  * synMonoid state labels to comma separated
  * list to accomodate 2+ digit state labels
  * add determinization/minimization
  *********************************
  */
    public static void main(String[] args) {
      String fileName = args[0];
      Convert.setRegexToSplitOn("\\s+");
      MutableFst originalFst = Convert.importFst(fileName);

      Fst synMonoid = Utils.getSM(originalFst);

      Convert.setUseSymbolIdsInText(true);
      Convert.export(synMonoid, fileName + "_syntactic_monoid");

      int period = Utils.isAperiodic(synMonoid);
      if (period == -1) {
        System.out.println("The input language is aperiodic.");
      } else {
        System.out.println("The input language is periodic with period "
          + period + ".");
      }

      // ArrayList<ArrayList<State>> SCCs = Utils.getSCCs(originalFst);
      // int counter = 0;
      // for (ArrayList<State> scc: SCCs){
      //   System.out.print("SCC " + counter + ": ");
      //   for (State state : scc){
      //     System.out.print(state.getId() + " ");
      //   }
      //   System.out.println();
      //   counter++;
      // }
      //
      // ArrayList<ArrayList<Integer>> ancestors = Utils.getAncestors(SCCs, originalFst);
      // counter = 0;
      // for (ArrayList<Integer> scc: ancestors){
      //   System.out.print("Ancestors of SCC " + counter + ": ");
      //   for (Integer i : scc){
      //     System.out.print(i + " ");
      //   }
      //   System.out.println();
      //   counter++;
      // }


    }
}
