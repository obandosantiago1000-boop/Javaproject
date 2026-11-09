package view.ingresos;

import view.validaciones;

public class ingresoPagos {
    validaciones v = new validaciones();

    public long pedirId() {
        return v.validarEnteroGrande("Ingrese el id del préstamo:");
    }

    public double pedirMonto() {
        return v.validarDecimal("Ingrese el monto a abonar:");
    }


    public String pedirMetodo() {

        int op;

        do {

            op = v.validarEntero("""

                                 Método de pago:
                                 1- Efectivo
                                 2- Transferencia
                                 3- Tarjeta
                                 4- Consignación
                                 """);

            if (op < 1 || op > 4) {
                System.out.println("Opción fuera de rango.");
            }

        } while (op < 1 || op > 4);

        return switch (op) {
            case 1 -> "EFECTIVO";
            case 2 -> "TRANSFERENCIA";
            case 3 -> "TARJETA";
            default -> "CONSIGNACION";
        };
    }

    public boolean confirmar(String mensaje) {

        int op = v.validarEntero(mensaje + """

                                 1- Confirmar
                                 2- Cancelar
                                 """);

        return op == 1;
    }
}
