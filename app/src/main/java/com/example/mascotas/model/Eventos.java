package com.example.mascotas.model;

public class Eventos {
    public String creador,descripcion,estado,fecha,horario,latitud,longitud,titulo,photo;
    public int inscriptos,costo,cupo;

    public Eventos(){}
    public Eventos(int costo, String creador, int cupo, String descripcion,
                   String estado, String fecha, String horario, int inscriptos,
                   String latitud, String longitud, String titulo, String photo) {
        this.costo = costo;
        this.creador = creador;
        this.cupo = cupo;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fecha = fecha;
        this.horario = horario;
        this.latitud = latitud;
        this.longitud = longitud;
        this.titulo = titulo;
        this.inscriptos = inscriptos;
        this.photo = photo;
    }
    public void setCosto(int costo) {
        this.costo = costo;
    }
    public void setCreador(String creador) {
        this.creador = creador;
    }
    public void setCupo(int cupo) {
        this.cupo = cupo;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public void setHorario(String horario) {
        this.horario = horario;
    }
    public void setInscriptos(int inscriptos) {
        this.inscriptos = inscriptos;
    }
    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    //*********************************************///
    public int getCosto() {
        return costo;
    }
    public String getCreador() {
        return creador;
    }
    public int getCupo() {
        return cupo;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public String getEstado() {
        return estado;
    }
    public String getFecha() {
        return fecha;
    }
    public String getHorario() {
        return horario;
    }
    public int getInscriptos() {
        return inscriptos;
    }
    public String getLatitud() {
        return latitud;
    }
    public String getLongitud() {
        return longitud;
    }
    public String getTitulo() {
        return titulo;
    }

   //********************************************************/////
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
