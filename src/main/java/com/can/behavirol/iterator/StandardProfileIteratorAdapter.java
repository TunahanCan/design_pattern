package com.can.behavirol.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Repodaki null-sentinel kullanan {@link ProfileIterator} sözleşmesini
 * standart Java {@link Iterator} sözleşmesine uyarlar.
 */
public final class StandardProfileIteratorAdapter implements Iterator<Profile> {

    private final ProfileIterator delegate;

    public StandardProfileIteratorAdapter(ProfileIterator delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate cannot be null");
    }

    @Override
    public boolean hasNext() {
        return delegate.hasMore();
    }

    @Override
    public Profile next() {
        Profile next = delegate.getNext();
        if (next == null) {
            throw new NoSuchElementException("No more profiles");
        }
        return next;
    }
}
