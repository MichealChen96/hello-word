package com.merlincrm.application.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.merlincrm.application.domain.Item;

@Repository
public interface ItemDao extends CrudRepository<Item, Long> {
	@Query("SELECT o FROM Item o ORDER BY o.createTime ASC")
	List<Item> findAllItem();
	
}
