/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import ua.kpi.comsys.test2.NumberList;

/**
 * Custom implementation of INumberList interface.
 * Variant: 12.
 * C3 = 0 (Linear Doubly Linked List)
 * C5 = 2 (Octal) -> Additional: Decimal
 * C7 = 5 (Algebraic/Logical AND)
 *
 * @author Ihor Panchenko, Group IC-32
 */
public class NumberListImpl implements NumberList {

    private static class Node {
        Byte value;
        Node next;
        Node prev;

        Node(Byte value) {
            this.value = value;
            this.next = null;
            this.prev = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (!lines.isEmpty()) {
                initFromDecimalString(lines.get(0).trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        initFromDecimalString(value);
    }

    private void initFromDecimalString(String decimalValue) {
        if (decimalValue == null || decimalValue.isEmpty()) return;

        try {
            BigInteger bigInt = new BigInteger(decimalValue);

            if (bigInt.signum() < 0) {
                return;
            }

            String octalString = bigInt.toString(8); // Base 8

            for (char c : octalString.toCharArray()) {
                this.add((byte) Character.getNumericValue(c));
            }
        } catch (NumberFormatException e) {
        }
    }

    private NumberListImpl(String value, int radix) {
        this();
        if (radix == 8) {
            for (char c : value.toCharArray()) {
                this.add((byte) Character.getNumericValue(c));
            }
        }
    }

    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     */
    public void saveList(File file) {
        try {
            String decimalString = toDecimalString();
            Files.write(file.toPath(), decimalString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns student's record book number.
     */
    public static int getRecordBookNumber() {
        return 12;
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation (Decimal).
     */
    public NumberListImpl changeScale() {
        String decimalStr = this.toDecimalString();
        NumberListImpl newList = new NumberListImpl();

        for (char c : decimalStr.toCharArray()) {
            newList.add((byte) Character.getNumericValue(c));
        }
        return newList;
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation (AND).
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        BigInteger val1 = this.toBigInteger();

        StringBuilder sbArg = new StringBuilder();
        for (Byte b : arg) {
            sbArg.append(b);
        }
        BigInteger val2 = new BigInteger(sbArg.toString(), 8);

        BigInteger resultVal = val1.and(val2);

        return new NumberListImpl(resultVal.toString(8), 8);
    }

    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        if (isEmpty()) return "0";
        return toBigInteger().toString(10);
    }

    private BigInteger toBigInteger() {
        if (isEmpty()) return BigInteger.ZERO;
        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            sb.append(current.value);
            current = current.next;
        }
        return new BigInteger(sb.toString(), 8);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            sb.append(current.value);
            if (current.next != null) sb.append("");
            current = current.next;
        }
        return sb.toString();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Byte next() {
                if (current == null) throw new NoSuchElementException();
                Byte val = current.value;
                current = current.next;
                return val;
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        int i = 0;
        Node curr = head;
        while (curr != null) {
            arr[i++] = curr.value;
            curr = curr.next;
        }
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not implemented by task requirement");
    }

    @Override
    public boolean add(Byte e) {
        Node newNode = new Node(e);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        int idx = indexOf(o);
        if (idx != -1) {
            remove(idx);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte e : c) {
            if (add(e)) modified = true;
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        boolean modified = false;
        int i = index;
        for (Byte e : c) {
            add(i++, e);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            while (contains(e)) {
                remove(e);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Node curr = head;
        while (curr != null) {
            Node next = curr.next;
            if (!c.contains(curr.value)) {
                removeNode(curr);
                modified = true;
            }
            curr = next;
        }
        return modified;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public Byte get(int index) {
        checkIndex(index);
        return getNode(index).value;
    }

    @Override
    public Byte set(int index, Byte element) {
        checkIndex(index);
        Node node = getNode(index);
        Byte oldVal = node.value;
        node.value = element;
        return oldVal;
    }

    @Override
    public void add(int index, Byte element) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();

        if (index == size) {
            add(element);
        } else if (index == 0) {
            Node newNode = new Node(element);
            newNode.next = head;
            if (head != null) head.prev = newNode;
            head = newNode;
            if (tail == null) tail = newNode;
            size++;
        } else {
            Node current = getNode(index);
            Node prev = current.prev;
            Node newNode = new Node(element);

            prev.next = newNode;
            newNode.prev = prev;
            newNode.next = current;
            current.prev = newNode;
            size++;
        }
    }

    @Override
    public Byte remove(int index) {
        checkIndex(index);
        Node nodeToRemove = getNode(index);
        Byte val = nodeToRemove.value;
        removeNode(nodeToRemove);
        return val;
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        size--;
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        Node curr = head;
        while (curr != null) {
            if (Objects.equals(o, curr.value)) return index;
            curr = curr.next;
            index++;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = size - 1;
        Node curr = tail;
        while (curr != null) {
            if (Objects.equals(o, curr.value)) return index;
            curr = curr.prev;
            index--;
        }
        return -1;
    }

    @Override
    public ListIterator<Byte> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<Byte> listIterator(int index) {
        return new ListIterator<Byte>() {
            private Node lastReturned = null;
            private Node nextNode = (index == size) ? null : getNode(index);
            private int nextIndex = index;

            @Override
            public boolean hasNext() { return nextIndex < size; }

            @Override
            public Byte next() {
                if (!hasNext()) throw new NoSuchElementException();
                lastReturned = nextNode;
                nextNode = nextNode.next;
                nextIndex++;
                return lastReturned.value;
            }

            @Override
            public boolean hasPrevious() { return nextIndex > 0; }

            @Override
            public Byte previous() {
                if (!hasPrevious()) throw new NoSuchElementException();
                if (nextNode == null) nextNode = tail;
                else nextNode = nextNode.prev;
                lastReturned = nextNode;
                nextIndex--;
                return lastReturned.value;
            }

            @Override
            public int nextIndex() { return nextIndex; }

            @Override
            public int previousIndex() { return nextIndex - 1; }

            @Override
            public void remove() { throw new UnsupportedOperationException(); }

            @Override
            public void set(Byte e) {
                if (lastReturned == null) throw new IllegalStateException();
                lastReturned.value = e;
            }

            @Override
            public void add(Byte e) { throw new UnsupportedOperationException(); }
        };
    }

    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        NumberListImpl sub = new NumberListImpl();
        for(int i=fromIndex; i<toIndex; i++) {
            sub.add(get(i));
        }
        return sub;
    }


    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) return false;
        if (index1 == index2) return true;

        Node n1 = getNode(index1);
        Node n2 = getNode(index2);

        Byte tmp = n1.value;
        n1.value = n2.value;
        n2.value = tmp;
        return true;
    }

    @Override
    public void sortAscending() {
        if (size < 2) return;
        boolean swapped;
        do {
            swapped = false;
            Node current = head;
            while (current != null && current.next != null) {
                if (current.value > current.next.value) {
                    Byte tmp = current.value;
                    current.value = current.next.value;
                    current.next.value = tmp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    @Override
    public void sortDescending() {
        if (size < 2) return;
        boolean swapped;
        do {
            swapped = false;
            Node current = head;
            while (current != null && current.next != null) {
                if (current.value < current.next.value) {
                    Byte tmp = current.value;
                    current.value = current.next.value;
                    current.next.value = tmp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    @Override
    public void shiftLeft() {
        if (size < 2) return;
        Byte oldHeadVal = head.value;
        remove(0);
        add(oldHeadVal);
    }

    @Override
    public void shiftRight() {
        if (size < 2) return;
        Byte oldTailVal = tail.value;
        remove(size - 1);
        add(0, oldTailVal);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    private Node getNode(int index) {
        Node x;
        if (index < (size >> 1)) {
            x = head;
            for (int i = 0; i < index; i++)
                x = x.next;
        } else {
            x = tail;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
        }
        return x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof List)) return false;
        Iterator<Byte> e1 = iterator();
        Iterator<?> e2 = ((List<?>) o).iterator();
        while (e1.hasNext() && e2.hasNext()) {
            Byte o1 = e1.next();
            Object o2 = e2.next();
            if (!Objects.equals(o1, o2)) return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }
}
