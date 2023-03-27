package nl.tudelft.cse.sem.user.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Set;
import nl.tudelft.cse.sem.user.models.Student;

public class StudentSerializer extends StdSerializer<Student> {
    private static final long serialVersionUID = -2835263995679556560L;

    protected StudentSerializer(Class<Student> t) {
        super(t);
    }

    public StudentSerializer() {
        this(null);
    }

    /**
     * This method is used to properly serialize student
     * without including their password or any other irrelevant fields.
     *
     * @param student  to be serialized
     * @param jgen     data structure to serialize object in stream
     *                 -> serializes and writes to output stream at the same time
     * @param provider data structure helps to serialize somehow
     * @throws IOException can be thrown
     */
    @Override
    public void serialize(Student student, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("name", student.getName());
        jgen.writeStringField("email", student.getEmail());
        jgen.writeStringField("netid", student.getNetId());
        jgen.writeNumberField("studentNumber", student.getStudentNumber());

        jgen.writeFieldName("currentCourses");
        jgen.writeStartArray();
        for (String course : student.getCurrentCourses()) {
            jgen.writeString(course);
        }
        jgen.writeEndArray();

        jgen.writeFieldName("taCourses");
        jgen.writeStartArray();
        for (String course : student.getTaCourses()) {
            jgen.writeString(course);
        }
        jgen.writeEndArray();

        jgen.writeFieldName("grades");
        jgen.writeStartObject();
        Set<String> courses = student.getGrades().keySet();
        for (String course : courses) {
            jgen.writeNumberField(course, student.getGrades().get(course));
        }
        jgen.writeEndObject();

        jgen.writeEndObject();
    }
}
