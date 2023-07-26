package org.group7.server;

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
import java.util.*;
import java.util.HashMap;

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
        configuration.addAnnotatedClass(ManualResult.class);

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

    public void sendToAllClients(Message message, ConnectionToClient client) {
        try {
            for (SubscribedClient subscribedClient : SubscribersList) {

                if (subscribedClient.getClient() == client) {
                    continue;
                }

                subscribedClient.getClient().sendToClient(message);
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
                case "#GetExams" -> {
                    List<Exam> exams = getAll(Exam.class);
                    client.sendToClient(new Message(exams, "#gotExams"));
                }
                case "#GetSubjects" -> {
                    List<Subject> subjects = getAll(Subject.class);
                    client.sendToClient(new Message(subjects, "#GotSubjects"));
                }
                case "#GetCourses" -> {
                    List<Course> courses = getAll(Course.class);
                    client.sendToClient(new Message(courses, "#getCourses"));
                }
                case "#GetTeachers" -> {
                    List<Teacher> teachers = getAll(Teacher.class);
                    client.sendToClient(new Message(teachers, "#getTeachers"));
                }
                case "#GetGrades" -> {
                    client.sendToClient(new Message(message.getObject(), "#GotGrades"));
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

                            ExtraTime et = new ExtraTime(exam, (String) obj[1], time);

                            session.save(et);
                            session.flush();

                            List<Principal> principals = getAll(Principal.class);

                            for (Principal p : principals) {
                                p.getRequestList().add(et);
                                session.save(p);
                                session.flush();
                            }

                            client.sendToClient(new Message(null, "#ExtraTime_Success"));

                            sendToAllClients(new Message(null, "#GetAllTimeRequests"), client);
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

                        ExecutableExam exam = session.find(ExecutableExam.class, request.getExam().getExamId());
                        exam.setTime(exam.getTime() + request.getExtra());
                        session.save(exam);
                        session.flush();

                        session.getTransaction().commit();

                        sendToAllClients(new Message(request, "#TimeRequestApproved"), client);

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

                        List<Principal> principals = getAll(Principal.class);

                        for (Principal p : principals) {
                            ExtraTime finalRequest = request;
                            p.getRequestList().removeIf(item -> item.getRequestId() == finalRequest.getRequestId());
                            session.save(p);
                            session.flush();
                        }

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

                        List<ExecutableExam> executableExams = getAll(ExecutableExam.class);

                        Object[] obj = {exams, executableExams};

                        client.sendToClient(new Message(obj, "#GotTeacherExams"));

                        session.getTransaction().commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "#SaveExecutable" -> {

                    Object[] obj = (Object[]) message.getObject();

                    try {
                        session.beginTransaction();

                        Teacher teacher = session.find(Teacher.class, ((Teacher) obj[2]).getUsername());

                        ExecutableExam exam = new ExecutableExam((String) obj[1], (Exam) obj[0], teacher);
                        teacher.getExamList().add(exam);

                        session.save(exam);
                        session.save(teacher);
                        session.flush();

                        session.getTransaction().commit();

                        updateData(client);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "#StartExam" -> {

                    String examId = (String) message.getObject();

                    try {
                        session.beginTransaction();

                        ExecutableExam exam = session.find(ExecutableExam.class, examId);

                        if (exam == null) {
                            client.sendToClient(new Message(examId, "#StartExam_Incorrect"));
                        } else {
                            if (exam.getExam().getType() == 1) {
                                client.sendToClient(new Message(exam, "#StartExam_Auto"));
                            } else {
                                client.sendToClient(new Message(exam, "#StartExam_Manual"));
                            }
                        }

                        session.getTransaction().commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "#saveExam" -> {

                    try {
                        session.beginTransaction();

                        Exam exam = (Exam) message.getObject();

                        List<Question> questions = exam.getQuestionList();
                        List<Question> temp = new ArrayList<>();
                        for (Question t : questions) {
                            Question question = session.find(Question.class, t.getQuestionId());
                            temp.add(question);
                        }
                        exam.setQuestionList(temp);

                        Teacher teacher = session.find(Teacher.class, exam.getCreator().getUsername());
                        exam.setCreator(teacher);
                        teacher.getCreatedExams().add(exam);

                        Course course = session.find(Course.class, exam.getCourse().getCourseId());
                        exam.setCourse(course);
                        course.getExamList().add(exam);

                        session.save(exam);
                        session.save(teacher);
                        session.save(course);

                        for (Question t : temp) {
                            Question question = session.find(Question.class, t.getQuestionId());
                            question.getExamList().add(exam);
                            session.save(course);
                        }

                        session.flush();
                        session.getTransaction().commit();

                        client.sendToClient(new Message(null, "#ExamSaved"));

                        updateData(client);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                case "#SaveQuestion" -> {
                    try {

                        session.beginTransaction();

                        Question question = (Question) message.getObject();

                        List<Course> courses = question.getCourseList();
                        List<Course> temp = new ArrayList<>();
                        for (Course t : courses) {
                            Course course = session.find(Course.class, t.getCourseId());
                            temp.add(course);
                        }

                        question.setCourseList(temp);

                        Subject subject = session.find(Subject.class, question.getSubject().getSubjectId());
                        question.setSubject(subject);
                        subject.getQuestionList().add(question);

                        session.save(subject);
                        session.save(question);

                        for (Course t : temp) {
                            Course course = session.find(Course.class, t.getCourseId());
                            course.getQuestionList().add(question);
                            session.save(course);
                        }

                        session.flush();
                        session.getTransaction().commit();

                        client.sendToClient(new Message(question, "#PrepareQuestion_Success"));

                        updateData(client);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                case "#FinishExam" -> {

                    Object[] objects = (Object[]) message.getObject();

                    try {
                        session.beginTransaction();

                        User user = session.find(User.class, ((User) objects[1]).getUsername());
                        Result result = (Result) objects[0];

                        ExecutableExam exam = session.find(ExecutableExam.class, ((ExecutableExam) objects[2]).getExamId());
                        double[] arr = (double[]) objects[3];
                        exam.setAverage(arr[0]);
                        exam.setMedian(arr[1]);
                        exam.setDistribution((int[]) objects[4]);
                        session.save(exam);

                        result.setExam(exam);
                        session.save(result);

                        Student student = (Student) user;
                        student.getExamList().add(exam);
                        student.getResultList().add(result);
                        session.save(student);

                        exam.getStudentList().add(student);
                        session.save(exam);
                        session.flush();

                        session.getTransaction().commit();

                        client.sendToClient(new Message(null, "#ExamFinished"));

                        updateData(client);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "#FinishManExam" -> {
                    try {
                        session.beginTransaction();

                        ManualResult result = (ManualResult) message.getObject();

                        Student student = result.getStudent();
                        student = session.find(Student.class, student.getUsername());
                        student.getManualResultList().add(result);
                        session.save(student);

                        ExecutableExam exam = result.getExam();
                        exam = session.find(ExecutableExam.class, exam.getExamId());
                        exam.getStudentList().add(student);
                        session.save(exam);

                        session.save(result);
                        session.flush();

                        session.getTransaction().commit();

                        client.sendToClient(new Message(null, "#ExamFinished"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "#GetTeacherCourses" -> {

                    try {
                        session.beginTransaction();

                        Teacher teacher = (Teacher) message.getObject();

                        teacher = session.find(Teacher.class, teacher.getUsername());

                        List<Course> courses = teacher.getCourseList();

                        client.sendToClient(new Message(courses, "#GotTeacherCourses"));

                        session.getTransaction().commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "#getExecutableExam" -> {
                    List<ExecutableExam> executable = getAll(ExecutableExam.class);
                    client.sendToClient(new Message(executable, "#GotExecutableExam"));
                }
                case "#GetStudentResults" -> {

                    try {
                        session.beginTransaction();

                        Student student = (Student) message.getObject();

                        student = session.find(Student.class, student.getUsername());

                        List<Result> results = student.getResultList();

                        client.sendToClient(new Message(results, "#GotStudentResults"));

                        session.getTransaction().commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "#approveResult" -> {

                    Object[] objects = (Object[]) message.getObject();

                    try {
                        session.beginTransaction();

                        Result result = session.find(Result.class, ((Result) objects[0]).getResultId());
                        ExecutableExam exam = session.find(ExecutableExam.class, result.getExam().getExamId());
                        result.setStatus(true);
                        result.setGrade((Integer) objects[2]);
                        result.setTeacherNote((String) objects[3]);
                        exam.setAverage((double) objects[4]);
                        exam.setMedian((double) objects[5]);
                        exam.setDistribution((int[]) objects[6]);

                        session.save(result);
                        session.save(exam);

                        session.flush();
                        session.getTransaction().commit();

                        updateData(client);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateData(ConnectionToClient client) {

        sendToAllClients(new Message(null, "#GetTeacherCourses"), client);
        sendToAllClients(new Message(null, "#GetTeacherExams"), client);
        sendToAllClients(new Message(null, "#GetStudentResults"), client);
        sendToAllClients(new Message(null, "#GetData"), client);
        sendToAllClients(new Message(null, "#getExecutableExam"), client);

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
        Student alaa = new Student("alaakhamis", "123", "Alaa", "Khamis", "123456789");
        Student ahed = new Student("ahedkhatib", "321", "Ahed", "Khatib", "123123123");
        Student lana = new Student("lanaasadi", "123", "Lana", "Asadi", "123456789");
        Student ebraheem = new Student("ebraheem", "321", "Ebraheem", "Ebraheem", "987564321");
        Student zinab = new Student("zinabdahle", "123", "Zinab", "Dahle", "123456321");
        Student adan = new Student("adanhammoud", "321", "Adan", "Hammoud", "456123789");
        Student diab = new Student("diabdabbah", "123", "Diab", "Dabbah", "123456789");
        Student yosef = new Student("yosefsafih", "123", "Yosef", "Safih", "123456789");
        Student mona = new Student("monaasadi", "123", "Mona", "Asadi", "123456789");
        Student fadi = new Student("fadiahmad", "123", "Fadi", "Ahmad", "123456789");
        Student mohamed = new Student("mohamedmosa", "123", "Mohamed", "Mosa", "123456789");
        Student hasan = new Student("hasanemar", "123", "Hasan", "Emar", "123456789");
        Student Sophia = new Student("sophiajon", "123", "Sophia", "Jon", "123456789");
        Student ava = new Student("avawilson", "123", "Ava", "Wilson", "123456789");
        Student lora = new Student("loraamon", "123", "Lora", "Amon", "123456789");


        session.save(alaa);
        session.save(ahed);
        session.save(lana);
        session.save(ebraheem);
        session.save(zinab);
        session.save(adan);
        session.save(diab);
        session.save(yosef);
        session.save(mona);
        session.save(fadi);
        session.save(mohamed);
        session.save(hasan);
        session.save(Sophia);
        session.save(ava);
        session.save(lora);
        session.flush();

        // Add teachers
        Teacher shir = new Teacher("shir", "shirpass", "Shir", "Sneh");
        Teacher malki = new Teacher("malki", "malkipass", "Malki", "Grosman");
        Teacher dan = new Teacher("dan", "danpass", "Dan", "Rosenbaum");
        Teacher or = new Teacher("or", "orpass", "Or", "Meir");
        Teacher hagit = new Teacher("hagit", "hagitpass", "Hagit", "Hel-Or");

        Teacher shuly = new Teacher("shuly", "shulypass", "Shuly", "Winter");
        Teacher oren = new Teacher("oren", "orenpass", "Oren", "Wymn");
        Teacher roi = new Teacher("roi", "roipass", "Roi", "Poranne");
        Teacher dalal = new Teacher("dalal", "dalalpass", "Dalal", "Akavia");

        session.save(shir);
        session.save(malki);
        session.save(dan);
        session.save(or);
        session.save(hagit);
        session.save(shuly);
        session.save(oren);
        session.save(roi);
        session.save(dalal);
        session.flush();

        // Add subjects
        Subject math = new Subject("Mathematics");
        Subject cs = new Subject("Computer Science");
        Subject english = new Subject("English");

        session.save(math);
        session.save(cs);
        session.save(english);
        session.flush();

        // Add courses
        Course algebra = new Course("Linear Algebra", math);
        Course calculus = new Course("Calculus", math);

        session.save(algebra);
        session.save(calculus);
        session.flush();

        math.setCourseList(new ArrayList<>(List.of(new Course[]{algebra, calculus})));

        session.save(math);
        session.flush();

        Course intro = new Course("Into to CS", cs);
        Course algo = new Course("Algorithms", cs);
        Course graphics = new Course("Computer Graphics", cs);
        Course cv = new Course("Computer Vision", cs);
        Course os = new Course("Operating Systems", cs);
        Course ca = new Course("Computer Architecture", cs);

        session.save(intro);
        session.save(algo);
        session.save(graphics);
        session.save(cv);
        session.save(os);
        session.save(ca);
        session.flush();

        cs.setCourseList(new ArrayList<>(List.of(new Course[]{intro, algo, graphics, cv, os, ca})));
        session.save(cs);
        session.flush();

        Course advanced1 = new Course("Advanced 1", english);
        Course advanced2 = new Course("Advanced 2", english);
        session.save(advanced1);
        session.save(advanced2);
        session.flush();

        english.setCourseList(new ArrayList<>(List.of(new Course[]{advanced1, advanced2})));
        session.save(english);
        session.flush();

        // Connect teachers to courses
        algebra.setTeacherList(new ArrayList<>(List.of(new Teacher[]{or, dan})));
        calculus.setTeacherList(new ArrayList<>(List.of(new Teacher[]{or})));
        intro.setTeacherList(new ArrayList<>(List.of(new Teacher[]{hagit, dan, shir})));
        algo.setTeacherList(new ArrayList<>(List.of(new Teacher[]{malki, dan})));
        graphics.setTeacherList(new ArrayList<>(List.of(new Teacher[]{shir, roi})));
        cv.setTeacherList(new ArrayList<>(List.of(new Teacher[]{hagit, or, malki})));
        os.setTeacherList(new ArrayList<>(List.of(new Teacher[]{dan, or})));
        ca.setTeacherList(new ArrayList<>(List.of(new Teacher[]{shir, malki})));
        advanced1.setTeacherList(new ArrayList<>(List.of(new Teacher[]{dalal})));
        advanced2.setTeacherList(new ArrayList<>(List.of(new Teacher[]{dalal})));

        session.save(algebra);
        session.save(calculus);
        session.save(intro);
        session.save(algo);
        session.save(graphics);
        session.save(cv);
        session.save(os);
        session.save(ca);
        session.save(advanced1);
        session.save(advanced2);
        session.flush();

        shir.setCourseList(new ArrayList<>(List.of(new Course[]{intro, graphics, ca})));
        malki.setCourseList(new ArrayList<>(List.of(new Course[]{algo, cv, ca})));
        dan.setCourseList(new ArrayList<>(List.of(new Course[]{intro, algo, algebra, os})));
        or.setCourseList(new ArrayList<>(List.of(new Course[]{algebra, calculus, cv, os})));
        hagit.setCourseList(new ArrayList<>(List.of(new Course[]{intro, cv})));
        dalal.setCourseList(new ArrayList<>(List.of(new Course[]{advanced1, advanced2})));
        shuly.setCourseList(new ArrayList<>(List.of(new Course[]{intro})));
        roi.setCourseList(new ArrayList<>(List.of(new Course[]{graphics})));
        oren.setCourseList(new ArrayList<>(List.of(new Course[]{algo})));

        session.save(shir);
        session.save(malki);
        session.save(dan);
        session.save(or);
        session.save(hagit);
        session.save(dalal);
        session.save(shuly);
        session.save(roi);
        session.save(oren);
        session.flush();

        // Add questions
        Question mathQ1 = new Question("What is 5+2 ?", new ArrayList<>(List.of(new Course[]{algebra, calculus})),
                math, 0, (new String[]{"7", "3", "12", "0"}));

        Question mathQ2 = new Question("What is 5-3 ?", new ArrayList<>(List.of(new Course[]{algebra})),
                math, 2, (new String[]{"4", "1", "2", "0"}));

        Question mathQ3 = new Question("What is integral of x ?", new ArrayList<>(List.of(new Course[]{calculus})),
                math, 1, (new String[]{"x", "x^2 / 2", "2x", "Doesn't have integral!"}));

        Question mathQ4 = new Question("Simplify 2x+3-4x", new ArrayList<>(List.of(new Course[]{calculus})),
                math, 0, (new String[]{"-2x + 3", "-2x - 3", "-2x + 7", "6x - 3"}));

        Question mathQ5 = new Question("Find x: 2(x+5)=16", new ArrayList<>(List.of(new Course[]{calculus})),
                math, 2, (new String[]{"x = 4", "x = 5", "x = 6", "x = 7"}));

        Question mathQ6 = new Question("Factorize x^2-4x+4", new ArrayList<>(List.of(new Course[]{calculus})),
                math, 0, (new String[]{"(x + 2)(x + 2)", "(x - 2)(x + 2)", "(x + 4)(x - 4)", "(x - 4)(x - 4)"}));

        session.save(mathQ1);
        session.save(mathQ2);
        session.save(mathQ3);
        session.save(mathQ4);
        session.save(mathQ5);
        session.save(mathQ6);
        session.flush();

        Question csQ1 = new Question("What does this print: cout << \"Hi\" << endl; ?",
                new ArrayList<>(List.of(new Course[]{intro})), cs, 0, (new String[]{"Hi", "Error!", "Null", "Hi!"}));

        Question csQ2 = new Question("Who created FFT ?", new ArrayList<>(List.of(new Course[]{graphics, cv, algo})),
                cs, 3, (new String[]{"Dr. Shuly", "Lagrange", "ME!", "Fourier"}));

        Question csQ3 = new Question("How many image pyramids do we know ?",
                new ArrayList<>(List.of(new Course[]{cv})), cs, 1, (new String[]{"1", "2", "3", "None"}));

        Question csQ4 = new Question("How do we find shortest path in graph ?",
                new ArrayList<>(List.of(new Course[]{intro, algo})), cs, 2, (new String[]{"DFS", "BFS", "Daijkstra", "A + B"}));

        Question csQ5 = new Question("How do we get edges of an image ?", new ArrayList<>(List.of(new Course[]{cv, graphics})),
                cs, 3, (new String[]{"Sobel", "Canny", "No way!", "A + B"}));

        Question csQ6 = new Question("What is recursion ?", new ArrayList<>(List.of(new Course[]{intro, algo})),
                cs, 1, (new String[]{"What is recursion ?", "Yes", "No", "Error"}));

        session.save(csQ1);
        session.save(csQ2);
        session.save(csQ3);
        session.save(csQ4);
        session.save(csQ5);
        session.save(csQ6);
        session.flush();

        //operating systems
        Question osQ1 = new Question("The purpose of a device drive?", new ArrayList<>(List.of(new Course[]{os})),
                cs, 3, (new String[]{"Manage memory resources", "Handle file management", "Provide security features", "Communicate with hardware devices"}));

        Question osQ2 = new Question("What is \"page replacement\":?", new ArrayList<>(List.of(new Course[]{os})),
                cs, 1, (new String[]{"Allocating memory to new processes", "Manging virtual memory", "Scheduling processes for execution", "Handling input/output operations"}));

        session.save(osQ1);
        session.save(osQ2);
        session.flush();

        //computer archeticture
        Question caQ1 = new Question("Fetching instructions happens in?", new ArrayList<>(List.of(new Course[]{ca})),
                cs, 1, (new String[]{"ALU", "CU", "Memory Unit", "I/O Unit"}));

        Question caQ2 = new Question("The purpose of cache memory?", new ArrayList<>(List.of(new Course[]{ca})),
                cs, 2, (new String[]{"To store data permanently", "Both 1+3", "Faster access to frequently used data", "No purpose!"}));

        Question caQ3 = new Question("RISC architecture has?", new ArrayList<>(List.of(new Course[]{ca})),
                cs, 2, (new String[]{"Large instruction set", "Complex instructions", "Fewer instructions", "Higher clock speeds"}));

        session.save(caQ1);
        session.save(caQ2);
        session.save(caQ3);
        session.flush();

        //english
        Question enQ1 = new Question("\"enormous\" is equivalent to?", new ArrayList<>(List.of(new Course[]{advanced1})),
                english, 1, (new String[]{"Tiny", "Large", "Average", "Minuscule"}));

        Question enQ2 = new Question("She ___ to the store yesterday.", new ArrayList<>(List.of(new Course[]{advanced1})),
                english, 3, (new String[]{"go", "goes", "going", "went"}));

        Question enQ3 = new Question("The correct spelling is:", new ArrayList<>(List.of(new Course[]{advanced1})),
                english, 3, (new String[]{"Accomodate", "Acommodate", "Acommmodate", "Accommodate"}));

        Question enQ4 = new Question("I enjoy __________ books.", new ArrayList<>(List.of(new Course[]{advanced2})),
                english, 2, (new String[]{"read", "reads", "reading", "to read"}));

        Question enQ5 = new Question("The correct spelling is:", new ArrayList<>(List.of(new Course[]{advanced2})),
                english, 2, (new String[]{"Recieve", "Recieve", "Receive", "Recieve"}));

        Question enQ6 = new Question("Correct the spelling:", new ArrayList<>(List.of(new Course[]{advanced2})),
                english, 2, (new String[]{"Neccessary ", "Necesary ", "Necessary", "Neccessary"}));

        session.save(enQ1);
        session.save(enQ2);
        session.save(enQ3);
        session.save(enQ4);
        session.save(enQ5);
        session.save(enQ6);
        session.flush();

        //graphics
        Question grQ1 = new Question("___ is NOT commonly used?", new ArrayList<>(List.of(new Course[]{graphics})),
                cs, 3, (new String[]{"Phong shading", "Gouraud shading", "Lambertian shading", "Pixel shading"}));

        Question grQ2 = new Question("What is \"GPU\"?", new ArrayList<>(List.of(new Course[]{graphics})),
                cs, 2, (new String[]{"Graphical Processing Unit", "General Purpose Unit", "Graphics Processing Unit", "Graphical Performance Unit"}));

        Question grQ3 = new Question("converting 3D objects into 2D is:", new ArrayList<>(List.of(new Course[]{graphics})),
                cs, 2, (new String[]{"Animation", "Modeling", "Rendering", "Texturing"}));

        session.save(grQ1);
        session.save(grQ2);
        session.save(grQ3);
        session.flush();

        // Connect questions with subjects and courses
        math.setQuestionList(new ArrayList<>(List.of(new Question[]{mathQ1, mathQ2, mathQ3, mathQ4, mathQ5, mathQ6})));
        session.save(math);

        algebra.setQuestionList(new ArrayList<>(List.of(new Question[]{mathQ1, mathQ2, mathQ4, mathQ5, mathQ6})));
        session.save(algebra);

        calculus.setQuestionList(new ArrayList<>(List.of(new Question[]{mathQ1, mathQ3, mathQ4})));
        session.save(calculus);

        cs.setQuestionList(new ArrayList<>(List.of(new Question[]{csQ1, csQ2, csQ3, csQ4, csQ5, csQ6})));
        session.save(cs);

        intro.setQuestionList(new ArrayList<>(List.of(new Question[]{csQ1, csQ4, csQ6})));
        session.save(intro);

        algo.setQuestionList(new ArrayList<>(List.of(new Question[]{csQ2, csQ4, csQ6})));
        session.save(algo);

        cv.setQuestionList(new ArrayList<>(List.of(new Question[]{csQ2, csQ3, csQ5})));
        session.save(cv);

        graphics.setQuestionList(new ArrayList<>(List.of(new Question[]{csQ2, csQ5, grQ1, grQ2, grQ3})));
        session.save(graphics);

        os.setQuestionList(new ArrayList<>(List.of(new Question[]{osQ1, osQ2})));
        session.save(os);

        ca.setQuestionList(new ArrayList<>(List.of(new Question[]{caQ1, caQ2, caQ3})));
        session.save(ca);

        advanced1.setQuestionList(new ArrayList<>(List.of(new Question[]{enQ1, enQ2, enQ3})));
        session.save(advanced1);

        advanced2.setQuestionList(new ArrayList<>(List.of(new Question[]{enQ4, enQ5, enQ6})));
        session.save(advanced2);

        session.flush();

        // Add exams
        List<Question> algebraQuestions = algebra.getQuestionList();
        List<Integer> points = new ArrayList<>(List.of(new Integer[]{20, 20, 20, 20, 20}));
        Exam algebraExam = new Exam("Algebra Exam moed a", 1, 60, or, "No comment!", "No Comment!", algebra, algebraQuestions, points);
        or.getCreatedExams().add(algebraExam);
        algebra.getExamList().add(algebraExam);
        session.save(algebraExam);
        session.save(or);
        session.save(algebra);
        for (Question q : algebraQuestions) {
            q.getExamList().add(algebraExam);
            session.save(q);
        }
        session.flush();

        Exam algebraExamB = new Exam("Algebra Exam moed b", 2, 60, or, "No comment!", "No Comment!", algebra, algebraQuestions, points);
        or.getCreatedExams().add(algebraExamB);
        algebra.getExamList().add(algebraExamB);
        session.save(algebraExamB);
        session.save(or);
        session.save(algebra);
        for (Question q : algebraQuestions) {
            q.getExamList().add(algebraExamB);
            session.save(q);
        }
        session.flush();

        List<Question> osQuestions = os.getQuestionList();
        List<Integer> osPoints = new ArrayList<>(List.of(new Integer[]{51, 49}));
        Exam osExam = new Exam("Operating Systems moed a", 1, 35, dan, "No comment!", "No    !", os, osQuestions, osPoints);
        dan.getCreatedExams().add(osExam);
        os.getExamList().add(osExam);
        session.save(osExam);
        session.save(dan);
        session.save(os);
        for (Question q : osQuestions) {
            q.getExamList().add(osExam);
            session.save(q);
        }
        session.flush();

        List<Question> caQuestions = ca.getQuestionList();
        List<Integer> caPoints = new ArrayList<>(List.of(new Integer[]{25, 30, 45}));
        Exam caExam = new Exam("Computer Architecture moed a", 1, 45, malki, "No comment!", "No    !", ca, caQuestions, caPoints);
        malki.getCreatedExams().add(caExam);
        ca.getExamList().add(caExam);
        session.save(caExam);
        session.save(malki);
        session.save(ca);
        for (Question q : caQuestions) {
            q.getExamList().add(caExam);
            session.save(q);
        }
        session.flush();

        List<Question> grQuestions = graphics.getQuestionList();
        List<Integer> grPoints = new ArrayList<>(List.of(new Integer[]{20, 20, 20, 20, 20}));
        Exam grExam = new Exam("Computer Graphics moed a", 1, 50, roi, "No comment!", "No    !", graphics, grQuestions, grPoints);
        roi.getCreatedExams().add(grExam);
        graphics.getExamList().add(grExam);
        session.save(grExam);
        session.save(roi);
        session.save(graphics);
        for (Question q : grQuestions) {
            q.getExamList().add(grExam);
            session.save(q);
        }
        session.flush();

        List<Question> en1Questions = advanced1.getQuestionList();
        List<Integer> en1Points = new ArrayList<>(List.of(new Integer[]{33, 33, 34}));
        Exam en1Exam = new Exam("Advanced 1 moed a", 1, 30, dalal, "No comment!", "", advanced1, en1Questions, en1Points);
        dalal.getCreatedExams().add(en1Exam);
        advanced1.getExamList().add(en1Exam);
        session.save(en1Exam);
        session.save(dalal);
        session.save(advanced1);
        for (Question q : en1Questions) {
            q.getExamList().add(en1Exam);
            session.save(q);
        }
        session.flush();

        List<Question> en2Questions = advanced2.getQuestionList();
        List<Integer> en2Points = new ArrayList<>(List.of(new Integer[]{33, 33, 34}));
        Exam en2Exam = new Exam("Advanced 2 moed a", 1, 30, dalal, "No comment!", "", advanced2, en2Questions, en2Points);
        dalal.getCreatedExams().add(en2Exam);
        advanced2.getExamList().add(en2Exam);
        session.save(en2Exam);
        session.save(dalal);
        session.save(advanced2);
        for (Question q : en2Questions) {
            q.getExamList().add(en2Exam);
            session.save(q);
        }
        session.flush();

        // Add executables
        ExecutableExam executableAlgebra = new ExecutableExam("1000", algebraExam, or);
        or.getExamList().add(executableAlgebra);
        session.save(executableAlgebra);
        session.save(or);
        session.flush();

        ExecutableExam executableAlgebraB = new ExecutableExam("1001", algebraExamB, dan);
        dan.getExamList().add(executableAlgebraB);
        session.save(executableAlgebraB);
        session.save(dan);
        session.flush();

        ExecutableExam executableOS = new ExecutableExam("1002", osExam, or);
        dan.getExamList().add(executableOS);
        session.save(executableOS);
        session.save(dan);
        session.flush();

        ExecutableExam executableCA = new ExecutableExam("1003", caExam, malki);
        malki.getExamList().add(executableCA);
        session.save(executableCA);
        session.save(malki);
        session.flush();

        ExecutableExam executableCG = new ExecutableExam("1004", grExam, shir);
        roi.getExamList().add(executableCG);
        session.save(executableCG);
        session.save(roi);
        session.flush();

        ExecutableExam executableEN1 = new ExecutableExam("1005", en1Exam, dalal);
        dalal.getExamList().add(executableEN1);
        session.save(executableEN1);
        session.save(dalal);
        session.flush();

        ExecutableExam executableEN2 = new ExecutableExam("1006", en2Exam, dalal);
        dalal.getExamList().add(executableEN2);
        session.save(executableEN2);
        session.save(dalal);
        session.flush();

        // Add results
        HashMap<Question, Integer> algebraanswers1 = new HashMap<>();
        algebraanswers1.put(executableAlgebra.getExam().getQuestionList().get(0), 0);
        algebraanswers1.put(executableAlgebra.getExam().getQuestionList().get(1), 2);
        algebraanswers1.put(executableAlgebra.getExam().getQuestionList().get(2), 0);
        algebraanswers1.put(executableAlgebra.getExam().getQuestionList().get(3), 2);
        algebraanswers1.put(executableAlgebra.getExam().getQuestionList().get(4), 0);

        Result result1 = new Result(100, lana, "", executableAlgebra, 45, false, algebraanswers1);
        lana.getExamList().add(executableAlgebra);
        lana.getResultList().add(result1);
        executableAlgebra.getStudentList().add(lana);
        session.save(result1);
        session.save(lana);
        session.save(executableAlgebra);
        session.flush();

        Result result30 = new Result(100, ebraheem, "", executableAlgebra, 60, true, algebraanswers1);
        ebraheem.getExamList().add(executableAlgebra);
        ebraheem.getResultList().add(result30);
        executableAlgebra.getStudentList().add(ebraheem);
        session.save(result30);
        session.save(ebraheem);
        session.save(executableAlgebra);
        result30.setStatus(true);
        result30.setTeacherNote("Well Done!");
        session.save(result30);
        session.flush();

        Result result31 = new Result(100, zinab, "", executableAlgebra, 60, true, algebraanswers1);
        zinab.getExamList().add(executableAlgebra);
        zinab.getResultList().add(result31);
        executableAlgebra.getStudentList().add(zinab);
        session.save(result31);
        session.save(zinab);
        session.save(executableAlgebra);
        result31.setStatus(true);
        result31.setTeacherNote("Well Done!");
        session.save(result31);
        session.flush();

        Result result32 = new Result(100, ava, "", executableAlgebra, 60, true, algebraanswers1);
        ava.getExamList().add(executableAlgebra);
        ava.getResultList().add(result32);
        executableAlgebra.getStudentList().add(ava);
        session.save(result32);
        session.save(ava);
        session.save(executableAlgebra);
        result32.setStatus(true);
        result32.setTeacherNote("Well Done!");
        session.save(result32);
        session.flush();

        Result result33 = new Result(100, mohamed, "", executableAlgebra, 58, false, algebraanswers1);
        mohamed.getExamList().add(executableAlgebra);
        mohamed.getResultList().add(result33);
        executableAlgebra.getStudentList().add(mohamed);
        session.save(result33);
        session.save(mohamed);
        session.save(executableAlgebra);
        result33.setStatus(true);
        result33.setTeacherNote("Well Done!");
        session.save(result33);
        session.flush();

        Result result2 = new Result(100, alaa, "", executableAlgebra, 50, false, algebraanswers1);
        alaa.getExamList().add(executableAlgebra);
        alaa.getResultList().add(result2);
        executableAlgebra.getStudentList().add(alaa);
        session.save(result2);
        session.save(alaa);
        session.save(executableAlgebra);
        result2.setStatus(true);
        result2.setTeacherNote("Well Done!");
        session.save(result2);
        session.flush();


        Result result3 = new Result(100, ahed, "", executableAlgebra, 60, true, algebraanswers1);
        ahed.getExamList().add(executableAlgebra);
        ahed.getResultList().add(result3);
        executableAlgebra.getStudentList().add(ahed);
        session.save(result3);
        session.save(ahed);
        session.save(executableAlgebra);
        session.flush();

        HashMap<Question, Integer> algebraanswers2 = new HashMap<>();
        algebraanswers2.put(executableAlgebra.getExam().getQuestionList().get(0), 0);
        algebraanswers2.put(executableAlgebra.getExam().getQuestionList().get(1), 0);
        algebraanswers2.put(executableAlgebra.getExam().getQuestionList().get(2), 0);
        algebraanswers2.put(executableAlgebra.getExam().getQuestionList().get(3), 2);
        algebraanswers2.put(executableAlgebra.getExam().getQuestionList().get(4), 0);

        Result result18 = new Result(80, fadi, "", executableAlgebra, 45, false, algebraanswers2);
        fadi.getExamList().add(executableAlgebra);
        fadi.getResultList().add(result18);
        executableAlgebra.getStudentList().add(fadi);
        session.save(result18);
        session.save(fadi);
        session.save(executableAlgebra);
        session.flush();

        Result result34 = new Result(80, Sophia, "", executableAlgebra, 45, false, algebraanswers2);
        Sophia.getExamList().add(executableAlgebra);
        Sophia.getResultList().add(result34);
        executableAlgebra.getStudentList().add(Sophia);
        session.save(result34);
        session.save(Sophia);
        session.save(executableAlgebra);
        session.flush();

        Result result35 = new Result(80, mona, "", executableAlgebra, 39, false, algebraanswers2);
        mona.getExamList().add(executableAlgebra);
        mona.getResultList().add(result35);
        executableAlgebra.getStudentList().add(mona);
        session.save(result35);
        session.save(mona);
        session.save(executableAlgebra);
        session.flush();

        HashMap<Question, Integer> algebraanswers3 = new HashMap<>();
        algebraanswers3.put(executableAlgebra.getExam().getQuestionList().get(0), 3);
        algebraanswers3.put(executableAlgebra.getExam().getQuestionList().get(1), 3);
        algebraanswers3.put(executableAlgebra.getExam().getQuestionList().get(2), 0);
        algebraanswers3.put(executableAlgebra.getExam().getQuestionList().get(3), 2);
        algebraanswers3.put(executableAlgebra.getExam().getQuestionList().get(4), 0);

        Result result19 = new Result(40, diab, "", executableAlgebra, 45, false, algebraanswers3);
        diab.getExamList().add(executableAlgebra);
        diab.getResultList().add(result19);
        executableAlgebra.getStudentList().add(diab);
        session.save(result19);
        session.save(diab);
        session.save(executableAlgebra);
        session.flush();

        Result result20 = new Result(40, yosef, "", executableAlgebra, 45, false, algebraanswers3);
        yosef.getExamList().add(executableAlgebra);
        yosef.getResultList().add(result20);
        executableAlgebra.getStudentList().add(yosef);
        session.save(result20);
        session.save(yosef);
        session.save(executableAlgebra);
        result20.setStatus(true);
        result20.setTeacherNote("You can do better");
        session.save(result20);
        session.flush();

        Result result36 = new Result(40, hasan, "", executableAlgebra, 48, false, algebraanswers3);
        hasan.getExamList().add(executableAlgebra);
        hasan.getResultList().add(result36);
        executableAlgebra.getStudentList().add(hasan);
        session.save(result36);
        session.save(hasan);
        session.save(executableAlgebra);
        result36.setStatus(true);
        result36.setTeacherNote("You can do better");
        session.save(result36);
        session.flush();

        int[] distribution = new int[]{0, 0, 0, 3, 0, 0, 0, 3, 0, 7};
        executableAlgebra.setDistribution(distribution);
        executableAlgebra.setAverage(81.53);
        executableAlgebra.setMedian(100);
        session.save(executableAlgebra);
        session.flush();

        //algebra b results
        Result result4 = new Result(100, ebraheem, "", executableAlgebraB, 45, false, algebraanswers1);
        ebraheem.getExamList().add(executableAlgebraB);
        ebraheem.getResultList().add(result4);
        executableAlgebraB.getStudentList().add(ebraheem);
        session.save(result4);
        session.save(ebraheem);
        session.save(executableAlgebraB);
        result4.setStatus(true);
        result4.setTeacherNote("Well Done!");
        session.save(result4);
        session.flush();

        Result result21 = new Result(100, adan, "", executableAlgebraB, 45, false, algebraanswers1);
        adan.getExamList().add(executableAlgebraB);
        adan.getResultList().add(result21);
        executableAlgebraB.getStudentList().add(adan);
        session.save(result21);
        session.save(adan);
        session.save(executableAlgebraB);
        result21.setStatus(true);
        result21.setTeacherNote("Amazing");
        session.save(result21);
        session.flush();

        Result result22 = new Result(40, mona, "", executableAlgebraB, 45, false, algebraanswers3);
        mona.getExamList().add(executableAlgebraB);
        mona.getResultList().add(result22);
        executableAlgebraB.getStudentList().add(mona);
        session.save(result22);
        session.save(mona);
        session.save(executableAlgebraB);
        result22.setStatus(true);
        result22.setTeacherNote("You can do better");
        session.save(result22);
        session.flush();

        Result result23 = new Result(80, diab, "", executableAlgebraB, 45, false, algebraanswers2);
        diab.getExamList().add(executableAlgebraB);
        diab.getResultList().add(result23);
        executableAlgebraB.getStudentList().add(diab);
        session.save(result23);
        session.save(diab);
        session.save(executableAlgebraB);
        result23.setStatus(true);
        result23.setTeacherNote("Good job");
        session.save(result23);
        session.flush();

        distribution = new int[]{0, 0, 0, 1, 0, 0, 0, 1, 0, 2};
        executableAlgebraB.setDistribution(distribution);
        executableAlgebraB.setMedian(85);
        executableAlgebraB.setAverage(80);
        session.save(executableAlgebraB);
        session.flush();

        //os results:
        HashMap<Question, Integer> osAnswers = new HashMap<>();
        osAnswers.put(executableOS.getExam().getQuestionList().get(0), 0);
        osAnswers.put(executableOS.getExam().getQuestionList().get(1), 0);

        Result result5 = new Result(0, mona, "", executableOS, 13, false, osAnswers);
        mona.getExamList().add(executableOS);
        mona.getResultList().add(result5);
        executableOS.getStudentList().add(mona);
        session.save(result5);
        session.save(mona);
        session.save(executableOS);
        session.flush();

        HashMap<Question, Integer> osAnswers1 = new HashMap<>();
        osAnswers1.put(executableOS.getExam().getQuestionList().get(0), 3);
        osAnswers1.put(executableOS.getExam().getQuestionList().get(1), 0);

        Result result6 = new Result(51, zinab, "", executableOS, 36, true, osAnswers1);
        zinab.getExamList().add(executableOS);
        zinab.getResultList().add(result6);
        executableOS.getStudentList().add(zinab);
        session.save(result6);
        session.save(zinab);
        session.save(executableOS);
        session.flush();

        Result result7 = new Result(51, alaa, "", executableOS, 15, false, osAnswers1);
        alaa.getExamList().add(executableOS);
        alaa.getResultList().add(result7);
        executableOS.getStudentList().add(alaa);
        session.save(result7);
        session.save(alaa);
        session.save(executableOS);
        result7.setStatus(true);
        result7.setTeacherNote("Not good");
        session.save(result7);
        session.flush();

        HashMap<Question, Integer> osAnswers2 = new HashMap<>();
        osAnswers2.put(executableOS.getExam().getQuestionList().get(0), 3);
        osAnswers2.put(executableOS.getExam().getQuestionList().get(1), 1);

        Result result8 = new Result(100, ahed, "", executableOS, 30, false, osAnswers2);
        ahed.getExamList().add(executableOS);
        ahed.getResultList().add(result8);
        executableOS.getStudentList().add(ahed);
        session.save(result8);
        session.save(ahed);
        session.save(executableOS);
        session.flush();

        Result result9 = new Result(100, lana, "", executableOS, 20, false, osAnswers);
        lana.getExamList().add(executableOS);
        lana.getResultList().add(result9);
        executableOS.getStudentList().add(lana);
        session.save(result9);
        session.save(lana);
        session.save(executableOS);
        session.flush();

        Result result10 = new Result(100, diab, "", executableOS, 20, false, osAnswers2);
        diab.getExamList().add(executableOS);
        diab.getResultList().add(result10);
        executableOS.getStudentList().add(diab);
        session.save(result10);
        session.save(diab);
        session.save(executableOS);
        session.flush();

        distribution = new int[]{1, 0, 0, 0, 0, 2, 0, 0, 0, 3};
        executableOS.setDistribution(distribution);
        executableOS.setMedian(75.50);
        executableOS.setAverage(67);
        session.save(executableOS);
        session.flush();

        // CA results:
        HashMap<Question, Integer> caAnswers1 = new HashMap<>();
        caAnswers1.put(executableCA.getExam().getQuestionList().get(0), 0);
        caAnswers1.put(executableCA.getExam().getQuestionList().get(1), 0);
        caAnswers1.put(executableCA.getExam().getQuestionList().get(2), 0);

        Result result11 = new Result(0, diab, "", executableCA, 20, false, caAnswers1);
        diab.getExamList().add(executableCA);
        diab.getResultList().add(result11);
        executableCA.getStudentList().add(diab);
        session.save(result11);
        session.save(diab);
        session.save(executableCA);
        session.flush();

        Result result12 = new Result(0, ebraheem, "", executableCA, 10, false, caAnswers1);
        ebraheem.getExamList().add(executableCA);
        ebraheem.getResultList().add(result12);
        executableCA.getStudentList().add(ebraheem);
        session.save(result12);
        session.save(ebraheem);
        session.save(executableCA);
        session.flush();

        HashMap<Question, Integer> caAnswers2 = new HashMap<>();
        caAnswers2.put(executableCA.getExam().getQuestionList().get(0), 1);
        caAnswers2.put(executableCA.getExam().getQuestionList().get(1), 2);
        caAnswers2.put(executableCA.getExam().getQuestionList().get(2), 0);

        Result result13 = new Result(55, lana, "", executableCA, 35, false, caAnswers2);
        lana.getExamList().add(executableCA);
        lana.getResultList().add(result13);
        executableCA.getStudentList().add(lana);
        session.save(result13);
        session.save(lana);
        session.save(executableCA);
        session.flush();

        Result result14 = new Result(55, fadi, "", executableCA, 35, false, caAnswers2);
        fadi.getExamList().add(executableCA);
        fadi.getResultList().add(result14);
        executableCA.getStudentList().add(fadi);
        session.save(result14);
        session.save(fadi);
        session.save(executableCA);
        session.flush();

        HashMap<Question, Integer> caAnswers3 = new HashMap<>();
        caAnswers3.put(executableCA.getExam().getQuestionList().get(0), 1);
        caAnswers3.put(executableCA.getExam().getQuestionList().get(1), 3);
        caAnswers3.put(executableCA.getExam().getQuestionList().get(2), 2);

        Result result15 = new Result(70, fadi, "", executableCA, 35, false, caAnswers3);
        fadi.getExamList().add(executableCA);
        fadi.getResultList().add(result15);
        executableCA.getStudentList().add(fadi);
        session.save(result15);
        session.save(fadi);
        session.save(executableCA);
        session.flush();

        Result result16 = new Result(70, adan, "", executableCA, 30, false, caAnswers3);
        adan.getExamList().add(executableCA);
        adan.getResultList().add(result16);
        executableCA.getStudentList().add(adan);
        session.save(result16);
        session.save(adan);
        session.save(executableCA);
        session.flush();

        HashMap<Question, Integer> caAnswers4 = new HashMap<>();
        caAnswers4.put(executableCA.getExam().getQuestionList().get(0), 1);
        caAnswers4.put(executableCA.getExam().getQuestionList().get(1), 2);
        caAnswers4.put(executableCA.getExam().getQuestionList().get(2), 2);

        Result result17 = new Result(100, alaa, "", executableCA, 40, false, caAnswers4);
        alaa.getExamList().add(executableCA);
        alaa.getResultList().add(result17);
        executableCA.getStudentList().add(alaa);
        session.save(result17);
        session.save(alaa);
        session.save(executableCA);
        result17.setStatus(true);
        result17.setTeacherNote("Amazing!");
        session.save(result17);
        session.flush();

        distribution = new int[]{2, 0, 0, 0, 0, 2, 2, 0, 0, 1};
        executableCA.setDistribution(distribution);
        executableCA.setMedian(55);
        executableCA.setAverage(50);
        session.save(executableCA);
        session.flush();

        //graphics results:
        HashMap<Question, Integer> grAnswers = new HashMap<>();
        grAnswers.put(executableCG.getExam().getQuestionList().get(0), 3);
        grAnswers.put(executableCG.getExam().getQuestionList().get(1), 3);
        grAnswers.put(executableCG.getExam().getQuestionList().get(2), 3);
        grAnswers.put(executableCG.getExam().getQuestionList().get(3), 2);
        grAnswers.put(executableCG.getExam().getQuestionList().get(4), 3);

        Result grResult1 = new Result(80, alaa, "", executableCG, 40, false, grAnswers);
        alaa.getExamList().add(executableCG);
        alaa.getResultList().add(grResult1);
        executableCG.getStudentList().add(alaa);
        session.save(grResult1);
        session.save(alaa);
        session.save(executableCG);
        grResult1.setStatus(true);
        grResult1.setTeacherNote("Amazing!");
        session.save(grResult1);
        session.flush();

        Result grResult3 = new Result(80, mohamed, "", executableCG, 50, true, grAnswers);
        mohamed.getExamList().add(executableCG);
        mohamed.getResultList().add(grResult3);
        executableCG.getStudentList().add(mohamed);
        session.save(grResult3);
        session.save(mohamed);
        session.save(executableCG);
        grResult3.setStatus(true);
        grResult3.setTeacherNote("Good job!");
        session.save(grResult3);
        session.flush();

        HashMap<Question, Integer> grAnswers1 = new HashMap<>();
        grAnswers1.put(executableCG.getExam().getQuestionList().get(0), 3);
        grAnswers1.put(executableCG.getExam().getQuestionList().get(1), 3);
        grAnswers1.put(executableCG.getExam().getQuestionList().get(2), 3);
        grAnswers1.put(executableCG.getExam().getQuestionList().get(3), 2);
        grAnswers1.put(executableCG.getExam().getQuestionList().get(4), 2);

        Result grResult2 = new Result(100, lana, "", executableCG, 40, false, grAnswers1);
        lana.getExamList().add(executableCG);
        lana.getResultList().add(grResult2);
        executableCG.getStudentList().add(lana);
        session.save(grResult2);
        session.save(lana);
        session.save(executableCG);
        grResult2.setStatus(true);
        grResult2.setTeacherNote("Wow!");
        session.save(grResult2);
        session.flush();

        Result grResult22 = new Result(100, zinab, "", executableCG, 36, false, grAnswers1);
        zinab.getExamList().add(executableCG);
        zinab.getResultList().add(grResult22);
        executableCG.getStudentList().add(zinab);
        session.save(grResult22);
        session.save(zinab);
        session.save(executableCG);
        grResult22.setStatus(true);
        grResult22.setTeacherNote("Wow!!");
        session.save(grResult22);
        session.flush();

        Result grResult23 = new Result(100, adan, "", executableCG, 35, false, grAnswers1);
        adan.getExamList().add(executableCG);
        adan.getResultList().add(grResult23);
        executableCG.getStudentList().add(adan);
        session.save(grResult23);
        session.save(adan);
        session.save(executableCG);
        grResult23.setStatus(true);
        grResult23.setTeacherNote("Wow!!");
        session.save(grResult23);
        session.flush();

        HashMap<Question, Integer> grAnswers2 = new HashMap<>();
        grAnswers2.put(executableCG.getExam().getQuestionList().get(0), 3);
        grAnswers2.put(executableCG.getExam().getQuestionList().get(1), 1);
        grAnswers2.put(executableCG.getExam().getQuestionList().get(2), 1);
        grAnswers2.put(executableCG.getExam().getQuestionList().get(3), 2);
        grAnswers2.put(executableCG.getExam().getQuestionList().get(4), 2);

        Result grResult4 = new Result(60, mona, "", executableCG, 38, false, grAnswers2);
        mona.getExamList().add(executableCG);
        mona.getResultList().add(grResult4);
        executableCG.getStudentList().add(mona);
        session.save(grResult4);
        session.save(mona);
        session.save(executableCG);
        session.flush();

        Result grResult44 = new Result(60, ava, "", executableCG, 20, false, grAnswers2);
        ava.getExamList().add(executableCG);
        ava.getResultList().add(grResult44);
        executableCG.getStudentList().add(ava);
        session.save(grResult44);
        session.save(ava);
        session.save(executableCG);
        session.flush();

        Result grResult45 = new Result(60, lora, "", executableCG, 20, false, grAnswers2);
        lora.getExamList().add(executableCG);
        lora.getResultList().add(grResult45);
        executableCG.getStudentList().add(lora);
        session.save(grResult45);
        session.save(lora);
        session.save(executableCG);
        session.flush();

        HashMap<Question, Integer> grAnswers3 = new HashMap<>();
        grAnswers3.put(executableCG.getExam().getQuestionList().get(0), 3);
        grAnswers3.put(executableCG.getExam().getQuestionList().get(1), 1);
        grAnswers3.put(executableCG.getExam().getQuestionList().get(2), 1);
        grAnswers3.put(executableCG.getExam().getQuestionList().get(3), 3);
        grAnswers3.put(executableCG.getExam().getQuestionList().get(4), 2);

        Result grResult5 = new Result(40, fadi, "", executableCG, 12, false, grAnswers3);
        fadi.getExamList().add(executableCG);
        fadi.getResultList().add(grResult5);
        executableCG.getStudentList().add(fadi);
        session.save(grResult5);
        session.save(fadi);
        session.save(executableCG);
        session.flush();

        Result grResult55 = new Result(40, ahed, "", executableCG, 12, false, grAnswers3);
        ahed.getExamList().add(executableCG);
        ahed.getResultList().add(grResult55);
        executableCG.getStudentList().add(ahed);
        session.save(grResult55);
        session.save(ahed);
        session.save(executableCG);
        session.flush();

        Result grResult6 = new Result(40, diab, "", executableCG, 50, true, grAnswers3);
        diab.getExamList().add(executableCG);
        diab.getResultList().add(grResult6);
        executableCG.getStudentList().add(diab);
        session.save(grResult6);
        session.save(diab);
        session.save(executableCG);
        session.flush();

        distribution = new int[]{0, 0, 0, 3, 0, 3, 0, 2, 0, 3};
        executableCG.setDistribution(distribution);
        executableCG.setMedian(60);
        executableCG.setAverage(69.09);
        session.save(executableCG);
        session.flush();

        //english results:
        HashMap<Question, Integer> enAnswers1 = new HashMap<>();
        enAnswers1.put(executableEN1.getExam().getQuestionList().get(0), 3);
        enAnswers1.put(executableEN1.getExam().getQuestionList().get(1), 1);
        enAnswers1.put(executableEN1.getExam().getQuestionList().get(2), 1);

        Result en1Result1 = new Result(0, fadi, "", executableEN1, 12, false, enAnswers1);
        fadi.getExamList().add(executableEN1);
        fadi.getResultList().add(en1Result1);
        executableEN1.getStudentList().add(fadi);
        session.save(en1Result1);
        session.save(fadi);
        session.save(executableEN1);
        session.flush();

        Result en1Result2 = new Result(0, ava, "", executableEN1, 17, false, enAnswers1);
        ava.getExamList().add(executableEN1);
        ava.getResultList().add(en1Result2);
        executableEN1.getStudentList().add(ava);
        session.save(en1Result2);
        session.save(ava);
        session.save(executableEN1);
        session.flush();

        Result en1Result3 = new Result(0, lora, "", executableEN1, 30, true, enAnswers1);
        lora.getExamList().add(executableEN1);
        lora.getResultList().add(en1Result3);
        executableEN1.getStudentList().add(lora);
        session.save(en1Result3);
        session.save(lora);
        session.save(executableEN1);
        session.flush();

        Result en1Result4 = new Result(0, hasan, "", executableEN1, 30, true, enAnswers1);
        hasan.getExamList().add(executableEN1);
        hasan.getResultList().add(en1Result4);
        executableEN1.getStudentList().add(hasan);
        session.save(en1Result4);
        session.save(hasan);
        session.save(executableEN1);
        session.flush();

        HashMap<Question, Integer> enAnswers2 = new HashMap<>();
        enAnswers2.put(executableEN1.getExam().getQuestionList().get(0), 1);
        enAnswers2.put(executableEN1.getExam().getQuestionList().get(1), 1);
        enAnswers2.put(executableEN1.getExam().getQuestionList().get(2), 1);

        Result en1Result5 = new Result(33, Sophia, "", executableEN1, 16, false, enAnswers2);
        Sophia.getExamList().add(executableEN1);
        Sophia.getResultList().add(en1Result5);
        executableEN1.getStudentList().add(Sophia);
        session.save(en1Result5);
        session.save(Sophia);
        session.save(executableEN1);
        session.flush();

        HashMap<Question, Integer> enAnswers3 = new HashMap<>();
        enAnswers3.put(executableEN1.getExam().getQuestionList().get(0), 1);
        enAnswers3.put(executableEN1.getExam().getQuestionList().get(1), 3);
        enAnswers3.put(executableEN1.getExam().getQuestionList().get(2), 1);

        Result en1Result6 = new Result(66, mohamed, "", executableEN1, 30, true, enAnswers3);
        mohamed.getExamList().add(executableEN1);
        mohamed.getResultList().add(en1Result6);
        executableEN1.getStudentList().add(mohamed);
        session.save(en1Result6);
        session.save(mohamed);
        session.save(executableEN1);
        session.flush();

        Result en1Result7 = new Result(66, alaa, "", executableEN1, 25, false, enAnswers3);
        alaa.getExamList().add(executableEN1);
        alaa.getResultList().add(en1Result7);
        executableEN1.getStudentList().add(alaa);
        session.save(en1Result7);
        session.save(alaa);
        session.save(executableEN1);
        session.flush();

        HashMap<Question, Integer> enAnswers4 = new HashMap<>();
        enAnswers4.put(executableEN1.getExam().getQuestionList().get(0), 1);
        enAnswers4.put(executableEN1.getExam().getQuestionList().get(1), 3);
        enAnswers4.put(executableEN1.getExam().getQuestionList().get(2), 3);

        Result en1Result8 = new Result(100, alaa, "", executableEN1, 30, true, enAnswers4);
        alaa.getExamList().add(executableEN1);
        alaa.getResultList().add(en1Result8);
        executableEN1.getStudentList().add(alaa);
        session.save(en1Result8);
        session.save(alaa);
        session.save(executableEN1);
        en1Result8.setTeacherNote("Good job");
        en1Result8.setStatus(true);
        session.save(en1Result8);
        session.flush();

        distribution = new int[]{4, 0, 0, 1, 0, 0, 2, 0, 0, 1};
        executableEN1.setDistribution(distribution);
        executableEN1.setMedian(16.50);
        executableEN1.setAverage(33.12);
        session.save(executableEN1);
        session.flush();

        session.getTransaction().commit();
    }
}

