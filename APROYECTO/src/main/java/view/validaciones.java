/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.util.Scanner;

public class validaciones {

    public String validarTexto(String mensaje) {
        String dato;
        boolean validacion;
        do {
            validacion = true;
            System.out.println(mensaje);
            dato = new Scanner(System.in).nextLine();
            if (dato.isBlank()) {
                System.out.println("Error, No se aceptan espacios en blanco");
                validacion = false;
                continue;
            }

            dato = dato.strip();
            for (int i = 0; i < dato.length(); i++) {

                if (Character.isLetter(dato.charAt(i)) == false) {
                    if (dato.charAt(i) != ' ') {
                        System.out.println("Error, solo se aceptan letras o espacios");
                        validacion = false;
                        break;
                    }
                }
            }
        } while (validacion == false);
        return dato;
    }
    
    
    public String validarCadenas(String mensaje) {

    String dato;
    boolean validacion;

    do {
        validacion = true;

        System.out.println(mensaje);
        dato = new Scanner(System.in).nextLine().strip();

        if (dato.isBlank()) {
            System.out.println("Error: el documento no puede estar vacío.");
            validacion = false;
            continue;
        }

        for (int i = 0; i < dato.length(); i++) {

            if (!Character.isDigit(dato.charAt(i))) {
                System.out.println("Error: el documento solo debe contener números.");
                validacion = false;
                break;
            }
        }

    } while (!validacion);

    return dato;
    }
    
    public String validarCorreo(String mensaje) {

    String correo;
    boolean validacion;

    do {
        validacion = true;

        System.out.println(mensaje);
        correo = new Scanner(System.in).nextLine().strip();

        if (correo.isBlank() || !correo.contains("@")) {
            System.out.println("Error: el correo debe contener @.");
            validacion = false;
        }

    } while (!validacion);

    return correo;
}

    public String validarClave(String mensaje) {
        java.io.Console consola = System.console();
        if (consola != null) {
            char[] clave = consola.readPassword("%s ", mensaje);
            return clave == null ? "" : new String(clave);
        }
        System.out.println(mensaje);
        return new Scanner(System.in).nextLine();
    }

    public double validarDecimal(String mensaje) {
        System.out.println(mensaje);
        Scanner x = new Scanner(System.in);
        while (x.hasNextDouble() == false) {
            System.out.println("Error, se espera un valor decimal");
            x.next();
        }
        return x.nextDouble();
    }

    public int validarEntero(String mensaje) {
        Scanner x = new Scanner(System.in);
        System.out.println(mensaje);
        while (x.hasNextInt() == false) {
            System.out.println("Error, se espera un valor entero");
            x.next();
        }
        return x.nextInt();
    }

    public long validarEnteroGrande(String mensaje) {
        Scanner x = new Scanner(System.in);
        System.out.println(mensaje);
        while (x.hasNextLong() == false) {
            System.out.println("Error, se espera un valor entero");
            x.next();
        }
        return x.nextLong();
    }
}