package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V>, Iterable<K> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private double loadFactor = 0.75;
    int size;

    /**
     * Constructors
     */
    public MyHashMap() {
        createTable(16);
    }

    public MyHashMap(int initialSize) {
        createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.loadFactor = maxLoad;
        createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        size = 0;
        buckets = new Collection[tableSize];
        for (int i = 0; i < tableSize; i += 1) {
            buckets[i] = createBucket();
        }
        return buckets;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        createTable(16);
        size = 0;
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    public boolean containsKey(K key) {
        for (K e : this) {
            if (e.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        int targetBucket = Math.floorMod(key.hashCode(), buckets.length);
        for (Node n : buckets[targetBucket]) {
            if (n.key.equals(key)) {
                return n.value;
            }
        }
        return null;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    public int size() {
        return size;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        int targetBucket = Math.floorMod(key.hashCode(), buckets.length);
        for (Node n : buckets[targetBucket]) {
            if (n.key.equals(key)) {
                n.value = value;
                return;
            }
        }
        Node newNode = new Node(key, value);
        buckets[targetBucket].add(newNode);
        size += 1;
        //TODO: resize
        if ((size/buckets.length) >= loadFactor) {
            resize();
        }
    }
    private void resize() {
        Collection<Node>[] oldBuckets = buckets;
        createTable(buckets.length * 2);
        size = 0;
        for (Collection<Node> bucket : oldBuckets) {
            for (Node node : bucket) {
                put(node.key, node.value);
            }
        }
    }

    /**
     * Returns a Set view of the keys contained in this map.
     */
    public Set<K> keySet() {
        Set<K> result = new HashSet<K>();
        for (K key : this) {
            result.add(key);
        }
        return result;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        throw new UnsupportedOperationException("Remove is not supported");
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("Remove is not supported");
    }

    public Iterator<K> iterator() {
        return new myHashSetIterator();
    }

    private class myHashSetIterator implements Iterator<K> {
        int bucketID;
        Iterator<Node> bucketIterator;  // tracks the current bucket iterator

        public myHashSetIterator() {
            bucketID = 0;
            updateBucketIterator();
        }

        // helper method to update bucketIterator to the next non-empty bucket's iterator
        // or null if no more buckets
        private void updateBucketIterator() {
            bucketIterator = null;
            while (bucketID < buckets.length) {
                if (!buckets[bucketID].isEmpty()) {
                    bucketIterator = buckets[bucketID].iterator();
                    break;
                }
                bucketID++;
            }
        }

        public boolean hasNext() {
            if (bucketIterator == null) {
                return false;
            } else if (bucketIterator.hasNext()) {
                return true;
            } else {
                bucketID++;
                updateBucketIterator();
                return hasNext();
            }
        }

        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements.");
            }
            return bucketIterator.next().key;
        }
    }

}
