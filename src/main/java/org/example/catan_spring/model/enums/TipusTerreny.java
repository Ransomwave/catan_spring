package org.example.relationships.enums;

public enum TipusTerreny {
    BOSC(TipusRecurs.FUSTA),
    TURONS(TipusRecurs.MAO),
    PASTURES(TipusRecurs.LLANA),
    CAMPS(TipusRecurs.BLAT),
    MUNTANYES(TipusRecurs.MINERAL),
    DESERT(null);

    private final TipusRecurs resource;

    TipusTerreny(TipusRecurs resource) {
        this.resource = resource;
    }

    /**
     * Gets the TipusRecurs produced by this terrain type, or null if it produces
     * none (desert).
     */
    public TipusRecurs getResource() {
        return resource;
    }
}
