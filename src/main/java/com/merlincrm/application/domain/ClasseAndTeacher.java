package com.merlincrm.application.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "cm_classe_and_teacher")
public class ClasseAndTeacher implements Serializable {

	private static final long serialVersionUID = 3458698790752002577L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "teacher_id")
	private ClasseTeacher classeTeacher;
	
	@JsonIgnore
	@NotNull
	@ManyToOne
	@JoinColumn(name = "classe_id")
	private Classe classe;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ClasseTeacher getClasseTeacher() {
		return classeTeacher;
	}

	public void setClasseTeacher(ClasseTeacher classeTeacher) {
		this.classeTeacher = classeTeacher;
	}

	public Classe getClasse() {
		return classe;
	}

	public void setClasse(Classe classe) {
		this.classe = classe;
	}

}
