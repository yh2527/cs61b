package deque;

public class LinkedListDeque<T> {
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

    public void addFirst(T item) {
        Node oldHead = sentinel.next;
        Node head = new Node(sentinel, item, oldHead);
        sentinel.next = head;
        oldHead.prev = head;
        size += 1;
    }

    public void addLast(T item) {
        Node oldTail = sentinel.prev;
        Node tail = new Node(oldTail, item, sentinel);
        sentinel.prev = tail;
        oldTail.next = tail;
        size += 1;
    }

    public boolean isEmpty() {
        if (size > 0) {
            return false;
        }
        return true;
    }

    public int size() {
        return size;
    }

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

    public void printDeque() {
        for (int i = 0; i < size(); i += 1) {
            System.out.print(get(i));
            System.out.print(" ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        LinkedListDeque L = new LinkedListDeque();
        L.addFirst(3);
        L.addFirst(2);
        L.addFirst(1);
        System.out.println(L.removeFirst());
        System.out.println(L.removeLast());
    }

}
