package nl.tudelft.cse.sem.user;

import java.util.Map;
import java.util.Set;
import nl.tudelft.cse.sem.user.models.Lecturer;
import nl.tudelft.cse.sem.user.models.Student;
import nl.tudelft.cse.sem.user.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
public class UserMicroService {

    public static void main(String[] args) {
        SpringApplication.run(UserMicroService.class, args);
    }

    /**
     * Demo method to save user in userRepository.
     *
     * @param userRepository - The userRepository the save the user in
     * @param encoder - The encoder of the password
     * @return - The action in args
     */
    @Bean
    public CommandLineRunner demo(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        Student student = new Student();

        student.setNetId("amereuta");
        student.setPassword(encoder.encode("password"));
        student.setName("Andrew");
        student.setEmail("email");
        student.setStudentNumber(1L);
        student.setGrades(Map.of("CSE1100", 8.0, "CSE1300", 7.5, "CSE1400", 7.0,
                "CSE1505", 7.0, "CSE1500", 7.0));
        student.setCurrentCourses(Set.of("CSE2525", "CSE2310", "CSE2115"));
        student.setTaCourses(Set.of("CSE1100"));


        Lecturer lecturer = new Lecturer();

        lecturer.setNetId("lofi");
        lecturer.setEmail("lofi@email");
        lecturer.setPassword(encoder.encode("password"));
        lecturer.setName("Christoph");
        lecturer.setEmployeeNumber(1L);
        lecturer.setCourses(Set.of("CSE1505", "CSE2525", "CSE1500"));

        return (args) -> {
            userRepository.save(lecturer);
            userRepository.save(student);
        };
    }
}
