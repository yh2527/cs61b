package flik;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class FlikTest {
    @Test
    public void randomizedTest() {
        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int ranNumber = StdRandom.uniform(0, 1000);
            assertTrue(Flik.isSameNumber(ranNumber, ranNumber));
            //assertTrue(isSameNumber(ranNumber, ranNumber));
        }
    }
}

