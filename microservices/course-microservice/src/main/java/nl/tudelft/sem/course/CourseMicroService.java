package nl.tudelft.sem.course;

import java.util.Date;
import java.util.Set;
import nl.tudelft.sem.course.entity.Course;
import nl.tudelft.sem.course.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class CourseMicroService {

    public static void main(String[] args) {
        SpringApplication.run(CourseMicroService.class, args);
    }

    /**
     * Demo method to save some courses in the courseRepository.
     *
     * @param courseRepository - The repo to save the courses in
     * @return - The actions in args
     */
    @Bean
    public CommandLineRunner demo(CourseRepository courseRepository) {
        Course idm = new Course();

        idm.setCourseCode("CSE1505");
        idm.setDescription("Information and DataManagement");
        idm.setLecturers(Set.of(1L));
        idm.setNumOfStudents(400);
        idm.setStartDate(new Date(2022 - 1900, 2, 24));
        idm.setEndDate(new Date(2022 - 1900, 4, 24));

        Course wdt = new Course();

        wdt.setCourseCode("CSE1500");
        wdt.setDescription("Web and Database Technology");
        wdt.setLecturers(Set.of(1L));
        wdt.setNumOfStudents(500);
        wdt.setStartDate(new Date(2021 - 1900, 11, 0));
        wdt.setEndDate(new Date(2022 - 1900, 2, 0));

        return (args -> {
            courseRepository.save(idm);
            courseRepository.save(wdt);
        });
    }
}