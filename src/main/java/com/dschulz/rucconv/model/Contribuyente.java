package com.dschulz.rucconv.model;

public final class Contribuyente {
    private final String ruc;
    private final String denominacion;
    private final String denominacionCorregida;
    private final int verificador;
    private final String rucAnterior;
    private final String estado;
    private final boolean activo;
    private final String notas;

    public Contribuyente(String ruc, String denominacion, String denominacionCorregida, int verificador,
                         String rucAnterior, String estado, boolean activo, String notas) {
        this.ruc = ruc;
        this.denominacion = denominacion;
        this.denominacionCorregida = denominacionCorregida;
        this.verificador = verificador;
        this.rucAnterior = rucAnterior;
        this.estado = estado;
        this.activo = activo;
        this.notas = notas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contribuyente that = (Contribuyente) o;

        return ruc.equals(that.ruc);
    }

    @Override
    public int hashCode() {
        return ruc.hashCode();
    }

    @Override
    public String toString() {
        return "Contribuyente{" +
            "denominacion='" + denominacion + '\'' +
            ", denominacionCorregida='" + denominacionCorregida + '\'' +
            ", ruc='" + ruc + '\'' +
            ", rucAnterior='" + rucAnterior + '\'' +
            ", verificador=" + verificador +
            ", estado=" + verificador +
            ", activo=" + activo +
            ", notas=" + notas +
            '}';
    }

    public String getRuc() {
        return ruc;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public String getDenominacionCorregida() {
        return denominacionCorregida;
    }

    public int getVerificador() {
        return verificador;
    }

    public String getRucAnterior() {
        return rucAnterior;
    }

    public String getEstado() {
        return estado;
    }

    public boolean isActivo() {
        return activo;
    }

    public String getNotas() {
        return notas;
    }

}
