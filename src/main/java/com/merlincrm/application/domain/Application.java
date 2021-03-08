package com.merlincrm.application.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cm_application")
public class Application implements Serializable  {

	private static final long serialVersionUID = 6948532930472733515L;
	
	public static final Integer STATUS_DRAFT = 10;
	public static final Integer STATUS_SUBMIT = 20;
	public static final Integer STATUS_REJECT = 30;
	public static final Integer STATUS_PASS = 31;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;
	
	@JsonIgnore
	@NotNull
	@ManyToOne
	@JoinColumn(name = "student_id")
	private Student applicationUser;
	
	@JsonIgnore
	@NotNull
	private Integer status = 10;
	
	@JsonIgnore
	private String uuid;
	
	@JsonIgnore
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "M-")
	private Date selectTime = new Date();
	
	@JsonIgnore
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "M-")
	private Date expirationTime = new Date();
	
	@JsonIgnore
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "M-")
	private Date createTime = new Date();

	@JsonIgnore
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "M-")
	private Date updateTime = new Date();
	
	@JsonIgnore
	private String result;
	
	@Transient
	private String codeImageUrl;
	
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", this.id);
		map.put("applicationUser", this.applicationUser.toMap());
		map.put("selectTime", this.selectTime);
		map.put("expirationTime", this.expirationTime);
		map.put("codeImageUrl", this.codeImageUrl);
		map.put("status", this.status);
		map.put("uuid", this.uuid);
		map.put("result", this.result);
		map.put("createTime", this.createTime);
		map.put("updateTime", this.updateTime);
		return map;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Student getApplicationUser() {
		return applicationUser;
	}

	public void setApplicationUser(Student applicationUser) {
		this.applicationUser = applicationUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getSelectTime() {
		return selectTime;
	}

	public void setSelectTime(Date selectTime) {
		this.selectTime = selectTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getCodeImageUrl() {
		return codeImageUrl;
	}

	public void setCodeImageUrl(String codeImageUrl) {
		this.codeImageUrl = codeImageUrl;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
