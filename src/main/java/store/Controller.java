package org.example;

import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.annotations.reflection.internal.XMLContext;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import store.model.Author;
import store.model.Book;
import store.model.Stock;
import store.model.Store;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

public class Controller implements AutoCloseable {

    Model model = new Model();
    Transaction transaction;

    @Override
    public void close() throws Exception {
        if (model != null) model.close();
    }

    public <T> void add(T toPersist) {
        Class clazz = null;
        if (toPersist instanceof Book) clazz = Book.class;
        save(toPersist);
    }

    public Author getAuthorByName(String authorName) {
        Session session = model.getSession();
        Transaction transaction = session.beginTransaction();
        String hql = "SELECT a FROM Author a WHERE (a.name)= :n";
        Author author;
        try {
            author = (Author) session.createSelectionQuery(hql)
                    .setParameter("n", authorName).getSingleResult();
        } catch (NoResultException e) {
            author = null;
        } finally {
            transaction.commit();
        }
        return author;
    }


    public List<Stock> fewInstances(int max) {
        Session session = model.getSession();
        if (transaction == null || !transaction.isActive()) transaction = session.beginTransaction();
        String hql = "SELECT s FROM Stock s JOIN FETCH Book b JOIN FETCH Store t WHERE s.stock< :max AND t.active=true";
        return (List<Stock>) session.createSelectionQuery(hql).setParameter("max", max).list();
    }

    public void modify(Object unsaved, Object saved) {
        try {
            Field[] fields = saved.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("id") || field.getName().equals("ISBN")) continue;
                field.setAccessible(true);
                Object u = field.get(unsaved);
                if (u != null && u != "" && u != (Integer) 0) {
                    if (!field.get(saved).equals(field.get(unsaved))) field.set(saved, u);
                }
            }
            save(saved);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modify(Object byId) {
        try {
            Field onMarket = byId.getClass().getDeclaredField("isOnMarket");
            onMarket.setAccessible(true);
            onMarket.set(byId, false);
            save(byId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> List<T> getList(Class clazz) {
        Session session = model.getSession();
        if (transaction == null || !transaction.isActive()) transaction = session.beginTransaction();
        String hql = "SELECT t FROM " + clazz.getName() + " t";
        List<T> list = (List<T>) session.createSelectionQuery(hql).list();
        transaction.commit();
        return list;
    }

    public <T> void save(T t) {
        Session session = model.getSession();
        if (transaction == null || !transaction.isActive()) transaction = session.beginTransaction();
        if (t instanceof Book)
            session.saveOrUpdate(((Book) t).getAuthor());
        session.persist(t);
        transaction.commit();
    }

    @SuppressWarnings("unchecked")
    public <T> Object getById(String id, Class clazz) {
        Session session = model.getSession();
        if (transaction == null || !transaction.isActive()) transaction = session.beginTransaction();
        String hql = "SELECT t FROM " + clazz.getName() + " t WHERE (t.id)= :i";
        T o = (T) session.createSelectionQuery(hql)
                .setParameter("i", id).getSingleResult();
        return o;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> search(Class clazz, String searchColumn, String pattern) {
        Session session = model.getSession();
        if (transaction == null || !transaction.isActive()) transaction = session.beginTransaction();
        String table = clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1);
        String hql = null;
        hql = switch (searchColumn) {
            case "title" -> "SELECT t FROM " + table + " t WHERE t.isOnMarket=true AND t.title LIKE :x";
            case "allTitle" -> "SELECT t FROM " + table + " t WHERE t.title LIKE :x";
            case "author" ->
                    "SELECT t FROM " + table + " t JOIN FETCH t.author a WHERE t.isOnMarket=true AND a.name LIKE :x";
            case "allAuthor" -> "SELECT t FROM " + table + " t JOIN FETCH t.author a WHERE a.name LIKE :x";
            default -> "SELECT t FROM " + table + " t WHERE " + searchColumn + " LIKE :x";
        };
        List<T> list = (List<T>) session.createSelectionQuery(hql).setParameter("x", "%" + pattern + "%").list();
        return list;
    }

    public void deleteById(long id) {
        Session session = model.getSession();
        if (transaction == null || !transaction.isActive()) transaction = session.beginTransaction();
        String hql = "SELECT s FROM Stock s JOIN FETCH Book b JOIN FETCH Author";
        List<Stock> stocks=(List<Stock>)session.createSelectionQuery(hql).list();
        stocks.stream().filter(stock -> stock.getBook().getAuthor().getId()==id).forEach(session::remove);
        hql = "SELECT a FROM Author a JOIN FETCH Book b WHERE a.id = :x";
        Author author = (Author) session.createSelectionQuery(hql).setParameter("x", id).getSingleResult();
        session.remove(author);
        transaction.commit();
    }

    @SuppressWarnings("unchecked")
    public void uploadRandomInstances() {
        Session session = model.getSession();
        if (transaction == null || !transaction.isActive()) transaction = session.beginTransaction();
        String hqlBook = "SELECT b FROM Book b";
        List<Book> books = (List<Book>) session.createSelectionQuery(hqlBook).list();
        String hqlStore = "SELECT s FROM Store s";
        List<Store> stores = (List<Store>) session.createSelectionQuery(hqlStore).list();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            Book book = books.get(random.nextInt(books.size()));
            Store store = stores.get(random.nextInt(stores.size()));
            Stock stock = new Stock(random.nextInt(7) + 1, book, store);
            session.persist(stock);
        }
        transaction.commit();
    }
    @SuppressWarnings("unchecked")
    public List<Tuple> urgent(int max,int sum) {
        Session session = model.getSession();
        if (transaction == null || !transaction.isActive()) transaction = session.beginTransaction();
        String hql = "SELECT sum(stock) SUM,address FROM Stock JOIN Store S on Stock.id = S.id " +
                "WHERE stock<:max AND active=true GROUP BY address HAVING SUM>:sum ORDER BY SUM DESC LIMIT 3";

        NativeQuery<Tuple> query= session.createNativeQuery(hql).setParameter("max",max).setParameter("sum",sum);
        List<Tuple> tuples= query.setResultTransformer( Transformers.aliasToBean(Tuple.class)).list();
        return tuples;
    }


}
