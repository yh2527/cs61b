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

    public void addFirst(T item) {
        //TODO: need to check for resizing
        items[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
        size += 1;
    }

    public void addLast(T item) {
        //TODO: need to check for resizing
        items[nextLast] = item;
        nextLast = plusOne(nextLast);
        size += 1;
    }
    public T removeFirst() {
        nextFirst = plusOne(nextFirst);
        T result = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        return result;
    }
    public T removeLast() {
        nextLast = minusOne(nextLast);
        T result = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        return result;
    }
    public boolean isEmpty() {
        if (size() > 0) {
            return false;
        }
        return true;
    }

    public int size() {
        return size;
    }

    public T get(int index) {
        if (index >= size()) {
            return null;
        }
        int ArrayIndex = nextFirst + 1 + index;
        if (ArrayIndex >= items.length) {
            ArrayIndex -= items.length;
        }
        return items[ArrayIndex];
    }

    public void printDeque() {
        for (int i = 0; i < size(); i += 1) {
            System.out.print(get(i));
            System.out.print(" ");
        }
        System.out.println();
    }

    private int minusOne(int index) {
        if (index == 0) {
            index = items.length - 1;
        } else {
            index -= 1;
        }
        return index;
    }

    private int plusOne(int index) {
        if (index == items.length - 1) {
            index = 0;
        } else {
            index += 1;
        }
        return index;
    }

    public static void main(String[] args) {
        ArrayDeque L = new ArrayDeque();
        L.addFirst(8);
        L.addFirst(7);
        L.addFirst(6);
        L.printDeque();
        L.addFirst(5);
        L.addFirst(4);
        L.addFirst(3);
        L.addFirst(2);
        L.addFirst(1);
        L.printDeque();
        System.out.println(L.get(0));
        System.out.println(L.get(1));
        System.out.println(L.get(8));
        L.removeFirst();
        L.printDeque();
        L.removeLast();
        L.printDeque();
        //L.addFirst(0);

    }
}
