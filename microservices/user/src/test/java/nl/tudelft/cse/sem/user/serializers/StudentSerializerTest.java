package nl.tudelft.cse.sem.user.serializers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import nl.tudelft.cse.sem.user.models.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SuppressWarnings("PMD")
public class StudentSerializerTest {

    @Test
    void emptyConstructorTest() {
        StudentSerializer studentSerializer = new StudentSerializer();
        assertNotNull(studentSerializer);
    }

    @Test
    void serializeTest() throws IOException {
        Student student = new Student();
        student.setName("Koen");
        student.setEmail("actuallyMyEmail");
        student.setNetId("netId");
        student.setStudentNumber(1L);

        Set<String> currentCourses = new HashSet<>();
        currentCourses.add("Course");
        student.setCurrentCourses(currentCourses);

        Set<String> taCourses = new HashSet<>();
        taCourses.add("Smort");
        student.setTaCourses(taCourses);

        Map<String, Double> grades = new HashMap<>();
        grades.put("Smort", 8.0);
        student.setGrades(grades);

        JsonGenerator generator = mock(JsonGenerator.class);
        SerializerProvider provider = mock(SerializerProvider.class);

        StudentSerializer serializer = new StudentSerializer();
        assertDoesNotThrow(() -> serializer.serialize(student, generator, provider));
        generator.close();
    }

    @Test
    public void serialiseStudent() throws JsonProcessingException, IOException {
        Student student = new Student();

        student.setNetId("amereuta");
        student.setPassword("password");
        student.setName("Andrew");
        student.setEmail("email");
        student.setStudentNumber(1L);
        student.setGrades(Map.of("CSE1100", 8.0, "CSE1300", 7.5, "CSE1400", 7.0,
                "CSE1505", 7.0, "CSE1500", 7.0));
        student.setCurrentCourses(Set.of("CSE2525", "CSE2310", "CSE2115"));
        student.setTaCourses(Set.of("CSE1100", "CSE4545"));

        Writer jsonWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
        SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
        new StudentSerializer().serialize(student, jsonGenerator, serializerProvider);
        jsonGenerator.flush();
        String s = jsonWriter.toString();

        Assertions.assertDoesNotThrow(() -> {new ObjectMapper().readValue(s, Object.class);});
        assertTrue(s.contains("name"));
        assertTrue(s.contains("email"));
        assertTrue(s.contains("currentCourses"));
        assertTrue(s.contains("taCourses"));
        assertTrue(s.contains("netid"));
        assertTrue(s.contains("studentNumber"));
        assertTrue(s.contains("grades"));
        assertTrue(s.contains("amereuta"));
        assertTrue(s.contains("Andrew"));
        assertTrue(s.contains("8.0"));
        assertTrue(s.contains("CSE1100"));
        assertTrue(s.contains("8.0"));
        assertTrue(s.contains("CSE2525"));
        assertTrue(s.contains("CSE4545"));
        assertTrue(s.contains("}"));
        assertTrue(s.contains("]"));
    }
}
