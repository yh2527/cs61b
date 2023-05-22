package timingtest;

import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        AList numOp = new AList();
        AList times = new AList();
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
            AList testArray = new AList();
            Stopwatch sw = new Stopwatch();
            for (int j = 0; j <= (int) numOp.get(i); j += 1) {
                testArray.addLast(1);
            }
            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);
        }
        printTimingTable(numOp, times, numOp);
    }
}
