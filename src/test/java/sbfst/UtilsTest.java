package sbfst;

import com.github.steveash.jopenfst.*;
import com.github.steveash.jopenfst.io.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

/**
 * Unit tests for sbfst.Utils.java.
 */
public class UtilsTest {

    Fst fig3A;
    Set<State> fig3AQ1;
    Set<State> fig3AQ2;

    Fst acyclic1;

    Fst fig1M2;
    Set<State> fig1M2Q1;
    Set<State> fig1M2Q2;

    Fst fig1M1;
    Set<State> fig1M1Q1;
    Set<State> fig1M1Q2;

    Fst fig1M1NoSyms;
    Set<State> fig1M1Q1NoSyms;
    Set<State> fig1M1Q2NoSyms;

    Fst fig2M2_altered;

    /**
     * Run before each test case to initialize the testing environment.
     */
    @Before
    public void initialize() {
        Convert.setRegexToSplitOn("\\s+");
        fig3A = Convert.importFst("test_pairgraph_1");
        fig3AQ1 = new HashSet<>();
        fig3AQ1.add(fig3A.getState("1"));
        fig3AQ1.add(fig3A.getState("2"));
        fig3AQ1.add(fig3A.getState("3"));
        fig3AQ1.add(fig3A.getState("4"));
        fig3AQ1.add(fig3A.getState("5"));
        fig3AQ2 = new HashSet<>();
        fig3AQ2.add(fig3A.getState("3"));
        fig3AQ2.add(fig3A.getState("4"));
        fig3AQ2.add(fig3A.getState("5"));
        acyclic1 = Convert.importFst("test_acyclic_1");
        fig1M2 = Convert.importFst("fig1M2");
        fig1M2Q1 = new HashSet<>();
        fig1M2Q1.add(fig1M2.getState(0));
        fig1M2Q1.add(fig1M2.getState(1));
        fig1M2Q2 = new HashSet<>();
        fig1M2Q2.add(fig1M2.getState(0));
        fig1M2Q2.add(fig1M2.getState(1));
        fig1M1 = Convert.importFst("fig1M1");
        fig1M1Q1 = new HashSet<>();
        fig1M1Q1.add(fig1M1.getState(0));
        fig1M1Q1.add(fig1M1.getState(1));
        fig1M1Q1.add(fig1M1.getState(2));
        fig1M1Q2 = new HashSet<>();
        fig1M1Q1.add(fig1M1.getState(0));
        fig1M1Q1.add(fig1M1.getState(1));
        fig1M1Q1.add(fig1M1.getState(2));
        fig1M1NoSyms = Convert.importFst("fig1M1_no_syms");
        fig1M1Q1NoSyms = new HashSet<>();
        fig1M1Q1NoSyms.add(fig1M1NoSyms.getState(0));
        fig1M1Q1NoSyms.add(fig1M1NoSyms.getState(1));
        fig1M1Q1NoSyms.add(fig1M1NoSyms.getState(2));
        fig1M1Q2NoSyms = new HashSet<>();
        fig1M1Q2NoSyms.add(fig1M1NoSyms.getState(0));
        fig1M1Q2NoSyms.add(fig1M1NoSyms.getState(1));
        fig1M1Q2NoSyms.add(fig1M1NoSyms.getState(2));
        fig2M2_altered = Convert.importFst("fig2M2");
    }

    /**
     * Test that the pair graph has the correct number of states.
     */
    @Test
    public void testPairGraphStates()
    {
        // get the pair graph of the test dfa using the state subsets above
        Fst pairGraph = Utils.getPairGraph(fig3A, fig3AQ1, fig3AQ2);

        assertTrue( pairGraph.getStateCount() == 23 );
    }

    /**
     * Test that the delta_i transition function works.
     */
    @Test
    public void testDeltaI() {
        // test transition function delta_1
        assertTrue(Utils.deltaI(fig3A, fig3AQ1, "1", "a").equals("2"));
        assertTrue(Utils.deltaI(fig3A, fig3AQ1, "1", "b").equals(Utils.UNUSED_SYMBOL));
        assertTrue(Utils.deltaI(fig3A, fig3AQ1, "1", "c").equals(Utils.UNUSED_SYMBOL));

        // test transition function delta_2
        assertTrue(Utils.deltaI(fig3A, fig3AQ2, "3", "a").equals("4"));
        assertTrue(Utils.deltaI(fig3A, fig3AQ1, "3", "b").equals(Utils.UNUSED_SYMBOL));
        assertTrue(Utils.deltaI(fig3A, fig3AQ1, "3", "c").equals(Utils.UNUSED_SYMBOL));
    }

