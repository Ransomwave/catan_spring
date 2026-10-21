package org.example.relationships.classes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;

@Entity
@DiscriminatorValue("carretera")
@NamedQuery(name = "Carretera.countByPropietari", query = "select count(c) from Carretera c where c.propietari = :jugador")
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
