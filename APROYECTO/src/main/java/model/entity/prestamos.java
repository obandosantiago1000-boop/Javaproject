/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entity;
import java.time.LocalDate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author ALIENWARE AURORA
 */
public class prestamos implements Serializable{
    private static final long serialVersionUID = 1L;

    private long id;
    private double interes;
    private LocalDate fechaInicio;
    private String estado;
    private empleados empleado;
    private Solicitudes solicitud;
    private List<cuota> cuotas = new ArrayList<>();
    
    
    


    public prestamos(long id, double interes, empleados empleado, Solicitudes solicitud) {

        this.id = id;
        this.interes = interes;
        this.fechaInicio = LocalDate.now();
        this.estado = "ACTIVO";
        this.empleado = empleado;
        this.solicitud = solicitud;

    }


public prestamos(long id, double interes,
                 LocalDate fechaInicio, String estado,
                 empleados empleado, Solicitudes solicitud) {

    this.id = id;
    this.interes = interes;
    this.fechaInicio = fechaInicio;
    this.estado = estado;
    this.empleado = empleado;
    this.solicitud = solicitud;

}

   



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getInteres() {
        return interes;
    }



    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public String getEstado() {
        return estado;
    }

    public empleados getEmpleado() {
        return empleado;
    }

    public Solicitudes getSolicitud() {
        return solicitud;
    }



    public void setEstado(String estado) {
        if (!estado.equals("ACTIVO") && !estado.equals("PAGADO")) {
            throw new IllegalArgumentException("Estado de préstamo no válido.");
        }

        this.estado = estado;
    }
    

    public List<cuota> generarCuotas() {

        int n = solicitud.getCuotas();
        double total = solicitud.getMonto() * (1 + interes);

        double valorBase = Math.round(total / n * 100.0) / 100.0;
        double acumulado = 0;

        List<cuota> lista = new ArrayList<>();

        for (int i = 0; i < n; i++) {

            double valor;

            if (i == n - 1) {

                valor = Math.round((total - acumulado) * 100.0) / 100.0;
            } else {
                valor = valorBase;
            }

            acumulado += valor;


            lista.add(new cuota(0, fechaInicio.plusMonths(i), valor, id));
        }

        return lista;
    }

    public List<cuota> getCuotas() {
        return cuotas;
    }

    public void setCuotas(List<cuota> cuotas) {
        this.cuotas = cuotas;
    }


    public double getSaldoPendiente() {

        double total = 0;

        for (cuota q : cuotas) {
            total += q.getSaldo();
        }

        return Math.round(total * 100.0) / 100.0;
    }


    public double getTotalPagado() {

        double total = 0;

        for (cuota q : cuotas) {
            total += q.getValorPagado();
        }

        return Math.round(total * 100.0) / 100.0;
    }

     @Override
public String toString() {

    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < cuotas.size(); i++) {
        sb.append("\n   ").append(cuotas.get(i).toString(i + 1));
    }

    return
            "\nID: " + id +
            "\nInteres: " + interes +
            "\nFecha incio: " + fechaInicio +
            "\nEstado: " + estado +
            "\nSolicitud: " + solicitud +
            "\nEmpleado: " + empleado +
            "\nCuotas:" + sb;
}
    
    
}
    
    
            

