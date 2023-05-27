package org.group7.server;

import com.mysql.cj.xdevapi.Client;
import org.group7.entities.*;

import org.group7.server.ocsf.AbstractServer;
import org.group7.server.ocsf.ConnectionToClient;
import org.group7.server.ocsf.SubscribedClient;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Server extends AbstractServer {

    public static Session session;

    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();


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
        configuration.addAnnotatedClass(ExecutableExam.class);
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

    public void sendToAllClients(Message message) {
        try {
            for (SubscribedClient SubscribedClient : SubscribersList) {
                SubscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        Message message = (Message) msg;
        String req = message.getMessage();

        try {
            switch (req) {
                case "#NewClient" -> {
                    SubscribedClient connection = new SubscribedClient(client);
                    SubscribersList.add(connection);
                }
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

                        ExecutableExam exam = session.find(ExecutableExam.class, obj[0]);

                        if (exam == null) {
                            client.sendToClient(new Message(obj[0], "#ExtraTime_Fail"));
                        } else {

                            int time = (int) obj[2];

                            ExtraTime et = new ExtraTime(exam.getExamId(), (String) obj[1], time);

                            session.save(et);
                            session.flush();

                            List<Principal> principals = getAll(Principal.class);

                            for (Principal p : principals) {
                                p.getRequestList().add(et);
                                session.save(p);
                                session.flush();
                            }

                            client.sendToClient(new Message(null, "#ExtraTime_Success"));
                        }
                        session.getTransaction().commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "#GetTimeRequests" -> {

                    try {
                        session.beginTransaction();

                        Principal principal = (Principal) message.getObject();

                        principal = session.find(Principal.class, principal.getUsername());

                        List<ExtraTime> requests = principal.getRequestList();

                        client.sendToClient(new Message(requests, "#GotRequestsList"));

                        session.getTransaction().commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "#ApproveTimeRequest" -> {

                    try {
                        session.beginTransaction();

                        ExtraTime request = (ExtraTime) message.getObject();
                        request = session.find(ExtraTime.class, request.getRequestId());
                        request.setStatus(true);
                        session.save(request);
                        session.flush();
                        Exam exam = session.find(Exam.class, request.getExamId());
                        exam.setDuration(exam.getDuration() + request.getExtra());
                        session.save(exam);
                        session.flush();

                        session.getTransaction().commit();

                        sendToAllClients(new Message(request, "#TimeRequestApproved"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "#DenyTimeRequest" -> {

                    try {
                        session.beginTransaction();

                        ExtraTime request = (ExtraTime) message.getObject();
                        request = session.find(ExtraTime.class, request.getRequestId());
                        session.delete(request);
                        session.flush();

                        session.getTransaction().commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "#GetTeacherExams" -> {

                    try {
                        session.beginTransaction();

                        Teacher teacher = (Teacher) message.getObject();

                        teacher = session.find(Teacher.class, teacher.getUsername());

                        List<Course> courses = teacher.getCourseList();

                        List<Exam> exams = new ArrayList<>();

                        for (Course course : courses) {
                            List<Exam> temp = course.getExamList();
                            if (temp != null) {
                                exams.addAll(temp);
                            }
                        }

                        client.sendToClient(new Message(exams, "#GotTeacherExams"));

                        session.getTransaction().commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "#SaveExecutable" -> {

                    Object[] obj = (Object[]) message.getObject();

                    try {
                        session.beginTransaction();

                        Teacher teacher = session.find(Teacher.class, ((Teacher)obj[2]).getUsername());

                        ExecutableExam exam = new ExecutableExam((String) obj[1], (Exam)obj[0], teacher);
                        teacher.getExamList().add(exam);
                        session.save(exam);
                        session.save(teacher);
                        session.flush();

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
        Subject math = new Subject("math");
        Subject cs = new Subject("Computer Science");
        session.save(math);
        session.save(cs);
        session.flush();

        // Add courses
        Course algebra = new Course("Linear Algebra", math);
        Course calculus = new Course("Calculus", math);
        session.save(algebra);
        session.save(calculus);
        session.flush();

        math.setCourseList(List.of(new Course[]{algebra, calculus}));
        session.save(math);
        session.flush();

        Course intro = new Course("Into to CS", cs);
        Course algo = new Course("Algorithms", cs);
        Course graphics = new Course("Computer Graphics", cs);
        Course cv = new Course("Computer Vision", cs);
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
        Question mathQ1 = new Question("What is 5+2 ?", List.of(new Course[]{algebra, calculus}),
                math, 0, (new String[]{"7", "3", "12", "0"}));

        Question mathQ2 = new Question("What is 5-3 ?", List.of(new Course[]{algebra}),
                math, 2, (new String[]{"4", "1", "2", "0"}));

        Question mathQ3 = new Question("What is integral of x ?", List.of(new Course[]{calculus}),
                math, 1, (new String[]{"x", "x^2 / 2", "2x", "Doesn't have integral!"}));

        session.save(mathQ1);
        session.save(mathQ2);
        session.save(mathQ3);
        session.flush();

        Question csQ1 = new Question("What does this print: cout << \"Hi\" << endl; ?",
                List.of(new Course[]{intro}), cs, 0, (new String[]{"Hi", "Error!", "Null", "Hi!"}));

        Question csQ2 = new Question("Who created FFT ?", List.of(new Course[]{graphics, cv, algo}),
                cs, 3, (new String[]{"Dr. Shuly", "Lagrange", "ME!", "Fourier"}));

        Question csQ3 = new Question("How many image pyramids do we know ?",
                List.of(new Course[]{cv}), cs, 1, (new String[]{"1", "2", "3", "None"}));

        Question csQ4 = new Question("How do we find shortest path in graph ?",
                List.of(new Course[]{intro, algo}), cs, 2, (new String[]{"DFS", "BFS", "Daijkstra", "A + B"}));

        Question csQ5 = new Question("How do we get edges of an image ?", List.of(new Course[]{cv, graphics}),
                cs, 2, (new String[]{"Sobel", "Canny", "No way!", "A + B"}));

        Question csQ6 = new Question("What is recursion ?", List.of(new Course[]{intro, algo}),
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
        List<Question> algebraQuestions = algebra.getQuestionList();
        List<Integer> points = List.of(new Integer[]{70, 30});
        Exam algebraExam = new Exam("Algebra Exam moed a", 1, 60, or, "No comment!", "No Comment!", algebra, algebraQuestions, points);
        or.getCreatedExams().add(algebraExam);
        algebra.getExamList().add(algebraExam);
        session.save(algebraExam);
        session.save(or);
        session.save(algebra);

        session.getTransaction().commit();
    }
}
