package model.entity;

import java.io.Serializable;
import java.time.LocalDate;

public class pago implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private LocalDate fechaPago;
    private double monto;
    private long prestamoId;
    private String metodo;

    public pago(long id, LocalDate fechaPago, double monto,
                long prestamoId, String metodo) {
        this.id = id;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.prestamoId = prestamoId;
        this.metodo = metodo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public double getMonto() {
        return monto;
    }

    public long getPrestamoId() {
        return prestamoId;
    }

    public String getMetodo() {
        return metodo;
    }

    @Override
    public String toString() {
        return "Pago #" + id +
               " | Fecha: " + fechaPago +
               " | Monto: " + String.format("%.2f", monto) +
               " | Método: " + metodo;
    }
}
