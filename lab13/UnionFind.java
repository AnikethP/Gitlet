import java.util.HashMap;
/** Disjoint sets of contiguous integers that allows (a) finding whether
 *  two integers are in the same set and (b) unioning two sets together.  
 *  At any given time, for a structure partitioning the integers 1 to N, 
 *  into sets, each set is represented by a unique member of that
 *  set, called its representative.
 *  @author
 */
public class UnionFind {

    /** A union-find structure consisting of the sets { 1 }, { 2 }, ... { N }.
     */
    public UnionFind(int N) {
        parents = new int[N+1];
        weights = new int[N+1];
        for(int i = 1; i<=N; i++){
            parents[i] = -1;
            weights[i] = 1;
        }
    }

    /** Return the representative of the set currently containing V.
     *  Assumes V is contained in one of the sets.  */
    public int find(int v) {
        while (parents[v] > 0){
            v = parents[v];
        }
        return v;
    }

    /** Return true iff U and V are in the same set. */
    public boolean samePartition(int u, int v) {
        return find(u) == find(v);
    }

    /** Union U and V into a single set, returning its representative. */
    public int union(int u, int v) {
        u = find(u);
        v = find(v);
        if(u==v){
            return u;
        }
        if (weights[u] > weights[v]){
            parents[v] = u;
            weights[u] += weights[v];
            weights[v] = -1;
            return v;
        }
        else{
            parents[u] = v;
            weights[v] += weights[u];
            weights[u] = -1;
            return u;
        }
    }

    private int[] parents;
    private int[] weights;

    //Used Professor Hug's lectures for guidance
}
