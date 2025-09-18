package com.jenksy.jenksymcp.service;

import com.jenksy.jenksymcp.record.Course;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CourseService {

   private final List<Course> courses = new ArrayList<>();


    @Tool(
            description = "Get all available courses",
            name = "get_courses"
    )
    public List<Course> getCourses() {
        log.info("Getting all courses");
        return courses;
    }

    @Tool(
            description = "Add a new course",
            name = "add_course"
    )
    public void addCourse(Course course) {
        log.info("Adding course: {}", course);
        courses.add(course);
    }

    @Tool(
            description = "Get a course by its title",
            name = "get_course_by_title"
    )
    public Course getCourseByTitle(String title) {
        log.info("Getting course by title: {}", title);
        return courses.stream()
                .filter(course -> course.title().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }


   @PostConstruct
    public void init() {
        courses.addAll(List.of(
                new Course("Java", "https://www.jenksy.com/java"),
                new Course("Spring Boot", "https://www.jenksy.com/spring-boot"),
                new Course("JavaScript", "https://www.jenksy.com/javascript"),
                new Course("TypeScript", "https://www.jenksy.com/typescript"),
                new Course("Angular", "https://www.jenksy.com/angular"),
                new Course("React", "https://www.jenksy.com/react"),
                new Course("Node.js", "https://www.jenksy.com/nodejs"),
                new Course("Python", "https://www.jenksy.com/python"),
                new Course("Django", "https://www.jenksy.com/django"),
                new Course("Flask", "https://www.jenksy.com/flask"),
                new Course("C#", "https://www.jenksy.com/csharp"),
                new Course(".NET Core", "https://www.jenksy.com/dotnet-core"),
                new Course("Ruby on Rails", "https://www.jenksy.com/ruby-on-rails"),
                new Course("PHP", "https://www.jenksy.com/php"),
                new Course("Laravel", "https://www.jenksy.com/laravel"),
                new Course("Go", "https://www.jenksy.com/go"),
                new Course("Kotlin", "https://www.jenksy.com/kotlin"),
                new Course("Swift", "https://www.jenksy.com/swift"),
                new Course("iOS Development", "https://www.jenksy.com/ios-development"),
                new Course("Android Development", "https://www.jenksy.com/android-development")
        ));
    }
}
