package org.example.catan_spring.model;

import org.example.catan_spring.enums.TipusCarta;

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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "carta_desenvolupament")
public class CartaDesenvolupament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = false)
    TipusCarta tipus;

    @NotNull
    @Column(nullable = false, unique = false)
    Boolean usada;

    @Min(1)
    @Column(nullable = true, unique = false)
    Integer rondaCompra;

    @ManyToOne
    @JoinColumn(nullable = false, unique = false)
    Partida partida;

    @ManyToOne
    @JoinColumn(nullable = true)
    Jugador propietari;

    public CartaDesenvolupament(TipusCarta tipus, boolean usada, Integer rondaCompra) {
        this.tipus = tipus;
        this.usada = usada;
        this.rondaCompra = rondaCompra;
    }

    public CartaDesenvolupament(TipusCarta tipus, boolean usada, Integer rondaCompra, Partida partida) {
        this.tipus = tipus;
        this.usada = usada;
        this.rondaCompra = rondaCompra;
        this.partida = partida;
    }

    public CartaDesenvolupament() {
    }

    public Integer getId() {
        return id;
    }

    public TipusCarta getTipus() {
        return tipus;
    }

    public void setTipus(TipusCarta tipus) {
        this.tipus = tipus;
    }

    public Boolean getUsada() {
        return usada;
    }

    public void setUsada(Boolean usada) {
        this.usada = usada;
    }

    public Integer getRondaCompra() {
        return rondaCompra;
    }

    public void setRondaCompra(Integer rondaCompra) {
        this.rondaCompra = rondaCompra;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public Jugador getPropietari() {
        return propietari;
    }

    public void setPropietari(Jugador propietari) {
        this.propietari = propietari;
    }
}
