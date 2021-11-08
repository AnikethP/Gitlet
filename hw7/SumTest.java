import org.junit.Test;

import static org.junit.Assert.*;
public class SumTest {
    @Test
    public void testSum(){
        int[] a = {1, 2, 3, 4, 5, 6, 7, 3};
        int[] b = {3, 433, 3 ,4 ,54};
        assertTrue(Sum.sumsTo(a, b, 61));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MySortingAlgorithmsTest.class));
    }
}
