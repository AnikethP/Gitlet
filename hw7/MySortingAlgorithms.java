import net.sf.saxon.lib.SaxonOutputKeys;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Note that every sorting algorithm takes in an argument k. The sorting 
 * algorithm should sort the array from index 0 to k. This argument could
 * be useful for some of your sorts.
 *
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Counting Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for(int j = 1; j <k; j++){

                for (int i = j-1; i >=0; i--) {
                    if(array[i+1] < array[i]) {
                        swap(array, i+1, i);
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {

            for(int i = 0; i < k; i++){
                int currMinIndex = i;
                for(int j = i; j < k; j++){
                    if(array[j] < array[currMinIndex]){
                        currMinIndex = j;
                    }

                }
                swap(array, i, currMinIndex);
            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if(k <= 1){
                return;
            }

            int[] first = Arrays.copyOfRange(array, 0, k/2);
            int[] second = Arrays.copyOfRange(array, k/2, k);
            sort(first, first.length);
            sort(second, second.length);
            merge(first, second, array);
        }

        public void merge(int[] array, int[] array2, int[] arr){
            int k = 0;
            int j = 0;
            int i = 0;
            while(k<array.length && j<array2.length){

                if(array[k]<array2[j]){
                    arr[i] = array[k];
                    i+=1;
                    k+=1;
                }
                else{
                    arr[i] = array2[j];
                    i+=1;
                    j+=1;
                }
            }
            //Add rest of elements
            while(k<array.length){
                arr[i] = array[k];
                i++;
                k++;
            }
            while(j<array2.length){
                arr[i] = array2[j];
                i++;
                j++;
            }


        }

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Counting Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class CountingSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME: to be implemented
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Counting Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
            LinkedList<Integer> ordered = new LinkedList();
            LinkedList[] positions = new LinkedList[10];
            for(int i = 0; i < positions.length; i++){
                positions[i] = new LinkedList();
            }
            int largest = Integer.MIN_VALUE;
            for(int i=0; i < k; i++){
                if(a[i]>largest){
                    largest = a[i];
                }
            }
            for(int i = 0; i < k; i ++){
                ordered.add(a[i]);
            }
            int maxDigits = Integer.toString(largest).length();
            int mod = 10;
            for(int i = 0; i < maxDigits; i ++){
                for(int j = 0; j < k; j ++){
                    positions[(ordered.get(j)%mod)/(mod/10)].add(j);
                }

                int[] copy = new int[ordered.size()];
                for(int m = 0; m < ordered.size(); m++){
                    copy[m] = ordered.get(m);
                }
                ordered.clear();

                for(int s = 0; s < 10; s++){
                    for(int b = 0; b < positions[s].size(); b++){
                        ordered.add(copy[(Integer) positions[s].get(b)]);
                    }
                }
                for(int x = 0; x < positions.length; x++){
                    positions[x] = new LinkedList();
                }

                mod*=10;
            }
            for(int i = 0; i < k; i++){
                a[i] = ordered.get(i);
            }
        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}
