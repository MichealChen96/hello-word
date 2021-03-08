package com.merlincrm.application.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.merlincrm.application.domain.Document;
import com.merlincrm.application.domain.Student;

@Repository
public interface DocumentDao extends CrudRepository<Document, Long> {
	
	@Query("SELECT o FROM Document o WHERE o.student = :student and o.type = :type")
	List<Document> findDocumentByStudent(@Param("student") Student student, @Param("type") Integer type);

}
