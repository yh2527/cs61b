package deque;

public class AListNoResizing<Item> {
    private Item[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    /**
     * Creates an empty list.
     */
    public AListNoResizing() {
        items = (Item[]) new Object[1000];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    /**
     * Inserts X into the back of the list.
     */
    public void addFirst(Item item) {
        items[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
        size += 1;
    }

    public void addLast(Item item) {
        items[nextLast] = item;
        nextLast = plusOne(nextLast);
        size += 1;
    }

    private int minusOne(int index) {
        return (index - 1 + items.length) % items.length;
    }

    private int plusOne(int index) {
        return (index + 1) % items.length;
    }

    /**
     * Returns the item from the back of the list.
     */
    public Item get(int index) {
        if (index >= size()) {
            return null;
        }
        int arrayIndex = (nextFirst + 1 + index) % items.length;
        return items[arrayIndex];
    }

    /**
     * Returns the number of items in the list.
     */
    public int size() {
        return size;
    }

    /**
     * Deletes item from back of the list and
     * returns deleted item.
     */
    public Item removeFirst() {
        nextFirst = plusOne(nextFirst);
        Item result = items[nextFirst];
        items[nextFirst] = null;
        if (!isEmpty()) {
            size -= 1;
        }
        return result;
    }


    public Item removeLast() {
        nextLast = minusOne(nextLast);
        Item result = items[nextLast];
        items[nextLast] = null;
        if (!isEmpty()) {
            size -= 1;
        }
        return result;
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}
