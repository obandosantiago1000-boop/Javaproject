/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;
import java.io.Serializable;

public class Solicitudes implements Serializable {
    private long id;
    private double monto;
    private int cuotas;
    private clientes cliente;

    public Solicitudes(long id, double monto, int cuotas, clientes cliente) {
        this.id = id;
        this.monto = monto;
        this.cuotas = cuotas;
        this.cliente = cliente;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public int getCuotas() {
        return cuotas;
    }

    public void setCuotas(int cuotas) {
        this.cuotas = cuotas;
    }

    public clientes getCliente() {
        return cliente;
    }

    public void setCliente(clientes cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return
                "\nID: " + id +
                "\n Monto: " +monto +
                "\n Cuotas: " + cuotas +
                "\nCliente: " + cliente;
    }
    
    
    
    
    
}
