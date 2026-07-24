package com.can.behavirol.iterator;

import java.util.Iterator;

public interface ProfileIterator {
    boolean hasMore();

    Profile getNext();

    /**
     * Custom eğitim sözleşmesini Java koleksiyon API'leriyle birlikte
     * kullanmak için standart bir {@link Iterator} görünümü döndürür.
     *
     * <p>Adapter aynı cursor'ı tüketir; bağımsız bir traversal başlatmaz.</p>
     */
    default Iterator<Profile> asJavaIterator() {
        return new StandardProfileIteratorAdapter(this);
    }
}
