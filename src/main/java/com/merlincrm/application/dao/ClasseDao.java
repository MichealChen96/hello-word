package com.merlincrm.application.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.merlincrm.application.domain.Application;
import com.merlincrm.application.domain.Classe;

@Repository
public interface ClasseDao extends CrudRepository<Classe, Long> {
	
	@Query("SELECT o FROM Classe o WHERE o.program = :program and o.classeName = :classeName ORDER BY o.createTime ASC")
	Optional<Classe> findClasse(@Param("program") String program, @Param("classeName") String classeName);

}
