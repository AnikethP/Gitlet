package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author
 */
class Reflector extends FixedRotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
        // FIXME
    }

    // FIXME?

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }
    @Override
    public String toString() {
        return "Reflector " + name();
    }

    @Override
    int convertForward(int p) {
        //System.out.print(" " + alphabet().toChar(p) + " ");
        //permutation().getCycles();
        int convert = permutation().permute(p);  //Permute setting
        //System.out.print(" " + alphabet().toChar(convert) + " ");
        return permutation().wrap(convert);
    }

    @Override
    int convertBackward(int e) {
        int convert = permutation().invert(e);  //Permute setting
        return permutation().wrap(convert);

    }
}
