package com.javaprojects.aproyecto;

import controller.controllerMenugeneral;
import java.sql.SQLException;



public class APROYECTO {

    public static void main(String[] args) throws SQLException {

         controllerMenugeneral menu= new controllerMenugeneral();
         menu.gestionarMenu();
    }
}
