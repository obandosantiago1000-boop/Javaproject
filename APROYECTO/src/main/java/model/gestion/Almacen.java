package model.gestion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Almacen<T extends Serializable> {

    private final String ruta;

    public Almacen(String ruta) {
        this.ruta = ruta;
    }

    @SuppressWarnings("unchecked")
    public List<T> cargar() {

        File archivo = new File(ruta);

        if (!archivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {

            return new ArrayList<>((List<T>) ois.readObject());

        } catch (IOException | ClassNotFoundException | ClassCastException e) {

            System.out.println("Error al cargar " + ruta + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void guardar(List<T> datos) {

        escribir(new File(ruta), datos);
    }


    public boolean exportar(List<T> datos, String destino) {

        return escribir(new File(destino), datos);
    }

    private boolean escribir(File archivo, List<T> datos) {

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {

            oos.writeObject(new ArrayList<>(datos));
            return true;

        } catch (IOException e) {

            System.out.println("Error al guardar " + archivo.getPath() + ": " + e.getMessage());
            return false;
        }
    }
}
