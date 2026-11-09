package controller;

import dao.auditoriaDao;
import dao.reporteDao;
import model.entity.ReporteGenerado;
import model.entity.registroAccion;
import model.gestion.gestionReportes;
import model.service.bitacora;
import view.mensajes;
import view.menus.menuAdmin;
import view.validaciones;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.text.NumberFormat;
import java.text.ParseException;


public class controllerReportes {

    private static final List<String> MODULOS = List.of(
            "EMPLEADOS", "CLIENTES", "SOLICITUDES", "PRESTAMOS", "PAGOS", "SESION", "REPORTES");

    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws SQLException;
    }

    private final auditoriaDao auditoria = new auditoriaDao();
    private final reporteDao reportes = new reporteDao();
    private final menuAdmin menu = new menuAdmin();
    private final validaciones v = new validaciones();
    private final mensajes m = new mensajes();

    private final List<String> salida = new ArrayList<>();
    private final Consumer<String> imprimir = linea -> {
        System.out.println(linea);
        salida.add(linea);
    };

    private final gestionReportes gestion = new gestionReportes();

    private final Predicate<String[]> sinPrestamos = f -> numero(f[3]) == 0;
    private final Predicate<String[]> conDeuda = f -> numero(f[5]) > 0;

    
    private static double numero(String texto) {

    if ("-".equals(texto)) {
        return 0;
    }

    try {
        return NumberFormat.getInstance().parse(texto).doubleValue();
    } catch (ParseException e) {
        throw new IllegalArgumentException("Valor numerico no valido en el reporte: " + texto, e);
    }
}

    private void tabla(String[] encabezados, List<String[]> filas) {

        if (filas.isEmpty()) {
            imprimir.accept("  (sin datos)");
            return;
        }

        int[] ancho = IntStream.range(0, encabezados.length)
                .map(i -> Math.max(encabezados[i].length(),
                        filas.stream().mapToInt(f -> f[i].length()).max().orElse(0)))
                .toArray();

        Function<String[], String> formatear = fila ->
                IntStream.range(0, ancho.length)
                        .mapToObj(i -> String.format("%-" + ancho[i] + "s  ", fila[i]))
                        .collect(Collectors.joining());

        imprimir.accept(formatear.apply(encabezados));
        imprimir.accept(IntStream.of(ancho)
                .mapToObj(a -> "-".repeat(a) + "  ")
                .collect(Collectors.joining()));

        filas.stream().map(formatear).forEach(imprimir);
    }

    private void indicadores(String[] etiquetas, List<String[]> filas) {

        if (filas.isEmpty()) {
            imprimir.accept("  (sin datos)");
            return;
        }

        String[] fila = filas.get(0);

        IntStream.range(0, etiquetas.length)
                .mapToObj(i -> String.format("  %-34s %s", etiquetas[i] + ":", fila[i]))
                .forEach(imprimir);
    }

    private void subtitulo(String texto) {
        imprimir.accept("\n>> " + texto);
    }

    private void listarRegistros(List<registroAccion> lista) {

        if (lista.isEmpty()) {
            imprimir.accept("No hay acciones registradas.");
            return;
        }

        lista.stream().map(String::valueOf).forEach(imprimir);

        imprimir.accept("\nTotal de acciones: " + lista.size());
    }


    private void accionesPorUsuarios(String tipo, String titulo) throws SQLException {

        m.informativo("ACCIONES DE " + titulo);

        List<String[]> resumen = auditoria.resumenPorUsuario(tipo);

        if (resumen.isEmpty()) {
            imprimir.accept("Todavia no hay acciones registradas.");
            return;
        }

        tabla(new String[]{"ID", "Nombre", "Acciones", "Sesiones", "Ultima actividad"},
                resumen);

        long id = v.validarEnteroGrande(
                "\nIngrese el ID para ver el detalle de sus acciones (0 para volver):");

        if (id == 0) {
            return;
        }

        List<registroAccion> detalle = auditoria.porUsuario(id, tipo);

        imprimir.accept("");
        listarRegistros(detalle);
    }

    private void accionesPorModulo() throws SQLException {

        m.informativo("ACCIONES POR MODULO");

        tabla(new String[]{"Modulo", "Accion", "Veces"}, auditoria.resumenPorModulo());

        String opciones = IntStream.range(0, MODULOS.size())
                .mapToObj(i -> (i + 1) + "- " + MODULOS.get(i))
                .collect(Collectors.joining("\n",
                        "\nVer el detalle de un modulo:\n",
                        "\n0- Volver\n"));

        int op = v.validarEntero(opciones);

        if (op < 1 || op > MODULOS.size()) {
            return;
        }

        imprimir.accept("");
        listarRegistros(auditoria.porModulo(MODULOS.get(op - 1)));
    }

    private void ultimosMovimientos() throws SQLException {

        m.informativo("ULTIMOS 30 MOVIMIENTOS DEL SISTEMA");
        listarRegistros(auditoria.ultimas(30));
    }


    private void reporteEmpleados() throws SQLException {

        m.informativo("REPORTE GENERAL - EMPLEADOS");

        subtitulo("Empleados por rol y nomina");
        tabla(new String[]{"Rol", "Cantidad", "Nomina total", "Salario promedio", "Salario mayor"},
                reportes.empleadosPorRol());

        subtitulo("Gestion de prestamos por empleado");
        List<String[]> desempeno = reportes.empleadosDesempeno();
        tabla(new String[]{"ID", "Nombre", "Rol", "Prestamos", "Capital colocado"}, desempeno);

        long sinGestion = desempeno.stream().filter(sinPrestamos).count();
        imprimir.accept("  Empleados sin prestamos gestionados: " + sinGestion);
    }

    private void reporteClientes() throws SQLException {

        m.informativo("REPORTE GENERAL - CLIENTES");

        subtitulo("Resumen");
        indicadores(new String[]{"Clientes registrados", "Con solicitudes",
            "Con prestamos", "Con prestamo activo"},
                reportes.clientesResumen());

        subtitulo("Detalle por cliente");
        List<String[]> detalle = reportes.clientesDetalle();
        tabla(new String[]{"ID", "Nombre", "Solicitudes", "Prestamos",
            "Total prestado", "Deuda pendiente"}, detalle);

        long enMora = detalle.stream().filter(conDeuda).count();
        imprimir.accept("  Clientes con deuda pendiente: " + enMora);
    }

    private void reporteSolicitudes() throws SQLException {

        m.informativo("REPORTE GENERAL - SOLICITUDES");

        subtitulo("Resumen");
        indicadores(new String[]{"Total de solicitudes", "Monto solicitado",
            "Monto promedio", "Monto mayor", "Monto menor", "Cuotas promedio"},
                reportes.solicitudesResumen());

        subtitulo("Por estado");
        tabla(new String[]{"Estado", "Cantidad", "Monto total", "Monto promedio"},
                reportes.solicitudesPorEstado());
    }

    private void reportePrestamos() throws SQLException {

        m.informativo("REPORTE GENERAL - PRESTAMOS");

        subtitulo("Indicadores");
        indicadores(new String[]{"Total de prestamos", "Interes promedio (%)",
            "Monto promedio prestado", "Total recaudado", "Total por cobrar"},
                reportes.prestamosIndicadores());

        subtitulo("Por estado");
        tabla(new String[]{"Estado", "Cantidad", "Capital", "Intereses", "Total a pagar"},
                reportes.prestamosPorEstado());
    }

    private void reportePagos() throws SQLException {

        m.informativo("REPORTE GENERAL - PAGOS Y CUOTAS");

        subtitulo("Pagos recibidos");
        indicadores(new String[]{"Cantidad de pagos", "Total recaudado",
            "Pago promedio", "Pago mayor"},
                reportes.pagosResumen());

        subtitulo("Por metodo de pago");
        tabla(new String[]{"Metodo", "Pagos", "Recaudado"}, reportes.pagosPorMetodo());

        subtitulo("Por mes (ultimos 12)");
        tabla(new String[]{"Mes", "Pagos", "Recaudado"}, reportes.pagosPorMes());

        subtitulo("Cuotas por estado");
        tabla(new String[]{"Estado", "Cuotas", "Valor", "Pagado"}, reportes.cuotasPorEstado());

        subtitulo("Cuotas vencidas (en mora)");
        indicadores(new String[]{"Cuotas vencidas", "Prestamos afectados", "Monto en mora"},
                reportes.cuotasVencidas());
    }

    private void resumenGeneral() throws SQLException {

        m.informativo("RESUMEN GENERAL DEL SISTEMA");

        long empleados = reportes.empleadosPorRol().stream()
                .mapToLong(f -> (long) numero(f[1]))
                .sum();

        List<String[]> resumenEmpleados = auditoria.resumenPorUsuario("EMPLEADO");
        List<String[]> resumenClientes = auditoria.resumenPorUsuario("CLIENTE");

        long acciones = Stream.concat(resumenEmpleados.stream(), resumenClientes.stream())
                .mapToLong(f -> (long) numero(f[2]))
                .sum();

        subtitulo("Personas");
        imprimir.accept(String.format("  %-34s %d", "Empleados:", empleados));
        indicadores(new String[]{"Clientes registrados", "Clientes con solicitudes",
            "Clientes con prestamos", "Clientes con prestamo activo"},
                reportes.clientesResumen());

        subtitulo("Solicitudes");
        indicadores(new String[]{"Total de solicitudes", "Monto solicitado"},
                reportes.solicitudesResumen());

        subtitulo("Prestamos y cartera");
        indicadores(new String[]{"Total de prestamos", "Interes promedio (%)",
            "Monto promedio prestado", "Total recaudado", "Total por cobrar"},
                reportes.prestamosIndicadores());

        subtitulo("Mora");
        indicadores(new String[]{"Cuotas vencidas", "Prestamos afectados", "Monto en mora"},
                reportes.cuotasVencidas());

        subtitulo("Actividad");
        imprimir.accept(String.format("  %-34s %d", "Acciones registradas en bitacora:", acciones));
    }


    private void reporte(Accion accion, String nombre) throws SQLException {

        salida.clear();

        accion.ejecutar();

        gestion.guardar(new ReporteGenerado(nombre, LocalDateTime.now(), List.copyOf(salida)));
        System.out.println("Reporte guardado en la base de datos y en reportesfinal.dat.");

        bitacora.registrar("REPORTES", nombre, null);
    }

    private void ejecutar(Accion accion, String nombreAccion) throws SQLException {
        accion.ejecutar();
        bitacora.registrar("REPORTES", nombreAccion, null);
    }

    public void menu() throws SQLException {

        int op;

        do {

            op = menu.menuReportes();

            switch (op) {

                case 1 -> ejecutar(() -> accionesPorUsuarios("EMPLEADO", "EMPLEADOS"),
                        "CONSULTAR_ACCIONES_EMPLEADOS");

                case 2 -> ejecutar(() -> accionesPorUsuarios("CLIENTE", "CLIENTES"),
                        "CONSULTAR_ACCIONES_CLIENTES");

                case 3 -> ejecutar(this::accionesPorModulo, "CONSULTAR_ACCIONES_MODULO");

                case 4 -> ejecutar(this::ultimosMovimientos, "CONSULTAR_ULTIMOS_MOVIMIENTOS");

                case 5 -> reporte(this::reporteEmpleados, "REPORTE_EMPLEADOS");

                case 6 -> reporte(this::reporteClientes, "REPORTE_CLIENTES");

                case 7 -> reporte(this::reporteSolicitudes, "REPORTE_SOLICITUDES");

                case 8 -> reporte(this::reportePrestamos, "REPORTE_PRESTAMOS");

                case 9 -> reporte(this::reportePagos, "REPORTE_PAGOS");

                case 10 -> reporte(this::resumenGeneral, "RESUMEN_GENERAL");

                case 11 -> imprimir.accept("Regresando al menu principal...");

                default -> imprimir.accept("Opcion fuera de rango.");
            }

        } while (op != 11);
    }
}
