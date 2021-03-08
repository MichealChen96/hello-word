package com.merlincrm.application.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.merlincrm.application.domain.ClasseAndTeacher;

@Repository
public interface ClasseAndTeacherDao extends CrudRepository<ClasseAndTeacher, Long> {

}