    /**
     * Test pair graph creation.
     */
    @Test
    public void testPairGraph() {
        // get the pair graph of the test dfa using the state subsets above
        Fst pairGraph = Utils.getPairGraph(fig3A, fig3AQ1, fig3AQ2);
        System.out.println(pairGraph);
    }

    /**
     * Test checking for cycles in graphs.
     */
    @Test
    public void testAcyclicChecking() {
        // this graph is cyclic, so isAcyclic should return false
        assertTrue(!Utils.isAcyclic(fig3A));

        // the pair graph is cyclic too
        Fst pairGraph = Utils.getPairGraph(fig3A, fig3AQ1, fig3AQ2);
        assertTrue(!Utils.isAcyclic(pairGraph));

        // this graph is acyclic, so isAcyclic should return true
        assertTrue(Utils.isAcyclic(acyclic1));
    }

    /**
     * Test checking if two components of a graph are pairwise s-local.
     */
    @Test
    public void testIsPairwiseSLocal() {
        // Fig. 3 in Kim, McNaughton, McCloskey 1991 (given components are not pairwise s-local)
        assertTrue(!Utils.isPairwiseSLocal(fig3A, fig3AQ1, fig3AQ2));

        // Fig. 1, M2 in Kim, McNaughton, McCloskey 1991 (given components are not pairwise s-local)
        assertTrue(!Utils.isPairwiseSLocal(fig1M2, fig1M2Q1, fig1M2Q2));

        // Fig. 1, M1 in Kim, McNaughton, McCloskey 1991 (give components are pairwise s-local)
        assertTrue(Utils.isPairwiseSLocal(fig1M1, fig1M1Q1, fig1M1Q2));
    }

    /**
     * Test hasDescendants().
     */
    @Test
    public void testHasDescendants() {
        ArrayList<State> scc = new ArrayList<>();
        scc.add(fig2M2_altered.getState(1));
        scc.add(fig2M2_altered.getState(2));
        assertTrue(!Utils.hasDescendants(scc, fig2M2_altered));

        scc.clear();
        scc.add(fig2M2_altered.getState(0));
        assertTrue(Utils.hasDescendants(scc, fig2M2_altered));
    }

    /**
     * Test componentIsReachable().
     */
    @Test
    public void testComponentIsReachable() {
        ArrayList<State> scc = new ArrayList<>();
        scc.add(fig3A.getState("1"));
        scc.add(fig3A.getState("2"));
        assertTrue(Utils.componentIsReachable(fig3A.getState("1"), scc, fig3A));
        assertTrue(Utils.componentIsReachable(fig3A.getState("2"), scc, fig3A));
        assertTrue(!Utils.componentIsReachable(fig3A.getState("3"), scc, fig3A));
        assertTrue(!Utils.componentIsReachable(fig3A.getState("4"), scc, fig3A));
        assertTrue(!Utils.componentIsReachable(fig3A.getState("5"), scc, fig3A));
    }

    /**
     * Test getM0().
     */
    @Test
    public void testGetM0() {
        ArrayList<State> scc = new ArrayList<>();
        scc.add(fig3A.getState("1"));
        scc.add(fig3A.getState("2"));
        assertTrue(Utils.getM0(scc, fig3A).equals(scc));

        scc.clear();
        scc.add(fig3A.getState("3"));
        scc.add(fig3A.getState("4"));
        scc.add(fig3A.getState("5"));
        ArrayList<State> m0 = new ArrayList<>();
        m0.add(fig3A.getState("1"));
        m0.add(fig3A.getState("2"));
        m0.add(fig3A.getState("3"));
        m0.add(fig3A.getState("4"));
        m0.add(fig3A.getState("5"));
        assertTrue(Utils.getM0(scc, fig3A).equals(m0));
    }

