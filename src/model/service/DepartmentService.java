package model.service;

import model.entities.Department;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DepartmentService {
    Department[] departments = {new Department(1, "One"), new Department(2, "Two"),
            new Department(3, "Three"), new Department(4, "Four")};
    List<Department> departmentList = new ArrayList<>(Arrays.stream(departments).collect(Collectors.toList()));
    public List<Department> getAll() {
        return departmentList;
    }
}
