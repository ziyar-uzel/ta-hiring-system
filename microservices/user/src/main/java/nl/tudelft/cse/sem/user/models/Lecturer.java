package nl.tudelft.cse.sem.user.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.tudelft.cse.sem.user.serializers.LecturerSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lecturer")
@JsonSerialize(using = LecturerSerializer.class)
//@EqualsAndHashCode(callSuper = false)
public class Lecturer extends User {

    static final long serialVersionUID = 1L;

    private Long employeeNumber;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private Set<String> courses = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority("ROLE_LECTURER"));
    }

    @Override
    public String getUsername() {
        return netId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lecturer)) {
            return false;
        }
        Lecturer lecturer = (Lecturer) o;
        return Objects.equals(employeeNumber, lecturer.employeeNumber)
                && Objects.equals(courses, lecturer.courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeNumber, courses);
    }
}
