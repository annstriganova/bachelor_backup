package compaction;

import java.io.Serializable;
import java.math.BigDecimal;

class Code implements Serializable {
        BigDecimal code;
        Integer size;
        Integer bits;

        Code(BigDecimal code, Integer bits) {
            this.code = code;
            this.bits = bits;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }