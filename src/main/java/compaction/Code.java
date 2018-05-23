package compaction;

import java.io.Serializable;
import java.math.BigDecimal;

class Code implements Serializable {

    transient static final int MAX_BITS = 65;
    BigDecimal code;
    Integer size;
    Integer bits;

    Code(BigDecimal code, Integer bits) {
        this.code = code;
        this.bits = bits;
    }

    void setSize(int size) {
        this.size = size;
    }

    boolean isBetterThan(Code previous) {
        if (previous == null)
            return true;
        double k1 = (double) previous.bits / Integer.SIZE * previous.size;
        double k2 = (double) this.bits / Integer.SIZE * this.size;
        // TODO: there could be problems, check it later
        return k1 < k2;
    }

}