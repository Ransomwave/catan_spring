package org.example.relationships.classes;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("poblat")
@NamedQuery(name = "Poblat.countByPropietari", query = "select count(p) from Poblat p where p.propietari = :jugador")
public class Poblat extends Assentament {

}
