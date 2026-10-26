package org.example.catan_spring.model;


import org.example.catan_spring.enums.TipusRecurs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "recurs_jugador", uniqueConstraints = @UniqueConstraint(columnNames = { "jugador_id", "tipus" }))
public class RecursJugador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Min(0)
    @Column(nullable = false, unique = false)
    Integer quantitat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = false)
    TipusRecurs tipus;

    @ManyToOne
    @JoinColumn(name = "jugador_id", nullable = false, unique = false)
    Jugador jugador;

    public RecursJugador(@Min(0) Integer quantitat, TipusRecurs tipus, Jugador jugador) {
        this.quantitat = quantitat;
        this.tipus = tipus;
        this.jugador = jugador;
    }

    public RecursJugador() {
    }

	public Integer getId() {
		return id;
	}

	public Integer getQuantitat() {
		return quantitat;
	}

	public void setQuantitat(Integer quantitat) {
		this.quantitat = quantitat;
	}

	public TipusRecurs getTipus() {
		return tipus;
	}

	public void setTipus(TipusRecurs tipus) {
		this.tipus = tipus;
	}

	public Jugador getJugador() {
		return jugador;
	}

	public void setJugador(Jugador jugador) {
		this.jugador = jugador;
	}
}
