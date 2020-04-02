package com.demo.student;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("management/api/v1/students")
public class StudentManagementController {

    private List<Student> students = new ArrayList<>() {
        {
            add(new Student(1, "James Bond"));
            add(new Student(2, "Maria Jones"));
            add(new Student(3, "Anna Smith"));
        }
    };

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_ADMINTRAINEE')")
    public List<Student> getAllStudents() {
        System.out.println("getAllStudents");
        return students;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('student:write')")
    public void registerNewStudent(@RequestBody Student student) {
        System.out.println("registerNewStudent");
        System.out.println(student);
        students.add(student);
    }

    @DeleteMapping(path = {"{studentId}"})
    @PreAuthorize("hasAuthority('student:write')")
    public void deleteStudent(@PathVariable("studentId") Integer studentId) {
        System.out.println("deleteStudent");
        System.out.println(studentId);

        students.stream()
                .filter(student -> student.getStudentId().equals(studentId))
                .findFirst()
                .ifPresentOrElse(students::remove, () -> System.out.println("Could not find student with id " + studentId));
    }

    @PutMapping(path = "{studentId}")
    @PreAuthorize("hasAuthority('student:write')")
    public void updateStudent(@PathVariable("studentId") Integer studentId, @RequestBody Student studentToUpdate) {
        System.out.println("updateStudent");
        System.out.println(String.format("%s %s", studentId, studentToUpdate));

        students.stream()
                .filter(student -> student.getStudentId().equals(studentId))
                .findFirst()
                .ifPresentOrElse(student -> {
                            deleteStudent(studentId);
                            registerNewStudent(studentToUpdate);
                        },
                        () -> System.out.println("Could not find student with id " + studentId));
    }
}
