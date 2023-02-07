package store.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private LocalDate dateOfBirth;

    private Boolean isMale;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Book> books;

    public Author() {
    }

    public Author(String name, LocalDate datOfBirth, Boolean isMale) {
        this.name = name;
        this.dateOfBirth = datOfBirth;
        this.isMale = isMale;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDatOfBirth() {
        return dateOfBirth;
    }

    public void setDatOfBirth(LocalDate datOfBirth) {
        this.dateOfBirth = datOfBirth;
    }

    public Boolean isMale() {
        return isMale;
    }

    public void setMale(Boolean male) {
        isMale = male;
    }

    @Override
    public String toString() {
        return String.format("Szerző: Id: %2d Name: %20s Birth: %20s Male: %5s" ,
                id, name, dateOfBirth.toString(),  isMale);
    }
}
