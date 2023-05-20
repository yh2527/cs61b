package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    //constuctor: create an empty array
    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    private void resize(int s) {
        T[] newArray = (T[]) new Object[s];
        for (int i = 0; i < size(); i += 1) {
            newArray[i] = get(i);
        }
        items = newArray;
        nextFirst = items.length - 1;
        nextLast = size();
    }

    public void addFirst(T item) {
        if (size() == items.length) {
            resize(size() * 2);
        }
        items[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
        size += 1;
    }

    public void addLast(T item) {
        if (size() == items.length) {
            resize(size() * 2);
        }
        items[nextLast] = item;
        nextLast = plusOne(nextLast);
        size += 1;
    }

    public T removeFirst() {
        nextFirst = plusOne(nextFirst);
        T result = items[nextFirst];
        items[nextFirst] = null;
        if (!isEmpty()) {
            size -= 1;
        }
        if (size() > 16 && size() / items.length < 0.25) {
            resize(size() / 2);
        }
        return result;
    }

    public T removeLast() {
        nextLast = minusOne(nextLast);
        T result = items[nextLast];
        items[nextLast] = null;
        if (!isEmpty()) {
            size -= 1;
        }
        if (size() > 16 && size() / items.length < 0.25) {
            resize(size() / 2);
        }
        return result;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size;
    }

    public T get(int index) {
        if (index >= size()) {
            return null;
        }
        int ArrayIndex = (nextFirst + 1 + index) % items.length;
        return items[ArrayIndex];
    }

    public boolean equals(Object o) {
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        if (((ArrayDeque<?>) o).size() != size()) {
            return false;
        }
        for (int i = 0; i < size(); i += 1) {
            if (!get(i).equals(((ArrayDeque<?>) o).get(i))) {
                return false;
            }
        }
        return true;
    }

    public void printDeque() {
        for (int i = 0; i < size(); i += 1) {
            System.out.print(get(i));
            System.out.print(" ");
        }
        System.out.println();
    }

    private int minusOne(int index) {
        return (index - 1 + items.length) % items.length;
    }

    private int plusOne(int index) {
        return (index + 1) % items.length;
    }

    public static void main(String[] args) {
        ArrayDeque L = new ArrayDeque();
        L.addFirst(8);
        L.addFirst(7);
        L.addFirst(6);
        L.addFirst(5);
        L.addFirst(4);
        L.addFirst(3);
        L.addFirst(2);
        L.addFirst(1);
        L.printDeque();
        System.out.println(L.get(4));

        L.removeLast();
        L.printDeque();
        System.out.println(L.get(4));
    }
}
