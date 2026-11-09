package controller;

import model.service.bitacora;

import dao.pagoDao;
import dao.prestamoDao;

import model.entity.cuota;
import model.entity.pago;
import model.entity.prestamos;

import model.gestion.gestionPagos;
import model.gestion.gestionPrestamos;

import view.ingresos.ingresoPagos;
import view.menus.menuAdmin;

import java.sql.SQLException;
import java.util.List;

public class controllerGestionPago {

    private final pagoDao dao = new pagoDao();

    private final prestamoDao daoPrestamo = new prestamoDao();

    private final gestionPrestamos gestion = new gestionPrestamos();

    private final gestionPagos gestionPago = new gestionPagos();

    private final ingresoPagos ingreso = new ingresoPagos();

    private final menuAdmin menu = new menuAdmin();


    private boolean prestamoActivo(prestamos p) {

        if (p == null) {

            System.out.println("No se encontró el préstamo.");

            return false;
        }

        if (!"ACTIVO".equals(p.getEstado())) {

            System.out.println("El préstamo ya está pagado.");

            return false;
        }

        return true;
    }



    private void mostrarCuotas(prestamos p) {

        List<cuota> lista = p.getCuotas();

        System.out.println("\n===== CUOTAS DEL PRÉSTAMO " + p.getId() + " =====");

        for (int i = 0; i < lista.size(); i++) {

            System.out.println(lista.get(i).toString(i + 1));
        }
    }



    private void mostrarResultado(pago registrado) throws SQLException {

        System.out.println("\nPAGO REGISTRADO CORRECTAMENTE.");

        System.out.println(registrado);


        prestamos actualizado = daoPrestamo.buscarId(
            registrado.getPrestamoId()
        );


        gestion.sincronizarTodos(daoPrestamo.listar());


        gestionPago.sincronizarTodos(dao.listarTodos());

        mostrarCuotas(actualizado);

        if ("PAGADO".equals(actualizado.getEstado())) {

            System.out.println("\nEl préstamo quedó totalmente pagado.");

        } else {

            System.out.println(
                "\nSaldo pendiente: "
                + String.format("%.2f", actualizado.getSaldoPendiente())
            );
        }
    }



    private void pagarCuota() throws SQLException {

        System.out.println("\n===== PAGAR CUOTA =====");

        long id = ingreso.pedirId();

        prestamos p = daoPrestamo.buscarId(id);

        if (!prestamoActivo(p)) {
            return;
        }


        List<cuota> cuotas = p.getCuotas();

        int posicion = -1;

        for (int i = 0; i < cuotas.size(); i++) {

            if (!cuotas.get(i).isPagada()) {
                posicion = i;
                break;
            }
        }

        if (posicion == -1) {

            System.out.println("El préstamo no tiene cuotas pendientes.");

            return;
        }

        cuota siguiente = cuotas.get(posicion);

        System.out.println("\nSiguiente cuota a pagar:");

        System.out.println(siguiente.toString(posicion + 1));

        String valor = String.format("%.2f", siguiente.getSaldo());

        if (!ingreso.confirmar("¿Desea pagar esta cuota por " + valor + "?")) {

            System.out.println("Pago cancelado.");

            return;
        }

        String metodo = ingreso.pedirMetodo();

        try {

            pago pagoHecho = dao.pagarCuota(id, metodo);

            mostrarResultado(pagoHecho);

            bitacora.registrar("PAGOS", "PAGAR_CUOTA", "Prestamo " + id + " - Valor: " + String.format("%.2f", pagoHecho.getMonto()) + " - Metodo: " + metodo);

        } catch (SQLException e) {

            System.out.println("No se pudo registrar el pago: " + e.getMessage());
        }
    }



    private void abonar() throws SQLException {

        System.out.println("\n===== ABONAR DINERO =====");

        long id = ingreso.pedirId();

        prestamos p = daoPrestamo.buscarId(id);

        if (!prestamoActivo(p)) {
            return;
        }

        System.out.println(
            "Saldo pendiente del préstamo: "
            + String.format("%.2f", p.getSaldoPendiente())
        );

        double monto = ingreso.pedirMonto();

        if (monto <= 0) {

            System.out.println("El monto debe ser mayor a cero.");

            return;
        }

        if (monto > p.getSaldoPendiente() + 0.01) {

            System.out.println("El monto supera el saldo pendiente.");

            return;
        }

        String metodo = ingreso.pedirMetodo();

        try {

            pago pagoHecho = dao.abonar(id, monto, metodo);

            mostrarResultado(pagoHecho);

            bitacora.registrar("PAGOS", "ABONAR", "Prestamo " + id + " - Valor: " + String.format("%.2f", pagoHecho.getMonto()) + " - Metodo: " + metodo);

        } catch (SQLException e) {

            System.out.println("No se pudo registrar el abono: " + e.getMessage());
        }
    }



    private void historial() throws SQLException {

        System.out.println("\n===== HISTORIAL DE PAGOS =====");

        long id = ingreso.pedirId();

        prestamos p = daoPrestamo.buscarId(id);

        if (p == null) {

            System.out.println("No se encontró el préstamo.");

            return;
        }

        List<pago> lista = dao.historialPorPrestamo(id);

        bitacora.registrar("PAGOS", "HISTORIAL", "Prestamo ID " + id);

        if (lista.isEmpty()) {

            System.out.println("El préstamo no tiene pagos registrados.");

            return;
        }

        System.out.println("\nPagos del préstamo " + id + ":");

        for (pago pg : lista) {

            System.out.println(pg);
        }

        System.out.println(
            "\nTotal pagado: " + String.format("%.2f", p.getTotalPagado())
        );

        System.out.println(
            "Saldo pendiente: " + String.format("%.2f", p.getSaldoPendiente())
        );
    }



    public void menuConsulta() throws SQLException {

        int op;

        do {

            op = menu.menuPagosConsulta();

            switch (op) {

                case 1 -> historial();

                case 2 -> System.out.println("Regresando al menú...");

                default ->
                    System.out.println("Opción fuera de rango.");
            }

        } while (op != 2);
    }



    public void menu() throws SQLException {

        int op;

        do {

            op = menu.menuPagos();

            switch (op) {

                case 1 -> pagarCuota();

                case 2 -> abonar();

                case 3 -> historial();

                case 4 -> System.out.println(
                    "Regresando al menú principal..."
                );

                default ->
                    System.out.println("Opción fuera de rango.");
            }

        } while (op != 4);
    }
}
