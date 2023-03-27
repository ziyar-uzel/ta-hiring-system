package nl.tudelft.cse.sem.user.serializers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import nl.tudelft.cse.sem.user.models.Lecturer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SuppressWarnings("PMD")
public class LecturerSerializerTest {

    @Test
    void emptyConstructorTest() {
        LecturerSerializer lecturerSerializer = new LecturerSerializer();
        assertNotNull(lecturerSerializer);
    }

    @Test
    void lecturerSerializerNoExceptionTest()
            throws IOException {
        Lecturer lecturer = new Lecturer();
        lecturer.setName("Stefan");
        lecturer.setEmail("email");
        lecturer.setNetId("netidithink");
        lecturer.setEmployeeNumber(1L);

        Set<String> courses = new HashSet<>();
        courses.add("course");
        lecturer.setCourses(courses);

        JsonGenerator generator = mock(JsonGenerator.class);
        SerializerProvider provider = mock(SerializerProvider.class);

        LecturerSerializer lecturerSerializer = new LecturerSerializer();

        assertDoesNotThrow(() ->
                lecturerSerializer.serialize(lecturer, generator, provider));
        generator.close();
    }

    @Test
    public void serialiseLecturer() throws IOException {
        Lecturer lecturer = new Lecturer();

        lecturer.setNetId("lofi");
        lecturer.setEmail("lofi@email");
        lecturer.setPassword("password");
        lecturer.setName("Christoph");
        lecturer.setEmployeeNumber(1L);
        lecturer.setCourses(Set.of("CSE1505", "CSE2525", "CSE1500"));

        Writer jsonWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
        SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider();
        new LecturerSerializer().serialize(lecturer, jsonGenerator, serializerProvider);
        jsonGenerator.flush();
        String s = jsonWriter.toString();

        Assertions.assertDoesNotThrow(() -> {new ObjectMapper().readValue(s, Object.class);});
        assertTrue(s.contains("name"));
        assertTrue(s.contains("email"));
        assertTrue(s.contains("teachingCourses"));
        assertTrue(s.contains("netid"));
        assertTrue(s.contains("employeeNumber"));
        assertTrue(s.contains("lofi"));
        assertTrue(s.contains("Christoph"));
        assertTrue(s.contains("CSE1505"));
    }
}
