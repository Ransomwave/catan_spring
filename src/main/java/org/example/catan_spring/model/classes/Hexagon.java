package org.example.relationships.classes;

import java.util.ArrayList;
import java.util.List;

import org.example.relationships.enums.TipusTerreny;
import org.hibernate.annotations.Check;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "hexagon", uniqueConstraints = @UniqueConstraint(columnNames = { "partida_id", "codi" }))
public class Hexagon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    @Min(1)
    @Max(19)
    private Integer codi;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipusTerreny terreny;

    @Min(2)
    @Max(12)
    @Check(constraints = "numero <> 7")
    private Integer numero;

    @Column(nullable = false)
    @Min(-2)
    @Max(2)
    private int q;

    @Column(nullable = false)
    @Min(-2)
    @Max(2)
    private int r;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Partida partida;

    @OneToMany(mappedBy = "hexagon", cascade = CascadeType.ALL)
    private List<HexagonVertex> vertexs = new ArrayList<>();

    public Hexagon() {
    }

    public Hexagon(Integer codi, TipusTerreny terreny, Integer numero, int q, int r, Partida partida) {
        this.codi = codi;
        this.terreny = terreny;
        this.numero = numero;
        this.q = q;
        this.r = r;
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

    public TipusTerreny getTerreny() {
        return terreny;
    }

    public void setTerreny(TipusTerreny terreny) {
        this.terreny = terreny;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public int getQ() {
        return q;
    }

    public void setQ(int q) {
        this.q = q;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public List<HexagonVertex> getVertexs() {
        return vertexs;
    }

    public void setVertexs(List<HexagonVertex> vertexs) {
        this.vertexs = vertexs;
    }

}