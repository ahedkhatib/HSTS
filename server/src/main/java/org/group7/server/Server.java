package org.group7.server;

import org.group7.entities.*;

import org.group7.server.ocsf.AbstractServer;
import org.group7.server.ocsf.ConnectionToClient;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


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

        configuration.addAnnotatedClass(Course.class);
        configuration.addAnnotatedClass(Exam.class);
        configuration.addAnnotatedClass(ExtraTime.class);
        configuration.addAnnotatedClass(Principal.class);
        configuration.addAnnotatedClass(Question.class);
        configuration.addAnnotatedClass(Result.class);
        configuration.addAnnotatedClass(Student.class);
        configuration.addAnnotatedClass(Subject.class);
        configuration.addAnnotatedClass(Teacher.class);
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Report.class);

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
                    List<Student> students = getAll(Student.class);
                    client.sendToClient(new Message(students, "#GotStudents"));
                }
                case "#GetGrades" -> {
                    client.sendToClient(new Message(message.getObject(), "#GotGrades"));
                }
                case "#UpdateGrade" -> {

                    Object[] obj = (Object[]) message.getObject();

                    try {
                        session.beginTransaction();

                        Result grade = (Result) obj[0];
                        int newValue = Integer.parseInt((String) obj[1]);

                        grade.setGrade(newValue);

                        session.merge(grade);

                        session.flush();

                        session.getTransaction().commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    client.sendToClient(new Message(obj[2], "#GradeUpdated"));
                }

                case "#Logout" -> {

                    String id = ((User) message.getObject()).getUsername();

                    try {
                        session.beginTransaction();

                        User user = session.find(User.class, id);

                        user.setConnected(false);
                        session.save(user);
                        session.flush();

                        client.sendToClient(new Message(user, "#Logout"));

                        session.getTransaction().commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                case "#Login" -> {

                    Object[] obj = (Object[]) message.getObject();

                    try {
                        session.beginTransaction();

                        User user = session.find(User.class, obj[0]);

                        String pass = obj[1].toString();

                        if (user == null || !Objects.equals(user.getPassword(), pass)) {
                            client.sendToClient(new Message(null, "#Login_Fail"));

                        } else {

                            if (user.isConnected()) {
                                client.sendToClient(new Message(user, "#Login_Connected"));
                            } else {
                                user.setConnected(true);
                                session.save(user);
                                session.flush();

                                client.sendToClient(new Message(user, "#Login_Success"));
                            }
                        }

                        session.getTransaction().commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

        Principal principal = new Principal("admin", "admin", "Dr. Ahed", "Khatib");
        session.save(principal);
        session.flush();

        Student alaa = new Student("alaakhamis", "123", "Alaa", "Khamis");
        Student ahed = new Student("ahedkhatib", "321", "Ahed", "Khatib");
        session.save(alaa);
        session.save(ahed);
        session.flush();

        session.getTransaction().commit();
    }
}
