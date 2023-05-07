package org.group7.server;

import org.group7.entities.*;

import org.group7.server.ocsf.AbstractServer;
import org.group7.server.ocsf.ConnectionToClient;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Server extends AbstractServer {

    public static Session session;

    public Server(int port) {
        super(port);
        SessionFactory sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();
        generate();
    }


    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();

        // Add classes
//        configuration.addAnnotatedClass(User.class);
//        configuration.addAnnotatedClass(Course.class);
//        configuration.addAnnotatedClass(Teacher.class);
//        configuration.addAnnotatedClass(Student.class);
//        configuration.addAnnotatedClass(Question.class);
//        configuration.addAnnotatedClass(Subject.class);
//        configuration.addAnnotatedClass(Exam.class);
//        configuration.addAnnotatedClass(Grade.class);

        configuration.addAnnotatedClass(Temp.class);
        configuration.addAnnotatedClass(TempGrade.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                configuration.getProperties()).build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    public static <T> List<T> getAll(Class<T> _class) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(_class);
        query.from(_class);
        List<T> data = session.createQuery(query).getResultList();

        return data;
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        Message message = (Message) msg;
        String req = message.getMessage();

        try {
            switch (req) {
                case "#GetStudents" -> {
                    List<Temp> students = getAll(Temp.class);
                    client.sendToClient(new Message(students, "#GotStudents"));
                }
                case "#SetGrades" -> {
                }
                case "#GetGrades" -> {
                    client.sendToClient(new Message(message.getObject(), "#GotGrades"));
                }
                case "#UpdateGrade" -> {

                    Object[] obj = (Object[]) message.getObject();

                    try {
                        session.beginTransaction();

                        TempGrade grade = (TempGrade) obj[0];
                        int newValue = (Integer) obj[1];

                        grade.setGrade(newValue);

                        session.merge(grade);

                        session.flush();

                        session.getTransaction().commit();
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    client.sendToClient(new Message(obj[2], "#GradeUpdated"));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void serverClosed() {
        super.serverClosed();
        stopListening();
        session.close();
    }

    public static void generate() {
        session.beginTransaction();

//        Student alaa = new Student("alaakhamis", "alaa123", "Alaa", "Khamis");
//        Student ahed = new Student("ahedkhatib", "ahed321", "Ahed", "Khatib");
//
//        session.save(alaa);
//        session.save(ahed);
//        session.flush();

        List<TempGrade> grades = new ArrayList<>();
        TempGrade grade1 = new TempGrade(95);
        TempGrade grade2 = new TempGrade(100);
        grades.add(grade1);
        grades.add(grade2);
        Temp student1 = new Temp("alaa", "khamis");
        student1.setGrades(grades);
        session.save(student1);
        session.flush();

        List<TempGrade> grades2 = new ArrayList<>();

        grade1 = new TempGrade(50);
        grade2 = new TempGrade(80);
        grades2.add(grade1);
        grades2.add(grade2);
        student1 = new Temp("ahed", "khatib");
        student1.setGrades(grades2);
        session.save(student1);
        session.flush();

        List<TempGrade> grades3 = new ArrayList<>();
        grade1 = new TempGrade(86);
        grade2 = new TempGrade(88);
        TempGrade grade3 = new TempGrade(87);
        grades3.add(grade1);
        grades3.add(grade2);
        grades3.add(grade3);
        student1 = new Temp("ebraheem", "ebraheem");
        student1.setGrades(grades3);
        session.save(student1);
        session.flush();

        List<TempGrade> grades4 = new ArrayList<>();
        grade1 = new TempGrade(90);
        grade2 = new TempGrade(91);
        grade3 = new TempGrade(80);
        grades4.add(grade1);
        grades4.add(grade2);
        grades4.add(grade3);
        student1 = new Temp("zinab", "dahle");
        student1.setGrades(grades4);
        session.save(student1);
        session.flush();

        List<TempGrade> grades5 = new ArrayList<>();
        grade1 = new TempGrade(90);
        grade2 = new TempGrade(91);
        grade3 = new TempGrade(98);
        grades5.add(grade1);
        grades5.add(grade2);
        grades5.add(grade3);
        student1 = new Temp("lana", "asadi");
        student1.setGrades(grades5);
        session.save(student1);
        session.flush();

        List<TempGrade> grades6 = new ArrayList<>();
        grade1 = new TempGrade(90);
        grade2 = new TempGrade(91);
        grade3 = new TempGrade(80);
        grades6.add(grade1);
        grades6.add(grade2);
        student1 = new Temp("adan", "hammoud");
        student1.setGrades(grades6);
        session.save(student1);
        session.flush();

        List<TempGrade> grades7 = new ArrayList<>();
        grade1 = new TempGrade(90);
        grade2 = new TempGrade(95);
        grade3 = new TempGrade(100);
        grades7.add(grade1);
        grades7.add(grade2);
        grades7.add(grade3);
        student1 = new Temp("alaa2", "la");
        student1.setGrades(grades7);
        session.save(student1);
        session.flush();

        List<TempGrade> grades8 = new ArrayList<>();
        grade1 = new TempGrade(90);
        grade2 = new TempGrade(91);
        grade3 = new TempGrade(80);
        grades8.add(grade1);
        grades8.add(grade3);
        student1 = new Temp("wolb", "asd");
        student1.setGrades(grades8);
        session.save(student1);
        session.flush();

        List<TempGrade> grades9 = new ArrayList<>();
        grade1 = new TempGrade(90);
        grade2 = new TempGrade(87);
        grade3 = new TempGrade(82);
        grades9.add(grade2);
        grades9.add(grade3);
        student1 = new Temp("dede", "dodo");
        student1.setGrades(grades9);
        session.save(student1);
        session.flush();

        List<TempGrade> grades0 = new ArrayList<>();
        grade1 = new TempGrade(4);
        grade2 = new TempGrade(32);
        grade3 = new TempGrade(13);
        grades0.add(grade1);
        grades0.add(grade2);
        grades0.add(grade3);
        student1 = new Temp("lola", "wa");
        student1.setGrades(grades0);
        session.save(student1);
        session.flush();

        session.getTransaction().commit();

    }

    public static void clearTables() {
        SessionFactory sessionFactory = getSessionFactory();
        org.hibernate.Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            String[] tableNames = {"Users", "Course", "Teacher", "Student", "Question", "Subject", "Exam", "Grade"};
            for (String tableName : tableNames) {
                session.createQuery("delete from " + tableName).executeUpdate();
            }

            session.flush();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public static void print() throws Exception {
        List<Temp> temps = getAll(Temp.class);

        for (Temp temp : temps) {
            System.out.print("Name: " + temp.getFirstName() + " " + temp.getLastName() + "\n");
            System.out.print("Grades: \n");

            List<TempGrade> grades = temp.getGrades();

            int i = 1;
            for (TempGrade grade : grades) {
                System.out.print(i + " - " + grade.getGrade());
                System.out.print("\n");
                i++;
            }
            System.out.print("\n");

        }
    }
}
