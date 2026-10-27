package org.example.catan_spring.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ciutat")
public class Ciutat extends Assentament {

}
