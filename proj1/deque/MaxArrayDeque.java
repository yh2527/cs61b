package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comp;

    public MaxArrayDeque(Comparator<T> c) {
        comp = c;
    }

    public T max() {
        if (size() == 0) {
            return null;
        } else {
            T returnElement = get(0);
            for (int i = 0; i < size(); i += 1) {
                if (comp.compare(returnElement, get(i)) < 0) {
                    returnElement = get(i);
                }
            }
            return returnElement;
        }
    }

    public T max(Comparator<T> c) {
        if (size() == 0) {
            return null;
        } else {
            T returnElement = get(0);
            for (int i = 0; i < size(); i += 1) {
                if (c.compare(returnElement, get(i)) < 0) {
                    returnElement = get(i);
                }
            }
            return returnElement;
        }
    }
}
