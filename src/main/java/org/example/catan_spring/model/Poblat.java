package org.example.catan_spring.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("poblat")
public class Poblat extends Assentament {

}
