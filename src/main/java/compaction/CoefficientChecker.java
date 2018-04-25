package compaction;

public class CoefficientChecker {

    private static Code previous;
    private static Code current;

    public static boolean isBetterThanPrevious(Code curr) {
        if (previous != null) {
            System.out.println("\nPrev: " + previous.size + " " + previous.code + " " + previous.bits);
        } else {
            System.out.println("Prev: null");
        }
        System.out.println("Curr: " + curr.size + " " + curr.code + " " + curr.bits);
        current = curr; // Это нужно для метода getPrevious

        if ((previous == null) || (previous.code.compareTo(current.code) == 0)) {
            previous = curr;
            return false;
        }
        double k1 = (double) previous.bits / Integer.SIZE * previous.size;
        double k2 = (double) curr.bits / Integer.SIZE * curr.size;
        System.out.println("Prev k " + k1 + " Curr k " + k2);
        previous = curr;
        // TODO: there could be problems, check it later
        return k1 < k2;
    }

    public static Code getPrevious() {
        Code var1 = previous;
        previous = current;
        return var1;
    }
}
