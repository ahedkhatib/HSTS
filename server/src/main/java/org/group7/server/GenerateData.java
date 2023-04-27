package org.group7.server;

import org.group7.entities.*;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class GenerateData {

    public static Session session;

    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();

        // Add classes
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Teacher.class);
        configuration.addAnnotatedClass(Student.class);
//        configuration.addAnnotatedClass(Question.class);
//        configuration.addAnnotatedClass(Subject.class);
        configuration.addAnnotatedClass(Course.class);
        configuration.addAnnotatedClass(Exam.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                configuration.getProperties()).build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    private static <T> List<T> getAll(Class<T> _class) throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(_class);
        query.from(_class);
        List<T> data = session.createQuery(query).getResultList();
        return data;
    }

    public static void generate() {

        Student alaa = new Student("alaakhamis", "alaa123", "Alaa", "Khamis");
        Student ahed = new Student("ahedkhatib", "ahed321", "Ahed", "Khatib");

        session.save(alaa);
        session.save(ahed);
        session.flush();
    }

    public static void main(String[] args) {
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            generate();

        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occured, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            session.close();
        }
    }
}


