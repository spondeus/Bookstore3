package store.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Book {

    @Id
    private String ISBN;

    private String title;

    private Boolean isOnMarket;

    private int edition;

    private LocalDate dateOfPublish;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author", nullable = false,referencedColumnName = "id")
    private Author author;
    @OneToMany(mappedBy = "id", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Store> stores;

    public Book() {
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    public Book(String ISBN, Author author, String title, Boolean isOnMarket, int edition, LocalDate dateOfPublish) {
        this.ISBN = ISBN;
        this.author = author;
        this.title = title;
        this.isOnMarket = isOnMarket;
        this.edition = edition;
        this.dateOfPublish = dateOfPublish;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean isOnMarket() {
        return isOnMarket;
    }

    public void setOnMarket(Boolean onMarket) {
        isOnMarket = onMarket;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public LocalDate getDateOfPublish() {
        return dateOfPublish;
    }

    public void setDateOfPublish(LocalDate dateOfPublish) {
        this.dateOfPublish = dateOfPublish;
    }

    @Override
    public String toString() {
        return String.format("Könyv: ISBN: %15s Title: %20s On market: %5s Edition: %2d Publish date: %20s Author: %20s"
                , ISBN, title, isOnMarket, edition, dateOfPublish.toString(), author.getName());
    }
}
