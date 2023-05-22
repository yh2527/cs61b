package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */

public class TestBuggyAList {
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                buggy.addLast(randVal);
                //System.out.println("addLast(" + randVal + ")");
                assertEquals(L.size(), buggy.size());
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int buggySize = buggy.size();
                //System.out.println("size: " + size);
                assertEquals(size, buggySize);
            } else if (operationNumber == 2) {
                // getLast
                if (L.size() > 0) {
                    int last = L.getLast();
                    int bLast = buggy.getLast();
                    //System.out.println("getLast: " + L.getLast());
                    assertEquals(last, bLast);
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (L.size() > 0) {
                    int removed = L.removeLast();
                    int bRemoved = buggy.removeLast();
                    assertEquals(removed, bRemoved);
                    //System.out.println("removeLast: " + L.removeLast());
                }
            }
        }
    }

    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<String> correct = new AListNoResizing<>();
        BuggyAList<String> broken = new BuggyAList<>();
        correct.addLast("a");
        correct.addLast("b");
        correct.addLast("c");
        broken.addLast("a");
        broken.addLast("b");
        broken.addLast("c");
        assertEquals(correct.size(), broken.size());
        assertEquals(correct.removeLast(), broken.removeLast());
        assertEquals(correct.removeLast(), broken.removeLast());
        assertEquals(correct.removeLast(), broken.removeLast());
        //System.out.println(L1.getLast());
    }
}
