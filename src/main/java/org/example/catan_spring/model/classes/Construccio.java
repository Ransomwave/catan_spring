package org.example.relationships.classes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "construccions")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipus", discriminatorType = DiscriminatorType.STRING, length = 10)
public abstract class Construccio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "propietari_id", nullable = false)
	private Jugador propietari;

	public Integer getId() {
		return id;
	}

	public Jugador getPropietari() {
		return propietari;
	}

	public void setPropietari(Jugador propietari) {
		this.propietari = propietari;
	}
}
