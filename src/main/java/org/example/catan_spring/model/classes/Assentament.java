package org.example.relationships.classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("assentament")
public abstract class Assentament extends Construccio {

	@OneToOne(optional = false)
	@JoinColumn(name = "vertex_id", nullable = false, unique = true)
	private Vertex vertex;

	public Assentament() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Assentament(Vertex vertex) {
		this.vertex = vertex;
	}

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}
}
