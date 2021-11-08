import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** HW #7, Two-sum problem.
 * @author
 */
public class Sum {

    /** Returns true iff A[i]+B[j] = M for some i and j. */
    public static boolean sumsTo(int[] A, int[] B, int m) {
        Quicksort.quicksort(A);
        Quicksort.quicksort(B);
        int[] c = new int[A.length + B.length];
        System.arraycopy(A, 0, c, 0, A.length);
        System.arraycopy(B, 0, c, A.length, B.length);

        Map<Integer, Integer> items = new HashMap<Integer, Integer>();
        for (int i = 0; i < c.length; items.put(c[i], ++i))
            if (items.containsKey(m - c[i]) && Arrays.binarySearch(A, c[i]) >= 0  && Arrays.binarySearch(B, m-c[i]) >= 0 || Arrays.binarySearch(A, m -c[i]) >= 0  && Arrays.binarySearch(B, c[i]) >= 0)
                return true;
        return false;


    }


}
