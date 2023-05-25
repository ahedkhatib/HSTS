package org.group7.server;

import org.group7.entities.*;

import org.group7.server.ocsf.AbstractServer;
import org.group7.server.ocsf.ConnectionToClient;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.boot.jaxb.hbm.spi.SubEntityInfo;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class Server extends AbstractServer {

    public static Session session;

    public Server(int port) {
        super(port);
        SessionFactory sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();
        generate();     // Generate data
    }


    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(Course.class);
        configuration.addAnnotatedClass(Exam.class);
        configuration.addAnnotatedClass(AutomatedExam.class);
        configuration.addAnnotatedClass(ManualExam.class);
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

                case "#SendTimeRequest" -> {

                    Object[] obj = (Object[]) message.getObject();

                    try {
                        session.beginTransaction();

                        Exam exam = session.find(Exam.class, obj[0]);

                        if (exam == null) {
                            client.sendToClient(new Message(obj[0], "#ExtraTime_Fail"));
                        } else {

                            ExtraTime et = new ExtraTime(exam.getExamId(), (String) obj[1]);

                            session.save(et);
                            session.flush();

                            List<Principal> principals = getAll(Principal.class);

                            for (Principal p : principals) {
                                p.getRequestList().add(et);
                                session.save(p);
                                session.flush();
                            }

                            session.getTransaction().commit();

                            client.sendToClient(new Message(null, "#ExtraTime_Success"));

                        }
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

        // Add principal
        Principal principal = new Principal("admin", "admin", "Dr. Ahed", "Khatib");
        session.save(principal);
        session.flush();

        // Add students
        Student alaa = new Student("alaakhamis", "123", "Alaa", "Khamis");
        Student ahed = new Student("ahedkhatib", "321", "Ahed", "Khatib");
        Student lana = new Student("lanaasadi", "123", "Lana", "Asadi");
        Student ebraheem = new Student("ebraheem", "321", "Ebraheem", "Ebraheem");
        Student zinab = new Student("zinabdahle", "123", "Zinab", "Dahle");
        Student adan = new Student("adanhammoud", "321", "Adan", "Hammoud");
        session.save(alaa);
        session.save(ahed);
        session.save(lana);
        session.save(ebraheem);
        session.save(zinab);
        session.save(adan);
        session.flush();

        // Add teachers
        Teacher shir = new Teacher("shir", "shirpass", "Shir", "Sneh");
        Teacher malki = new Teacher("malki", "malkipass", "Malki", "Grosman");
        Teacher dan = new Teacher("dan", "danpass", "Dan", "Rosenbaum");
        Teacher or = new Teacher("or", "orpass", "Or", "Meir");
        Teacher hagit = new Teacher("hagit", "hagitpass", "Hagit", "Hel-Or");
        session.save(shir);
        session.save(malki);
        session.save(dan);
        session.save(or);
        session.save(hagit);
        session.flush();

        // Add subjects
        Subject math = new Subject(10, "math");
        Subject cs = new Subject(20, "Computer Science");
        session.save(math);
        session.save(cs);
        session.flush();

        // Add courses
        Course algebra = new Course(10, "Linear Algebra", math);
        Course calculus = new Course(20, "Calculus", math);
        session.save(algebra);
        session.save(calculus);
        session.flush();

        math.setCourseList(List.of(new Course[]{algebra, calculus}));
        session.save(math);
        session.flush();

        Course intro = new Course(30, "Into to CS", cs);
        Course algo = new Course(40, "Algorithms", cs);
        Course graphics = new Course(50, "Computer Graphics", cs);
        Course cv = new Course(60, "Computer Vision", cs);
        session.save(intro);
        session.save(algo);
        session.save(graphics);
        session.save(cv);
        session.flush();

        cs.setCourseList(List.of(new Course[]{intro, algo, graphics, cv}));
        session.save(cs);
        session.flush();

        // Connect teachers to courses
        intro.setTeacherList(List.of(new Teacher[]{hagit, dan, shir}));
        algo.setTeacherList(List.of(new Teacher[]{malki, dan}));
        graphics.setTeacherList(List.of(new Teacher[]{shir}));
        cv.setTeacherList(List.of(new Teacher[]{hagit, or, malki}));
        session.save(intro);
        session.save(algo);
        session.save(graphics);
        session.save(cv);
        session.flush();

        algebra.setTeacherList(List.of(new Teacher[]{or, dan}));
        calculus.setTeacherList(List.of(new Teacher[]{or}));
        session.save(algebra);
        session.save(calculus);
        session.flush();

        shir.setCourseList(List.of(new Course[]{intro, graphics}));
        malki.setCourseList(List.of(new Course[]{algo, cv}));
        dan.setCourseList(List.of(new Course[]{intro, algo, algebra}));
        or.setCourseList(List.of(new Course[]{algebra, calculus, cv}));
        hagit.setCourseList(List.of(new Course[]{intro, cv}));
        session.save(shir);
        session.save(malki);
        session.save(dan);
        session.save(or);
        session.save(hagit);
        session.flush();

        // Add questions
        Question mathQ1 = new Question(10, "What is 5+2 ?", List.of(new Course[]{algebra, calculus}),
                math, 0, (new String[]{"7", "3", "12", "0"}));

        Question mathQ2 = new Question(11, "What is 5-3 ?", List.of(new Course[]{algebra}),
                math, 2, (new String[]{"4", "1", "2", "0"}));

        Question mathQ3 = new Question(12, "What is integral of x ?", List.of(new Course[]{calculus}),
                math, 1, (new String[]{"x", "x^2 / 2", "2x", "Doesn't have integral!"}));

        session.save(mathQ1);
        session.save(mathQ2);
        session.save(mathQ3);
        session.flush();

        Question csQ1 = new Question(20, "What does this print: cout << \"Hi\" << endl; ?",
                List.of(new Course[]{intro}), cs, 0, (new String[]{"Hi", "Error!", "Null", "Hi!"}));

        Question csQ2 = new Question(21, "Who created FFT ?", List.of(new Course[]{graphics, cv, algo}),
                cs, 3, (new String[]{"Dr. Shuly", "Lagrange", "ME!", "Fourier"}));

        Question csQ3 = new Question(22, "How many image pyramids do we know ?",
                List.of(new Course[]{cv}), cs, 1, (new String[]{"1", "2", "3", "None"}));

        Question csQ4 = new Question(23, "How do we find shortest path in graph ?",
                List.of(new Course[]{intro, algo}), cs, 2, (new String[]{"DFS", "BFS", "Daijkstra", "A + B"}));

        Question csQ5 = new Question(24, "How do we get edges of an image ?", List.of(new Course[]{cv, graphics}),
                cs, 2, (new String[]{"Sobel", "Canny", "No way!", "A + B"}));

        Question csQ6 = new Question(25, "What is recursion ?", List.of(new Course[]{intro, algo}),
                cs, 1, (new String[]{"What is recursion ?", "Yes", "No", "Error"}));

        session.save(csQ1);
        session.save(csQ2);
        session.save(csQ3);
        session.save(csQ4);
        session.save(csQ5);
        session.save(csQ6);
        session.flush();

        // Connect questions with subjects and courses
        math.setQuestionList(List.of(new Question[]{mathQ1, mathQ2, mathQ3}));
        session.save(math);
        session.flush();

        algebra.setQuestionList(List.of(new Question[]{mathQ1, mathQ2}));
        calculus.setQuestionList(List.of(new Question[]{mathQ1, mathQ3}));
        session.save(algebra);
        session.save(calculus);
        session.flush();

        cs.setQuestionList(List.of(new Question[]{csQ1, csQ2, csQ3, csQ4, csQ5, csQ6}));
        session.save(cs);
        session.flush();

        intro.setQuestionList(List.of(new Question[]{csQ1, csQ4, csQ6}));
        algo.setQuestionList(List.of(new Question[]{csQ2, csQ4, csQ6}));
        cv.setQuestionList(List.of(new Question[]{csQ2, csQ3, csQ5}));
        graphics.setQuestionList(List.of(new Question[]{csQ2, csQ5}));
        session.save(intro);
        session.save(algo);
        session.save(graphics);
        session.save(cv);
        session.flush();

        // Add exams
        AutomatedExam exam = new AutomatedExam(1010);
        session.save(exam);
        session.flush();

        session.getTransaction().commit();
    }
}
