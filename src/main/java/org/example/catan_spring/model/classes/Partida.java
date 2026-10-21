package org.example.relationships.classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.example.relationships.enums.EstatPartida;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.ManyToAny;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@Entity
@Table(name = "partida")
@DynamicInsert
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    // Integer i no int: mentre l'objecte no s'ha guardat, id és null (i no 0)
    private Integer id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estat", nullable = false, length = 15, unique = false)
    private EstatPartida estat;

    @CreationTimestamp
    @Column(name = "data_inici", nullable = false, unique = false, updatable = false)
    private Date dataInici;

    @Min(1)
    @Column(name = "ronda", nullable = false, unique = false)
    private Integer ronda;

    @Min(2)
    @Max(12)
    @Column(name = "ultima_tirada", nullable = true, unique = false)
    private Integer ultimaTirada;

    @Min(3)
    @Max(20)
    @Column(name = "punts_per_guanyar", nullable = false, unique = false)
    private Integer puntsPerGuanyar;

    @Column(name = "daus_tirats", nullable = false, unique = false)
    private boolean dausTirats;

    @Column(name = "lladre_pendent", nullable = false, unique = false)
    private boolean lladrePendent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lladre_hexagon_id")
    private Hexagon lladre;

    @ManyToOne()
    @JoinColumn(name = "actiu_jugador_id", nullable = true)
    private Jugador actiu;

    @ManyToOne()
    @JoinColumn(name = "guanyador_jugador_id", nullable = true)
    private Jugador guanyador;

    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
    private List<Hexagon> tauler = new ArrayList<>();

    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
    private List<Jugador> jugadors = new ArrayList<>();

    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
    private List<Aresta> arestes = new ArrayList<>();

    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
    private List<CartaDesenvolupament> pila = new ArrayList<>();

    @OneToMany(mappedBy = "partida", cascade = CascadeType.ALL)
    private List<Vertex> vertices = new ArrayList<>();

    public Partida(EstatPartida estat, Integer ronda, Integer puntsPerGuanyar) {
        this.estat = estat;
        this.ronda = ronda;
        this.puntsPerGuanyar = puntsPerGuanyar;
    }

    public Partida() {
    }

    public Integer getId() {
        return id;
    }

    public void setLladre(Hexagon lladre) {
        this.lladre = lladre;
    }

    public EstatPartida getEstat() {
        return estat;
    }

    public void setEstat(EstatPartida estat) {
        this.estat = estat;
    }

    public Date getDataInici() {
        return dataInici;
    }

    public void setDataInici(Date dataInici) {
        this.dataInici = dataInici;
    }

    public Integer getRonda() {
        return ronda;
    }

    public void setRonda(Integer ronda) {
        this.ronda = ronda;
    }

    public Integer getUltimaTirada() {
        return ultimaTirada;
    }

    public void setUltimaTirada(Integer ultimaTirada) {
        this.ultimaTirada = ultimaTirada;
    }

    public Integer getPuntsPerGuanyar() {
        return puntsPerGuanyar;
    }

    public void setPuntsPerGuanyar(Integer puntsPerGuanyar) {
        this.puntsPerGuanyar = puntsPerGuanyar;
    }

    public boolean isDausTirats() {
        return dausTirats;
    }

    public void setDausTirats(boolean dausTirats) {
        this.dausTirats = dausTirats;
    }

    public boolean isLladrePendent() {
        return lladrePendent;
    }

    public void setLladrePendent(boolean lladrePendent) {
        this.lladrePendent = lladrePendent;
    }

    public Hexagon getLladre() {
        return lladre;
    }

    public Jugador getActiu() {
        return actiu;
    }

    public void setActiu(Jugador actiu) {
        this.actiu = actiu;
    }

    public Jugador getGuanyador() {
        return guanyador;
    }

    public void setGuanyador(Jugador guanyador) {
        this.guanyador = guanyador;
    }

    public List<Hexagon> getTauler() {
        return tauler;
    }

    public void setTauler(List<Hexagon> tauler) {
        this.tauler = tauler;
    }

    public List<Jugador> getJugadors() {
        return jugadors;
    }

    public void setJugadors(List<Jugador> jugadors) {
        this.jugadors = jugadors;
    }

    public List<Aresta> getArestes() {
        return arestes;
    }

    public void setArestes(List<Aresta> arestes) {
        this.arestes = arestes;
    }

    public List<CartaDesenvolupament> getPila() {
        return pila;
    }

    public void setPila(List<CartaDesenvolupament> pila) {
        this.pila = pila;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }
}
