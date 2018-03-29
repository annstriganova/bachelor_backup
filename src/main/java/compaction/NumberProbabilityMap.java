package compaction;

import java.math.BigDecimal;
import java.util.*;

/*
Вспомогательный класс, хранящий пары число=вероятность в отсортированном по вероятностям виде.
*/
public class NumberProbabilityMap implements Iterable<NumberProbabilityMap.Entry> {

    private ArrayList<Entry> n_p;

    NumberProbabilityMap() {
        this.n_p = new ArrayList<>();
    }

    class Entry {
        BigDecimal probability;
        Integer number;

        Entry(Integer number, BigDecimal probability) {
            this.probability = probability;
            this.number = number;
        }
    }

    public boolean containsKey(Integer key) {
        return keyArray().contains(key);
    }

    public BigDecimal get(Integer number) {
        for (Entry e : n_p) {
            if (e.number.equals(number))
                return e.probability;
        }
        return null;
    }

    public void put(Integer number, BigDecimal probabilty) {
        n_p.add(new Entry(number, probabilty));
        sortByValue();
    }

    public ArrayList<Integer> keyArray() {
        ArrayList<Integer> set = new ArrayList<>();
        for (Entry e : n_p) {
            set.add(e.number);
        }
        return set;
    }

    private void sortByValue() {
        Collections.sort(n_p, Comparator.comparing(e -> e.probability));
    }


    private Entry getEntry(int index) {
        return n_p.get(index);
    }

    public Iterator<NumberProbabilityMap.Entry> iterator() {

        return new Iterator<NumberProbabilityMap.Entry>() {
            // The next array position to return
            int pos = 0;

            public boolean hasNext() {
                return pos < n_p.size();
            }

            public NumberProbabilityMap.Entry next() {
                if(hasNext())
                    return getEntry(pos++);
                else
                    throw new NoSuchElementException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
