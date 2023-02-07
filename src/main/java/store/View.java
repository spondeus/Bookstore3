package org.example;

import store.model.Author;
import store.model.Book;
import store.model.Stock;
import store.model.Store;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.in;
import static java.lang.System.out;

public class View {

    Controller controller;
    Scanner scanner;

    Book book;

    String[] menu = new String[]{"""
            1 Könyv kezelés
            2 Szerző kezelés
            3 Bolt kezelés
            4 Kevés példány
            5 Kilépés
                """, """
             1 Új könyv felvétele
             2 Könyv keresése, módosítása, kivezetése
            """, """
             1 Új szerző felvétele
             2 Szerző keresése, módosítása, törlése
            """, """
             1 Új bolt felvétele
             2 Bolt keresése, módosítása
            """, """
              1 Könyvcím alapján
              2 Szerző alapján
              3 ISBN alapján
            """, """
              1 Módosítás
              2 Kivezetés/Törlés
            """};

    public View(Controller c, Scanner sc) {
        controller = c;
        scanner = sc;
    }

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(in);
             Controller c = new Controller();
        ) {
            new View(c, sc).showMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMenu() {
//        controller.uploadRandomInstances();
        for (Tuple tuple: controller.urgent(5,10)) out.println(tuple);
        loop:
        do {
            out.println(menu[0]);
            switch (scanner.nextLine().charAt(0) - '0') {
                case 1 -> bookMenu();
                case 2 -> authorMenu();
                case 3 -> storeMenu();
                case 4 -> {
                    for (Stock stock : controller.fewInstances(5)) out.println(stock);
                }
                case 5 -> {
                    break loop;
                }
                default -> out.println("Téves választás!");
            }
        } while (true);
        out.println("Viszlát!");
    }


    private Object read(Class clazz) throws Exception {
        Constructor[] constructors = clazz.getConstructors();
        Constructor constructor;
        constructor = (constructors[0].getParameterCount() == 0) ? constructors[1] : constructors[0];
        int separator = clazz.getName().lastIndexOf(".");
        String clazzName = clazz.getName().substring(separator + 1);
        switch (clazzName) {
            case "Book" -> {
                Author author = (Author) read(Author.class);
                if (book != null && author.getName().equals("")) author = book.getAuthor();
                if (author.isMale() == null) author.setMale(true);
                return readBook(constructor, author);
            }
            case "Author" -> {
                return readAuthor(constructor);
            }
            case "Store" -> {
                return readStore(constructor);
            }
        }
        return null;
    }


    private Object readBook(Constructor constructor, Author author) throws Exception {
        out.print("Könyv ISBN:");
        String ISBN = scanner.nextLine();
        out.print("Cím:");
        String title = scanner.nextLine();
        out.print("Piacon van? y/n");
        String onMarket = scanner.nextLine();
        Boolean isOnMarket = onMarket.equals("") ? null : onMarket.equals("y");
        out.print("Kiadás:");
        String num = scanner.nextLine();
        int edition = num.isEmpty() ? 0 : Integer.parseInt(num);
        out.print("Publikálás időpontja: yyyy-mm-dd");
        String date = scanner.nextLine();
        LocalDate dateOfPublish = date.isEmpty() ? null : LocalDate.parse(date);
        return constructor.newInstance(ISBN, author, title, isOnMarket, edition, dateOfPublish);
    }

    private Object readAuthor(Constructor constructor) throws Exception {
        out.print("Szerző neve:");
        String name = scanner.nextLine();
        out.print("Születési idő yyyy-mm-dd:");
        String date = scanner.nextLine();
        LocalDate dateOfBirth = date.isEmpty() ? null : LocalDate.parse(date);
        out.print("Neme? m/f");
        String gender = scanner.nextLine();
        Boolean isMale = gender.equals("") ? null : gender.equals("m");
        return constructor.newInstance(name, dateOfBirth, isMale);
    }

    private Object readStore(Constructor constructor) throws Exception {
        out.print("Bolt címe:");
        String address = scanner.nextLine();
        out.print("Bolt tulaja:");
        String owner = scanner.nextLine();
        out.print("Aktív? y/n");
        String active = scanner.nextLine();
        Boolean isActive = active.equals("") ? null : active.equals("y");
        return constructor.newInstance(address, owner, isActive);
    }


    private void bookMenu() {
        try {
            out.println(menu[1]);
            switch (scanner.nextLine().charAt(0) - '0') {
                case 1 -> {
                    Book book = (Book) read(Book.class);
                    if (book.isOnMarket() == null) book.setOnMarket(true);
                    controller.add(book);
                }
                case 2 -> {
                    controller.getList(Book.class).stream().forEach(out::println);
                    List<Book> books;
                    out.print("Keresés (minden könyvre, Enter, különben szöveg és Enter) ");
                    boolean all = scanner.nextLine().equals("");
                    out.println(menu[4]);
                    char choice = scanner.nextLine().charAt(0);
                    do {
                        out.print("Keresési minta: ");
                        String pattern = scanner.nextLine();
                        books = switch (choice - '0') {
                            case 1 -> {
                                if (all) yield controller.search(Book.class, "allTitle", pattern);
                                else yield controller.search(Book.class, "title", pattern);
                            }
                            case 2 -> {
                                if (all) yield controller.search(Book.class, "allAuthor", pattern);
                                else yield controller.search(Book.class, "author", pattern);
                            }
                            case 3 -> controller.search(Book.class, "ISBN", pattern);
                            default -> new ArrayList<Book>();
                        };
                        for (Book book : books) out.println(book);
                    } while (books.size() > 1);
                    if (books.size() == 0) out.println("Nincs találat!");
                    else {
                        out.println(menu[5]);
                        switch (scanner.nextLine().charAt(0) - '0') {
                            case 1 -> {
                                out.println("Új értékek (Enter, ha változatlan):");
                                book = (Book) controller.getById(books.get(0).getISBN(), Book.class);
                                controller.modify(read(Book.class), book);
                                book = null;
                            }
                            case 2 -> {
                                controller.modify(controller.getById(books.get(0).getISBN(), Book.class));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void authorMenu() {
        try {
            out.println(menu[2]);
            switch (scanner.nextLine().charAt(0) - '0') {
                case 1 -> {
                    Author author = (Author) read(Author.class);
                    if (author.isMale() == null) author.setMale(true);
                    controller.add(author);
                }
                case 2 -> {
                    controller.getList(Author.class).stream().forEach(out::println);
                    out.println("Keresés név alapján");
                    List<Author> authors;
                    do {
                        out.print("Keresési minta: ");
                        String pattern = scanner.nextLine();
                        authors = controller.search(Author.class, "name", pattern);
                        for (Author author : authors) out.println(author);
                    } while (authors.size() > 1);
                    if (authors.size() == 0) out.println("Nincs találat!");
                    else {
                        out.println(menu[5]);
                        switch (scanner.nextLine().charAt(0) - '0') {
                            case 1 -> {
                                out.println("Új értékek (Enter, ha változatlan):");
                                controller.modify(read(Author.class), authors.get(0));
                            }
                            case 2 -> controller.deleteById(authors.get(0).getId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeMenu() {
        try {
            out.println(menu[3]);
            switch (scanner.nextLine().charAt(0) - '0') {
                case 1 -> {
                    Store store = (Store) read(Store.class);
                    if (store.isActive() == null) store.setActive(true);
                    controller.add(store);
                }
                case 2 -> {
                    controller.getList(Store.class).stream().forEach(out::println);
                    out.println("Keresés cím alapján");
                    List<Store> stores;
                    do {
                        out.print("Keresési minta: ");
                        String pattern = scanner.nextLine();
                        stores = controller.search(Store.class, "address", pattern);
                        for (Store store : stores) out.println(store);
                    } while (stores.size() > 1);
                    if (stores.size() == 0) out.println("Nincs találat!");
                    else {
                        out.println(menu[5].substring(menu[5].length() / 2));
                        out.println("Új értékek (Enter, ha változatlan):");
                        controller.modify(read(Store.class), stores.get(0));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}