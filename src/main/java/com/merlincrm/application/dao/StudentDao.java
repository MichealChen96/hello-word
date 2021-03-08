package com.merlincrm.application.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.merlincrm.application.domain.Student;

@Repository
public interface StudentDao extends CrudRepository<Student, Long> {
	
	@Query("SELECT o FROM Student o WHERE o.name = :name")
	Optional<Student> findUser(@Param("name") String name);
	
	@Query("SELECT o FROM Student o WHERE o.uid = :uid")
	Optional<Student> findUserByUid(@Param("uid") Long uid);
	
}