    /**
     * Test getAsteriskStates().
     */
    @Test
    public void testGetAsteriskStates() {
        Fst pairGraph = Utils.getPairGraph(fig3A, fig3AQ1, fig3AQ2);
        ArrayList<State> asteriskStates = Utils.getAsteriskStates(pairGraph);
        assertTrue(asteriskStates.contains(pairGraph.getState("1" + Utils.DELIMITER + Utils.UNUSED_SYMBOL)));
        assertTrue(asteriskStates.contains(pairGraph.getState("2" + Utils.DELIMITER + Utils.UNUSED_SYMBOL)));
        assertTrue(asteriskStates.contains(pairGraph.getState("3" + Utils.DELIMITER + Utils.UNUSED_SYMBOL)));
        assertTrue(asteriskStates.contains(pairGraph.getState("4" + Utils.DELIMITER + Utils.UNUSED_SYMBOL)));
        assertTrue(asteriskStates.contains(pairGraph.getState("5" + Utils.DELIMITER + Utils.UNUSED_SYMBOL)));
        assertTrue(asteriskStates.contains(pairGraph.getState(Utils.UNUSED_SYMBOL + Utils.DELIMITER + "3")));
        assertTrue(asteriskStates.contains(pairGraph.getState(Utils.UNUSED_SYMBOL + Utils.DELIMITER + "4")));
        assertTrue(asteriskStates.contains(pairGraph.getState(Utils.UNUSED_SYMBOL + Utils.DELIMITER + "5")));
    }

    @Test
    public void testTest() {
        Fst fig3A_copy = Convert.importFst("test_pairgraph_1");
        Set<State> fig3AQ1_copy = new HashSet<>();
        fig3AQ1_copy.add(fig3A_copy.getState("1"));
        fig3AQ1_copy.add(fig3A_copy.getState("2"));
        fig3AQ1_copy.add(fig3A_copy.getState("3"));
        fig3AQ1_copy.add(fig3A_copy.getState("4"));
        fig3AQ1_copy.add(fig3A_copy.getState("5"));
        Set<State> fig3AQ2_copy = new HashSet<>();
        fig3AQ2_copy.add(fig3A_copy.getState("3"));
        fig3AQ2_copy.add(fig3A_copy.getState("4"));
        fig3AQ2_copy.add(fig3A_copy.getState("5"));
        Fst pairGraph = Utils.getPairGraph(fig3A_copy, fig3AQ1_copy, fig3AQ2_copy);
        ((MutableFst) pairGraph).setStart(((MutableFst) fig3A_copy).getState(0));
        ArrayList<ArrayList<State>> SCCs = Utils.getSCCs(pairGraph);
        System.out.println(SCCs);
    }

    /**
     * Test isPathFromComponentToAsteriskState().
     */
    @Test
    public void testIsPathFromComponentToAsteriskState() {
        Fst pairGraph = Utils.getPairGraph(fig3A, fig3AQ1, fig3AQ2);
        ((MutableFst) pairGraph).setStart(((MutableFst) pairGraph).getState(0));
        ArrayList<State> comp = new ArrayList<>();
        comp.add(pairGraph.getState("1,3"));
        comp.add(pairGraph.getState("2,4"));
        assertTrue(!Utils.isPathFromComponentToAsteriskState(comp, pairGraph));

        comp.clear();
        comp.add(pairGraph.getState("5,3"));
        assertTrue(Utils.isPathFromComponentToAsteriskState(comp, pairGraph));

        comp.clear();
        comp.add(pairGraph.getState("4,4"));
        assertTrue(!Utils.isPathFromComponentToAsteriskState(comp, pairGraph));
    }

    /**
     * Test isLocallyTestable().
     */
    @Test
    public void testIsLocallyTestable() {
        // Fig. 1, M1 in Kim, McNaugton, McCloskey 1991 (is locally testable)
        assertTrue(Utils.isLocallyTestable(fig1M1));

        // Fig. 1, M2 in Kim, McNaughton, McCloskey 1991 (is NOT locally testable)
        assertTrue(!Utils.isLocallyTestable(fig1M2));
    }
}
