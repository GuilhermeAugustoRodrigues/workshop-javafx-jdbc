package model.service;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.util.List;

public class DepartmentService {

    DepartmentDao departmentDao = DaoFactory.createDepartmentDao();

    public List<Department> findAll() {
        return departmentDao.findAll();
    }

    public void save(Department department) {
        if (department.getId() == null) {
            departmentDao.insert(department);
        } else {
            departmentDao.update(department);
        }
    }

    public void delete(Department department) {
        departmentDao.deleteById(department.getId());
    }
}
