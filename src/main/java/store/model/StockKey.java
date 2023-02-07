package store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StockKey implements Serializable {

    @Column(name = "ISBN")
    private String book;
    @Column(name = "id")
    private long store;

    public StockKey() {
    }

    public StockKey(String book, long store) {
        this.book = book;
        this.store = store;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockKey stockKey = (StockKey) o;
        return book == stockKey.book && store == stockKey.store;
    }

    @Override
    public int hashCode() {
        return Objects.hash(book, store);
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public long getStore() {
        return store;
    }

    public void setStore(long store) {
        this.store = store;
    }
}
