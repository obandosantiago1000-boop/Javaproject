/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.gestion;
import model.entity.personas;
import model.entity.empleados;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class gestionPersonas {

    private List<personas> personas = new ArrayList<>();

    private final String ARCHIVO = "personas_archivo.dat";
    
    
    
    

public void sincronizar(personas persona) {

    for (int i = 0; i < personas.size(); i++) {

        if (personas.get(i).getId() == persona.getId()) {

            personas.set(i, persona);
            guardar();

            return;
        }
    }

    personas.add(persona);
    guardar();
}



    public void cargar() {

        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            System.out.println("Todavía no existe un archivo de personas.");
            return;
        }

        try (ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(archivo))) {

            personas = (List<personas>) ois.readObject();

            System.out.println("Personas cargadas correctamente.");

        } catch (Exception e) {

            System.out.println("Error al cargar las personas: " + e.getMessage());
        }
    }


    private void guardar() {

        try (ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {

            oos.writeObject(personas);

        } catch (Exception e) {

            System.out.println("Error al guardar las personas: " + e.getMessage());
        }
    }

  


    public void registrar(personas persona) {

        long nuevoId = personas.isEmpty()
                ? 1
                : personas.get(personas.size() - 1).getId() + 1;

        persona.setId(nuevoId);

        personas.add(persona);

        guardar();

        System.out.println("Persona registrada correctamente.");
    }


    public void listar() {

        if (personas.isEmpty()) {
            System.out.println("No hay personas registradas.");
            return;
        }

        for (personas p : personas) {
            System.out.println(p);
        }
    }


public void buscarPorDocumento(String documento) {

    for (personas p : personas) {

        if (p.getDocumento().equals(documento)) {

            System.out.println(p);
            return;
        }
    }

    System.out.println("No existe una persona con ese documento.");
}


    public void eliminar(long id) {

        boolean eliminado = personas.removeIf(p -> p.getId() == id);

        if (eliminado) {

            guardar();

            System.out.println("Persona eliminada correctamente.");

        } else {

            System.out.println("No existe una persona con ese ID.");
        }
    }

public void buscarPorRol(String rol) {

    boolean encontrado = false;

    for (personas p : personas) {

        if (p instanceof empleados) {

            empleados empleado = (empleados) p;

            if (empleado.getRol().equalsIgnoreCase(rol)) {

                System.out.println(empleado);
                encontrado = true;
            }
        }
    }

    if (!encontrado) {
        System.out.println("No existen empleados con ese rol.");
    }
}


    public void exportarBackup(String ruta) {

        File destino = new File(ruta);

        try (ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(destino))) {

            oos.writeObject(personas);

            System.out.println("Backup exportado correctamente.");
            System.out.println("Ubicación: " + destino.getAbsolutePath());

        } catch (Exception e) {

            System.out.println("Error al exportar el backup: " + e.getMessage());
        }
    }
}
