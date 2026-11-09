package model.gestion;

import model.entity.Solicitudes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class gestionSolicitudes {

    private List<Solicitudes> solicitudes = new ArrayList<>();

    private final String ARCHIVO = "solicitudes_archivo.dat";



    public gestionSolicitudes() {
        cargar();
    }


    public void cargar() {

        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            return;
        }

        try (ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(archivo))) {

            solicitudes = (List<Solicitudes>) ois.readObject();

            System.out.println("Solicitudes cargadas correctamente.");

        } catch (Exception e) {

            System.out.println(
                "Error al cargar las solicitudes: " + e.getMessage()
            );
        }
    }



    private void guardar() {

        try (ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {

            oos.writeObject(solicitudes);

        } catch (Exception e) {

            System.out.println(
                "Error al guardar las solicitudes: " + e.getMessage()
            );
        }
    }



    public void sincronizar(Solicitudes solicitud) {

        for (int i = 0; i < solicitudes.size(); i++) {

            if (solicitudes.get(i).getId() == solicitud.getId()) {

                solicitudes.set(i, solicitud);

                guardar();

                return;
            }
        }

        solicitudes.add(solicitud);

        guardar();
    }



    public void sincronizarTodos(List<Solicitudes> lista) {

        solicitudes = new ArrayList<>(lista);

        guardar();
    }


    public void eliminar(long id) {

        boolean eliminado = solicitudes.removeIf(
            s -> s.getId() == id
        );

        if (eliminado) {
            guardar();
        }
    }



    public void exportarBackup(String ruta) {

        File destino = new File(ruta);

        try (ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(destino))) {

            oos.writeObject(solicitudes);

            System.out.println("Backup exportado correctamente.");

            System.out.println(
                "Ubicación: " + destino.getAbsolutePath()
            );

        } catch (Exception e) {

            System.out.println(
                "Error al exportar el backup: " + e.getMessage()
            );
        }
    }
}
