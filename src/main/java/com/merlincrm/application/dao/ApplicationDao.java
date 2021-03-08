package com.merlincrm.application.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.merlincrm.application.domain.Application;
import com.merlincrm.application.domain.ClasseTeacher;
import com.merlincrm.application.domain.Student;

@Repository
public interface ApplicationDao extends CrudRepository<Application, Long>{
	
	@Query("SELECT o FROM Application o WHERE o.applicationUser = :user ORDER BY o.createTime ASC")
	List<Application> findApplicationsByUser(@Param("user") Student user);
	
	@Query("SELECT o FROM Application o WHERE o.uuid = :uuid ORDER BY o.createTime ASC")
	Optional<Application> findApplicationForUUid(@Param("uuid") String uuid);
	
	@Query("SELECT o FROM Application o WHERE o.applicationUser.classe in ( select f.classe from ClasseAndTeacher f where f.classeTeacher = :user) AND o.status = :status ORDER BY o.createTime ASC")
	List<Application> findApplicationsByTeacher(@Param("user") ClasseTeacher teacher, @Param("status") Integer status);
	
	@Query("SELECT o FROM Application o WHERE o.applicationUser.classe in ( select f.classe from ClasseAndTeacher f where f.classeTeacher = :user) AND o.status > 20 ORDER BY o.createTime ASC")
	List<Application> findApplicationsByTeacher(@Param("user") ClasseTeacher teacher);
	
	@Query("SELECT o FROM Application o WHERE o.applicationUser.classe in ( select f.classe from ClasseAndTeacher f where f.classeTeacher = :user) AND o.expirationTime > :now AND o.status >= 20 ORDER BY o.createTime ASC")
	List<Application> findApplicationsByTeacher2(@Param("user") ClasseTeacher teacher, @Param("now") Date date);
	
	@Query("SELECT COUNT(o) FROM Application o WHERE o.applicationUser.classe in ( select f.classe from ClasseAndTeacher f where f.classeTeacher = :user) AND o.status = :status")
	Long countApplication(@Param("user") ClasseTeacher teacher, @Param("status") Integer status);
	
	public static long countApplications(EntityManager entityManager, Date selectDateStart, Date selectDateEnd, ClasseTeacher user, String name) {
		StringBuffer hql = new StringBuffer("SELECT COUNT(o) FROM Application o WHERE o.applicationUser.classe in ( select f.classe from ClasseAndTeacher f where f.classeTeacher = :user) AND o.status >= 20 ");
		if (StringUtils.isNotBlank(name)) {
			hql.append(" AND o.applicationUser.name LIKE :name");
		}
		if (selectDateStart != null) {
			hql.append(" AND o.selectTime >= :selectDateStart");
		}
		if (selectDateEnd != null) {
			hql.append(" AND o.selectTime < :selectDateEnd");
		}

		TypedQuery<Long> query = entityManager.createQuery(hql.toString(), Long.class).setParameter("user", user);

		if (StringUtils.isNotBlank(name)) {
			query.setParameter("name", "%" + name + "%");
		}
		if (selectDateStart != null) {
			query.setParameter("selectDateStart", selectDateStart);
		}
		
		if (selectDateEnd != null) {
			query.setParameter("selectDateEnd", selectDateEnd);
		}

		return query.getSingleResult();
	}

	public static Page<Application> findApplications(EntityManager entityManager, ClasseTeacher user, String name, Date selectDateStart, Date selectDateEnd, Pageable pageable) {
		final long count = ApplicationDao.countApplications(entityManager, selectDateStart, selectDateEnd, user, name);
		final int firstResult = (int) pageable.getOffset();

		StringBuffer hql = new StringBuffer("SELECT o FROM Application o WHERE o.applicationUser.classe in ( select f.classe from ClasseAndTeacher f where f.classeTeacher = :user) AND o.status >= 20 ");
		if (StringUtils.isNotBlank(name)) {
			hql.append(" AND o.applicationUser.name LIKE :name");
		}
		if (selectDateStart != null) {
			hql.append(" AND o.selectTime >= :selectDateStart");
		}
		if (selectDateEnd != null) {
			hql.append(" AND o.selectTime < :selectDateEnd");
		}
		
		hql.append(" ORDER BY o.status asc, o.selectTime desc, o.applicationUser.classe.classeName asc");
		TypedQuery<Application> query = entityManager.createQuery(hql.toString(), Application.class).setParameter("user", user)
				.setFirstResult(firstResult).setMaxResults(pageable.getPageSize());
		if (StringUtils.isNotBlank(name)) {
			query.setParameter("name", "%" + name + "%");
		}
		if (selectDateStart != null) {
			query.setParameter("selectDateStart", selectDateStart);
		}
		
		if (selectDateEnd != null) {
			query.setParameter("selectDateEnd", selectDateEnd);
		}

		return new PageImpl<Application>(query.getResultList(), pageable, count);
	}
	
	public static List<Application> findApplications(EntityManager entityManager, ClasseTeacher user, String name, Date selectDateStart, Date selectDateEnd) {

		StringBuffer hql = new StringBuffer("SELECT o FROM Application o WHERE o.applicationUser.classe in ( select f.classe from ClasseAndTeacher f where f.classeTeacher = :user) AND o.status >= 20 ");
		if (StringUtils.isNotBlank(name)) {
			hql.append(" AND o.applicationUser.name LIKE :name");
		}
		if (selectDateStart != null) {
			hql.append(" AND o.selectTime >= :selectDateStart");
		}
		if (selectDateEnd != null) {
			hql.append(" AND o.selectTime < :selectDateEnd");
		}
		
		hql.append(" ORDER BY o.status asc, o.selectTime desc, o.applicationUser.classe.classeName asc");
		TypedQuery<Application> query = entityManager.createQuery(hql.toString(), Application.class).setParameter("user", user);
		
		if (StringUtils.isNotBlank(name)) {
			query.setParameter("name", "%" + name + "%");
		}
		
		if (selectDateStart != null) {
			query.setParameter("selectDateStart", selectDateStart);
		}
		
		if (selectDateEnd != null) {
			query.setParameter("selectDateEnd", selectDateEnd);
		}

		return query.getResultList();
	}
}
