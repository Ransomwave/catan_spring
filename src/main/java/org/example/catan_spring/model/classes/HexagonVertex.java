package org.example.relationships.classes;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "hexagonvertex")
public class HexagonVertex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "hexagon_id", nullable = false)
    private Hexagon hexagon;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "vertex_id", nullable = false)
    private Vertex vertex;

    @NotNull
    @Min(0)
    @Max(5)
    @Column(nullable = false)
    private Integer posicio;

    public HexagonVertex() {
    }

    public HexagonVertex(Hexagon hexagon, Vertex vertex, Integer posicio) {
        this.hexagon = hexagon;
        this.vertex = vertex;
        this.posicio = posicio;
    }

    public Integer getId() {
        return id;
    }

    public Hexagon getHexagon() {
        return hexagon;
    }

    public void setHexagon(Hexagon hexagon) {
        this.hexagon = hexagon;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }

    public Integer getPosicio() {
        return posicio;
    }

    public void setPosicio(Integer posicio) {
        this.posicio = posicio;
    }
}