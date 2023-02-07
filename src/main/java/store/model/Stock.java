package store.model;

import jakarta.persistence.*;

@Entity
public class Stock {
    @EmbeddedId
    private StockKey id;

    private int stock;
    @ManyToOne
    @JoinColumn(name = "ISBN")
    @MapsId("book")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "id")
    @MapsId("store")
    private Store store;

    public Stock() {
    }

    public Stock(int stock, Book book, Store store) {
        this.stock = stock;
        this.book = book;
        this.store = store;
        this.id = new StockKey(book.getISBN(), store.getId());
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public StockKey getId() {
        return id;
    }

    public void setId(StockKey id) {
        this.id = id;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return String.format("Készlet: %20s %20s %3d", this.store.getAddress(), this.book.getTitle(), this.stock);
    }
}
