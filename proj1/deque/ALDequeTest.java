package deque;

import edu.princeton.cs.algs4.Stopwatch;
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
                    //System.out.println(buggy.getItemsLength());
                    //System.out.println(buggy.size());
                    //boolean resize = ((buggy.size() > 16) && ((buggy.size() / buggy.getItemsLength()) < 0.25));
                    //System.out.println(resize);
                    int removedLast = L.removeLast();
                    int bRemovedLast = buggy.removeLast();
                    assertEquals(removedLast, bRemovedLast);
                    //System.out.println("removeLast: " + L.removeLast());
                }
            } else if (operationNumber == 4) {
                //System.out.println("removeFirst");
                if (L.size() > 0) {
                    //System.out.println(buggy.getItemsLength());
                    //System.out.println(buggy.size());
                    //boolean resize = ((buggy.size() > 16) && ((buggy.size() / buggy.getItemsLength()) < 0.25));
                    //System.out.println(resize);
                    int removedFirst = L.removeFirst();
                    int bRemoved = buggy.removeFirst();

                    assertEquals(removedFirst, bRemoved);
                    //System.out.println("removeLast: " + L.removeLast());
                }
            }
        }
    }

/*
    private static void printTimingTable(ArrayDeque<Integer> Ns, ArrayDeque<Double> times, ArrayDeque<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeArrayDequeConstruction();
    }

    public static void timeArrayDequeConstruction() {
        ArrayDeque numOp = new ArrayDeque();
        ArrayDeque times = new ArrayDeque();
        numOp.addLast(1000);
        numOp.addLast(2000);
        numOp.addLast(4000);
        numOp.addLast(8000);
        numOp.addLast(16000);
        numOp.addLast(32000);
        numOp.addLast(64000);
        numOp.addLast(128000);
        numOp.addLast(256000);
        //System.out.println(numOp.get(3));
        for (int i = 0; i < numOp.size(); i += 1) {
            ArrayDeque testArray = new ArrayDeque();
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j <= (int) numOp.get(i); j += 1) {
                testArray.addLast(1);
            }
            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);
        }
        printTimingTable(numOp, times, numOp);
    }

 */

}
