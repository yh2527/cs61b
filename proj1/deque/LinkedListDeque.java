package deque;

public class LinkedListDeque<T> implements Deque<T> {
    private class Node {
        private Node prev;
        private T item;
        private Node next;

        public Node(Node p, T i, Node n) {
            prev = p;
            item = i;
            next = n;
        }
    }

    private Node sentinel;
    private int size;

    //Constructor: create an empty list
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node oldHead = sentinel.next;
        Node head = new Node(sentinel, item, oldHead);
        sentinel.next = head;
        oldHead.prev = head;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        Node oldTail = sentinel.prev;
        Node tail = new Node(oldTail, item, sentinel);
        sentinel.prev = tail;
        oldTail.next = tail;
        size += 1;
    }

    /*@Override
    public boolean isEmpty() {
        return size() == 0;
    }*/

    @Override
    public int size() {
        return size;
    }

    @Override
    public T removeFirst() {
        T result = sentinel.next.item;
        Node newHead = sentinel.next.next;
        sentinel.next = newHead;
        newHead.prev = sentinel;
        if (!isEmpty()) {
            size -= 1;
        }
        return result;
    }

    @Override
    public T removeLast() {
        T result = sentinel.prev.item;
        Node newTail = sentinel.prev.prev;
        sentinel.prev = newTail;
        newTail.next = sentinel;
        if (!isEmpty()) {
            size -= 1;
        }
        return result;
    }

    @Override
    public T get(int index) {
        if (index >= size()) {
            return null;
        }
        Node current = sentinel;
        while (index >= 0) {
            current = current.next;
            index -= 1;
        }
        return current.item;
    }

    public T getRecursive(int index) {
        if (index >= size()) {
            return null;
        }
        return getRecursiveHelper(index, sentinel);

    }

    private T getRecursiveHelper(int index, Node n) {
        if (index == 0) {
            return n.next.item;
        } else {
            return getRecursiveHelper(index - 1, n.next);
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        if (((LinkedListDeque<?>) o).size() != size()) {
            return false;
        }
        for (int i = 0; i < size(); i += 1) {
            if (!get(i).equals(((LinkedListDeque<?>) o).get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void printDeque() {
        for (Node p = sentinel.next; p != sentinel; p = p.next) {
            System.out.print(p.item);
            System.out.print(" ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        LinkedListDeque L = new LinkedListDeque();
        L.addFirst(3);
        L.addFirst(2);
        L.addFirst(1);
        L.printDeque();
        //System.out.println(L.get(1));
        //System.out.println(L.getRecursive(1));
        //System.out.println(L.removeFirst());
        //System.out.println(L.removeLast());
        LinkedListDeque L2 = new LinkedListDeque();
        L2.addFirst(1);
        L2.addFirst(2);
        L2.addFirst(3);
        int L3 = 99;
        System.out.println(L.equals(L2));
    }

}
