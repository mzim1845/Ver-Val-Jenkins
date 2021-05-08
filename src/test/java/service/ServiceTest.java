package service;

import domain.Grade;
import domain.Homework;
import domain.Pair;
import domain.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import validation.*;

import java.io.File;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServiceTest {

    private Service service;

    @org.junit.jupiter.api.BeforeAll
    void setUp() {
        Validator<Student> studentValidator = new StudentValidator();
        Validator<Homework> homeworkValidator = new HomeworkValidator();
        Validator<Grade> gradeValidator = new GradeValidator();

        StudentXMLRepository fileRepository1 = new StudentXMLRepository(studentValidator, "students.xml");
        HomeworkXMLRepository fileRepository2 = new HomeworkXMLRepository(homeworkValidator, "homework.xml");
        GradeXMLRepository fileRepository3 = new GradeXMLRepository(gradeValidator, "grades.xml");

        service = new Service(fileRepository1, fileRepository2, fileRepository3);
    }

    @org.junit.jupiter.api.AfterAll
    void rollbackUpdate() {
        service.updateStudent("2", "Mary", 222);
        service.updateHomework("2", "XML", 7, 3);
    }

    @org.junit.jupiter.api.Test
    void findAllStudents() {
        Iterator<Student> iterator = service.findAllStudents().iterator();
        assertAll(
                () -> assertEquals(iterator.next(), new Student("2", "Mary", 222)),
                () -> assertEquals(iterator.next(), new Student("4", "Ian", 227))
        );
    }

    @org.junit.jupiter.api.Test
    void findAllHomework() {
        Iterator<Homework> iterator = service.findAllHomework().iterator();
        assertAll(
                () -> assertEquals(iterator.next(), new Homework("1", "File", 7, 6)),
                () -> assertEquals(iterator.next(), new Homework("2", "XML", 8, 7)),
                () -> assertEquals(iterator.next(), new Homework("3", "gui", 13, 8))
        );
    }

    @org.junit.jupiter.api.Test
    void findAllGrades() {
        Iterator<Grade> iterator = service.findAllGrades().iterator();
        assertAll(
                () -> assertTrue(iterator.next().equals(new Grade(new Pair<>("1", "1"), 10.0, 7, "done"))),
                () -> assertTrue(iterator.next().equals(new Grade(new Pair<>("1", "2"), 7.5, 9, "done")))
        );
    }

    @DisplayName("Save valid student")
    @org.junit.jupiter.api.Test
    void saveValidStudent() {
        int result = service.saveStudent("10", "Joe", 224);
        assertEquals(result, 1);
        service.deleteStudent("10");
    }

    @DisplayName("Save invalid student")
    @org.junit.jupiter.api.Test
    void saveInvalidStudent() {
        int result = service.saveStudent("11", "", 1000);
        assertEquals(result, 0);
    }

    @DisplayName("Save valid homework")
    @org.junit.jupiter.api.Test
    void saveValidHomework() {
        int result = service.saveHomework("10", "Model", 12, 10);
        assertNotEquals(result, 0);
        service.deleteHomework("10");
    }

    @DisplayName("Save invalid homework")
    @org.junit.jupiter.api.Test
    void saveInvalidHomework() {
        int result = service.saveHomework("11", "Model", 4, 12);
        assertNotEquals(result, 1);
    }

    @DisplayName("Save valid grade")
    @org.junit.jupiter.api.Test
    void saveValidGrade() {
        int result = service.saveGrade("2", "2", 8.75, 11, "done");
        assertTrue(result > 0);
        service.deleteGrade("2", "2");
    }

    @DisplayName("Save invalid grade")
    @org.junit.jupiter.api.Test
    void saveInvalidGrade() {
        int result = service.saveGrade("10", "10", 11.2, 11, "done");
        assertTrue(result < 1);
    }

    @DisplayName("Delete student with valid ID")
    @org.junit.jupiter.api.Test
    void deleteValidStudent() {
        int result = service.deleteStudent("2");
        assertTrue(result == 1);
        service.saveStudent("2", "Mary", 222);
    }

    @DisplayName("Delete student with invalid ID")
    @org.junit.jupiter.api.Test
    void deleteInvalidStudent() {
        assertThrows(IllegalArgumentException.class,
                ()->service.deleteStudent(null));
//        int result = service.deleteStudent("9");
//        assertNotEquals(result, 1);
    }

    @DisplayName("Delete homework with valid ID")
    @org.junit.jupiter.api.Test
    void deleteValidHomework() {
        int result = service.deleteHomework("1");
        assertEquals(result, 1);
        service.saveHomework("1", "File", 7, 6);
    }

    @DisplayName("Delete homework with invalid ID")
    @org.junit.jupiter.api.Test
    void deleteInvalidHomework() {
        int result = service.deleteHomework("9");
        assertNotEquals(result, 1);
    }

    @DisplayName("Update valid student")
    @org.junit.jupiter.api.Test
    void updateValidStudent() {
        int result = service.updateStudent("2", "Ann", 300);
        assertEquals(result, 1);
    }

    @DisplayName("Update invalid student")
    @org.junit.jupiter.api.Test
    void updateInvalidStudent() {
        int result = service.updateStudent("2", "Ann", 980);
        assertEquals(result,0);
    }

    @DisplayName("Update valid homework")
    @org.junit.jupiter.api.Test
    void updateValidHomework() {
        int result = service.updateHomework("2", "File", 7, 3);
        assertEquals(result, 1);
    }

    @DisplayName("Update invalid homework")
    @org.junit.jupiter.api.Test
    void updateInvalidHomework() {
        int result = service.updateHomework("2", "", 7, 3);
        assertEquals(result, 0);
    }

    @org.junit.jupiter.api.Test
    void extendDeadline() {
        int result = service.extendDeadline("2", 2);
        assertTrue(result > 0);
    }

    @org.junit.jupiter.api.Test
    void createStudentFile() {
        service.createStudentFile("1", "1");
        File file = new File("ana.txt");
        assertTrue(file.exists());
        file.delete();

    }
}