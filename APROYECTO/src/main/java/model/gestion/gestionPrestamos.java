package model.gestion;

import model.entity.prestamos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class gestionPrestamos {

    private List<prestamos> prestamos = new ArrayList<>();

    private final String ARCHIVO = "prestamos_archivo.dat";



    public gestionPrestamos() {
        cargar();
    }


    @SuppressWarnings("unchecked")
    public void cargar() {

        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            return;
        }

        try (ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(archivo))) {

            prestamos = (List<prestamos>) ois.readObject();

            System.out.println("Préstamos cargados correctamente.");

        } catch (Exception e) {

            System.out.println(
                "Error al cargar los préstamos: " + e.getMessage()
            );
        }
    }



    private void guardar() {

        try (ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {

            oos.writeObject(prestamos);

        } catch (Exception e) {

            System.out.println(
                "Error al guardar los préstamos: " + e.getMessage()
            );
        }
    }



    public void sincronizar(prestamos prestamo) {

        for (int i = 0; i < prestamos.size(); i++) {

            if (prestamos.get(i).getId() == prestamo.getId()) {

                prestamos.set(i, prestamo);

                guardar();

                return;
            }
        }

        prestamos.add(prestamo);

        guardar();
    }



    public void sincronizarTodos(List<prestamos> lista) {

        prestamos = new ArrayList<>(lista);

        guardar();
    }



    public void exportarBackup(String ruta) {

        File destino = new File(ruta);

        try (ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(destino))) {

            oos.writeObject(prestamos);

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