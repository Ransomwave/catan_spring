package org.example.catan_spring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "vertex", uniqueConstraints = @UniqueConstraint(columnNames = { "partida_id", "codi" }))
public class Vertex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Min(1)
    @Max(54)
    @Column(nullable = false)
    private Integer codi;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida partida;

    public Vertex() {
    }

    public Vertex(Integer codi, Partida partida) {
        this.codi = codi;
        this.partida = partida;
    }

    public Integer getId() {
        return id;
    }

    public Integer getCodi() {
        return codi;
    }

    public void setCodi(Integer codi) {
        this.codi = codi;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }
}
