package com.bobocode.bibernate.collection;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public class LazyList<E> implements List<E> {
    private final Supplier<List<E>> listSupplier;
    private List<E> internalList;

    public LazyList(Supplier<List<E>> listSupplier) {
        this.listSupplier = listSupplier;
    }
    
    private List<E> getInternalList() {
        if (internalList == null) {
            internalList = listSupplier.get();
        }
        return internalList;
    }

    @Override
    public String toString() {
        return getInternalList().toString();
    }

    @Override
    public int size() {
        return getInternalList().size();
    }

    @Override
    public boolean isEmpty() {
        return getInternalList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getInternalList().contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return getInternalList().iterator();
    }

    @Override
    public Object[] toArray() {
        return getInternalList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getInternalList().toArray(a);
    }

    @Override
    public boolean add(E e) {
        return getInternalList().add(e);
    }

    @Override
    public boolean remove(Object o) {
        return getInternalList().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getInternalList().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return getInternalList().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return getInternalList().addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getInternalList().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getInternalList().retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        getInternalList().replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        getInternalList().sort(c);
    }

    @Override
    public void clear() {
        getInternalList().clear();
    }

    @Override
    public boolean equals(Object o) {
        return getInternalList().equals(o);
    }

    @Override
    public int hashCode() {
        return getInternalList().hashCode();
    }

    @Override
    public E get(int index) {
        return getInternalList().get(index);
    }

    @Override
    public E set(int index, E element) {
        return getInternalList().set(index, element);
    }

    @Override
    public void add(int index, E element) {
        getInternalList().add(index, element);
    }

    @Override
    public E remove(int index) {
        return getInternalList().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return getInternalList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getInternalList().lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return getInternalList().listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return getInternalList().listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return getInternalList().subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<E> spliterator() {
        return getInternalList().spliterator();
    }

    public static <E1> List<E1> of() {
        return List.of();
    }

    public static <E1> List<E1> of(E1 e1) {
        return List.of(e1);
    }

    public static <E1> List<E1> of(E1 e1, E1 e2) {
        return List.of(e1, e2);
    }

    public static <E1> List<E1> of(E1 e1, E1 e2, E1 e3) {
        return List.of(e1, e2, e3);
    }

    public static <E1> List<E1> of(E1 e1, E1 e2, E1 e3, E1 e4) {
        return List.of(e1, e2, e3, e4);
    }

    public static <E1> List<E1> of(E1 e1, E1 e2, E1 e3, E1 e4, E1 e5) {
        return List.of(e1, e2, e3, e4, e5);
    }

    public static <E1> List<E1> of(E1 e1, E1 e2, E1 e3, E1 e4, E1 e5, E1 e6) {
        return List.of(e1, e2, e3, e4, e5, e6);
    }

    public static <E1> List<E1> of(E1 e1, E1 e2, E1 e3, E1 e4, E1 e5, E1 e6, E1 e7) {
        return List.of(e1, e2, e3, e4, e5, e6, e7);
    }

    public static <E1> List<E1> of(E1 e1, E1 e2, E1 e3, E1 e4, E1 e5, E1 e6, E1 e7, E1 e8) {
        return List.of(e1, e2, e3, e4, e5, e6, e7, e8);
    }

    public static <E1> List<E1> of(E1 e1, E1 e2, E1 e3, E1 e4, E1 e5, E1 e6, E1 e7, E1 e8, E1 e9) {
        return List.of(e1, e2, e3, e4, e5, e6, e7, e8, e9);
    }

    public static <E1> List<E1> of(E1 e1, E1 e2, E1 e3, E1 e4, E1 e5, E1 e6, E1 e7, E1 e8, E1 e9, E1 e10) {
        return List.of(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10);
    }

    @SafeVarargs
    public static <E1> List<E1> of(E1... elements) {
        return List.of(elements);
    }

    public static <E1> List<E1> copyOf(Collection<? extends E1> coll) {
        return List.copyOf(coll);
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return getInternalList().toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return getInternalList().removeIf(filter);
    }

    @Override
    public Stream<E> stream() {
        return getInternalList().stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return getInternalList().parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        getInternalList().forEach(action);
    }
}
