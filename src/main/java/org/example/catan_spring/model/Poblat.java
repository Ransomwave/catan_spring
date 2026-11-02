package org.example.catan_spring.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;

@Entity
@DiscriminatorValue("poblat")
@NamedQuery(name = "Poblat.countByPropietari", query = "select count(p) from Poblat p where p.propietari = :jugador")
public class Poblat extends Assentament {

}
