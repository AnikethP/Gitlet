import java.util.Arrays;

/** HW #7, Two-sum problem.
 * @author
 */
public class Sum {

    /** Returns true iff A[i]+B[j] = M for some i and j. */
    public static boolean sumsTo(int[] A, int[] B, int m) {
        Quicksort.quicksort(A);
        Quicksort.quicksort(B);

        int index = 0;
        while(index < A.length && A[index] <= m){
            if(Arrays.binarySearch(B, (m-A[index])) >= 0){
                return true;
            }
            index+=1;
        }
        return false;
    }


}
