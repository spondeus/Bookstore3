package store.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String address;

    private String owner;

    private Boolean active;

    @OneToMany(mappedBy = "ISBN", cascade = CascadeType.ALL)
    private List<Book> books;

    public Store() {
    }

    public Store(String address, String owner, Boolean active) {
        this.address = address;
        this.owner = owner;
        this.active = active;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }


    public long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return String.format("Bolt: Id: %2d Address: %20s Owner: %20s Active: %5s ",
                id, address, owner, String.valueOf(active));
    }
}
