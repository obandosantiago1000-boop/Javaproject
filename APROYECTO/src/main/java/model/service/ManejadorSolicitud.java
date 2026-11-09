/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.service;

import model.entity.Solicitudes;

/**
 *
 * @author ALIENWARE AURORA
 */

public abstract class ManejadorSolicitud {

    protected ManejadorSolicitud siguiente; 

    public void setSiguiente(ManejadorSolicitud siguiente) {
        this.siguiente = siguiente;
    }


    public abstract boolean manejar(Solicitudes solicitud);
}
