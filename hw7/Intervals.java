import edu.neu.ccs.gui.Interval;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/** HW #7, Sorting ranges.
 *  @author
  */
public class Intervals {
    /** Assuming that INTERVALS contains two-element arrays of integers,
     *  <x,y> with x <= y, representing intervals of ints, this returns the
     *  total length covered by the union of the intervals. */
    public static int coveredLength(List<int[]> intervals) {
        // REPLACE WITH APPROPRIATE STATEMENTS.
        List<int[]> merged = Intervals.merge(intervals);

        int count = 0;
        for(int[] x : merged){
            System.out.print(x[0] + " " + x[1]);
            count+= x[1]-x[0];
        }
        return count;
    }

    public static List<int[]> merge(List<int[]> intervals) {
        if(intervals.size() <= 1){ return intervals;}

        int[][] array = new int[intervals.size()][];
        for (int i = 0; i < intervals.size(); i++) {
            int[] row = intervals.get(i);
            for(int j = 0; j < 2; j++){
                array[i] = row;
            }
        }
        Arrays.sort(array, (i1, i2) -> Integer.compare(i1[0], i2[0]));

        List<int[]> merged = new ArrayList();

        int[] lastAdded = array[0];
        merged.add(lastAdded);
        for(int i = 0; i < array.length; i++){
            if(array[i][0] <= lastAdded[1]){
                lastAdded[1] = Math.max(lastAdded[1], array[i][1]);
            }
            else{
                lastAdded = array[i];
                merged.add(array[i]);
            }
        }
        return merged;
    }

    /** Test intervals. */
    static final int[][] INTERVALS = {
        {19, 30},  {8, 15}, {3, 10}, {6, 12}, {4, 5},
    };
    /** Covered length of INTERVALS. */
    static final int CORRECT = 23;

    /** Performs a basic functionality test on the coveredLength method. */
    @Test
    public void basicTest() {
        assertEquals(CORRECT, coveredLength(Arrays.asList(INTERVALS)));
    }

    /** Runs provided JUnit test. ARGS is ignored. */
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(Intervals.class));
    }

}
