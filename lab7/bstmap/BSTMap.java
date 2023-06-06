package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode label;
    private BSTMap<K, V> left;
    private BSTMap<K, V> right;
    private int size;

    private class BSTNode {
        private K key;
        private V value;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public BSTMap() {
        size = 0;
    }

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        label = null;
        left = null;
        right = null;
        size = 0;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        if (label == null) {
            return false;
        }
        if (label.key.compareTo(key) == 0) {
            return true;
        } else if (label.key.compareTo(key) > 0) {
            return left.containsKey(key);
        } else {
            return right.containsKey(key);
        }
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        if (label == null) {
            return null;
        }
        if (label.key.compareTo(key) == 0) {
            return label.value;
        } else if (label.key.compareTo(key) > 0) {
            return left.get(key);
        } else {
            return right.get(key);
        }
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        if (size() == 0 || label.key.compareTo(key) == 0) {
            label = new BSTNode(key, value);
        } else if (label.key.compareTo(key) > 0) {
            if (left == null) {
                left = new BSTMap<>();
            }
            left.put(key, value);
        } else {
            if (right == null) {
                right = new BSTMap<>();
            }
            right.put(key, value);
        }
        size += 1;
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    /* Prints out the BSTMap instance in order of increasing Key.
    * make changes */
    public void printInOrder() {
        String result = "";
        if (!(left == null)) {
            left.printInOrder();
        }
        result += label.key;
        result += ": ";
        result += label.value;
        result += ", ";
        System.out.print(result);
        if (!(right == null)) {
            right.printInOrder();
        }
    }
}
