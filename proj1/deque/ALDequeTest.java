package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

public class ALDequeTest {
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        ArrayDeque<Integer> buggy = new ArrayDeque<>();
        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);
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
                // addFirst
                if (L.size() > 0) {
                    L.addFirst(1);
                    buggy.addFirst(1);
                    //System.out.println("getLast: " + L.getLast());
                    assertEquals(L.size(), buggy.size());
                }
            } else if (operationNumber == 3) {
                //System.out.println("removeLast");
                if (L.size() > 0) {
                    int removedLast = L.removeLast();
                    int bRemovedLast = buggy.removeLast();
                    assertEquals(removedLast, bRemovedLast);
                    //System.out.println("removeLast: " + L.removeLast());
                }
            } else if (operationNumber == 4) {
                //System.out.println("removeFirst");
                if (L.size() > 0) {
                    int removedFirst = L.removeFirst();
                    int bRemoved = buggy.removeFirst();

                    assertEquals(removedFirst, bRemoved);
                    //System.out.println("removeLast: " + L.removeLast());
                }
            }
        }
    }
}
