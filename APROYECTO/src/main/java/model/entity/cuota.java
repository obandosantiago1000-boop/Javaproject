package model.entity;

import java.io.Serializable;
import java.time.LocalDate;

public class cuota implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;
    private LocalDate fechaPago;
    private double valor;
    private long prestamoId;
    private double valorPagado;
    private String estado;


    public cuota(long id, LocalDate fechaPago, double valor, long prestamoId) {
        this(id, fechaPago, valor, prestamoId, 0, "PENDIENTE");
    }

 
    public cuota(long id, LocalDate fechaPago, double valor,
                 long prestamoId, double valorPagado, String estado) {
        this.id = id;
        this.fechaPago = fechaPago;
        this.valor = valor;
        this.prestamoId = prestamoId;
        this.valorPagado = valorPagado;
        this.estado = estado;
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

    public double getValor() {
        return valor;
    }

    public long getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(long prestamoId) {
        this.prestamoId = prestamoId;
    }

    public double getValorPagado() {
        return valorPagado;
    }

    public String getEstado() {
        return estado == null ? "PENDIENTE" : estado;
    }

    public boolean isPagada() {
        return "PAGADA".equals(estado);
    }


    public double getSaldo() {
        return Math.round((valor - valorPagado) * 100.0) / 100.0;
    }

    public String toString(int numero) {
        return "Cuota " + numero +
               " | Fecha: " + fechaPago +
               " | Valor: " + String.format("%.2f", valor) +
               " | Pagado: " + String.format("%.2f", valorPagado) +
               " | Estado: " + getEstado();
    }
}
