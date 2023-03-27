package nl.tudelft.cse.sem.user.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import nl.tudelft.cse.sem.user.models.Lecturer;

public class LecturerSerializer extends StdSerializer<Lecturer> {

    private static final long serialVersionUID = -2035267935679256369L;

    protected LecturerSerializer(Class<Lecturer> lecturerClass) {
        super(lecturerClass);
    }

    public LecturerSerializer() {
        this(null);
    }

    /**
     * This method is used to properly serialize lecturer
     * without including their password or any other irrelevant fields.
     *
     * @param lecturer to be serialized
     * @param gen data structure to serialize object in stream
     *            -> serializes and writes to output stream at the same time
     * @param provider data structure helps to serialize somehow
     * @throws IOException can be thrown
     */
    @Override
    public void serialize(Lecturer lecturer,
                          JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("name", lecturer.getName());
        gen.writeStringField("email", lecturer.getEmail());
        gen.writeStringField("netid", lecturer.getNetId());
        gen.writeNumberField("employeeNumber", lecturer.getEmployeeNumber());

        gen.writeFieldName("teachingCourses");
        gen.writeStartArray();
        for (String course : lecturer.getCourses()) {
            gen.writeString(course);
        }
        gen.writeEndArray();

        gen.writeEndObject();
    }
}
