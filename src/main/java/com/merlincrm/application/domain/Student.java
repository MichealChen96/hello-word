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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cm_student")
public class Student implements Serializable {

	private static final long serialVersionUID = 8384423559507212657L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;
	
	@JsonIgnore
	@NotNull
	@ManyToOne
	@JoinColumn(name = "classe_id")
	private Classe classe;
	
	private Long uid;
	
	@NotNull
	@Size(min = 1, max = 80)
	private String name;
	
	@NotNull
	@Size(min = 1, max = 80)
	private String studentId;
	
	private String career;
	
	private String imageUrl;
	
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
	
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", this.id);
		map.put("uid", this.uid);
		map.put("name", this.name);
		map.put("studentId", this.studentId);
		map.put("career", this.career);
		map.put("program", this.classe.getProgram());
		map.put("classe", this.classe.getClasseName());
		map.put("imageUrl", this.imageUrl);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public Classe getClasse() {
		return classe;
	}

	public void setClasse(Classe classe) {
		this.classe = classe;
	}

	public String getCareer() {
		return career;
	}

	public void setCareer(String career) {
		this.career = career;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
}
