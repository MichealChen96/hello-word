package com.merlincrm.application.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.merlincrm.application.domain.EnterSchoolLog;

@Repository
public interface EnterSchoolLogDao extends CrudRepository<EnterSchoolLog, Long> {
	
	@Query("SELECT o FROM EnterSchoolLog o WHERE o.createTime > :statTime  and  o.createTime < :endTime ORDER BY o.createTime DESC")
	List<EnterSchoolLog> findEnterSchoolLogList1(@Param("statTime") Date statTime, @Param("endTime") Date endTime);
	
	@Query("SELECT o FROM EnterSchoolLog o WHERE o.createTime > :statTime ORDER BY o.createTime DESC")
	List<EnterSchoolLog> findEnterSchoolLogList2(@Param("statTime") Date statTime);
	
	@Query("SELECT o FROM EnterSchoolLog o WHERE o.createTime < :endTime ORDER BY o.createTime DESC")
	List<EnterSchoolLog> findEnterSchoolLogList3(@Param("endTime") Date endTime);
	
	@Query("SELECT o FROM EnterSchoolLog o ORDER BY o.createTime ASC")
	List<EnterSchoolLog> findEnterSchoolLogAll();

}
