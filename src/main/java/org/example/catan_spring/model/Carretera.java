package org.example.catan_spring.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@DiscriminatorValue("carretera")
public class Carretera extends Construccio {

	@OneToOne(optional = false)
	@JoinColumn(name = "aresta_id", nullable = false, unique = true)
	private Aresta aresta;

	public Aresta getAresta() {
		return aresta;
	}

	public void setAresta(Aresta aresta) {
		this.aresta = aresta;
	}

}
