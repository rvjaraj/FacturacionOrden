/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import modelo.Trabajos;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author Vinic
 */
public class ControladorTrabajos {

    private Connection con;
    private ResultSet seter;

    public void conectar() {
        con = null;
        seter = null;
        try {
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/BaseMaquinasElectricas", "postgres", "root");
        } catch (SQLException ex) {
            System.out.println("Error de conexion : " + ex.getMessage());
        }

        if (con != null) {
        }
    }

    public void crear(Trabajos p) {
        String sql = "INSERT INTO public.\"Trabajos\" values("
                + p.getCodigo() + ",'"
                + p.getNombre() + "',"
                + p.getPrecio() + ")";

        try {
            conectar();
            Statement sta = (Statement) con.createStatement();
            sta.executeUpdate(sql);
            desconectar();
        } catch (SQLException ex) {
            System.out.println("Error de sql : " + ex.getMessage());
        }
    }

    public int obtenerCodigo() {
        String sql = "SELECT MAX(\"trabajosId\") FROM \"Trabajos\";";
        int codigo = 0;
        try {
            conectar();
            Statement sta = con.createStatement();
            ResultSet res = sta.executeQuery(sql);
//           res.first();
            res.next();
            codigo = res.getInt(1);
            res.close();
            sta.close();
            desconectar();
        } catch (SQLException ex) {
            Logger.getLogger(ControladorTrabajos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return codigo + 1;
    }

    public void desconectar() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                System.out.println("Erro al desconctar " + ex.getMessage());
            }
        }
    }

    public ArrayList<Trabajos> listar() {
        conectar();
        String sql = "select * from \"Trabajos\" ";
        ArrayList<Trabajos> lista = new ArrayList<>();
        try {
            Statement se = con.createStatement();
            seter = se.executeQuery(sql);
            while (seter.next()) {
                int codigo = seter.getInt(1);
                String nombre = seter.getString(2);
                double precio = seter.getDouble(3);

                Trabajos t = new Trabajos(codigo, nombre, precio);
                lista.add(t);
            }
        } catch (SQLException ex) {
            System.out.println("Error de lectura jaaja :" + ex.getMessage());
        }
        desconectar();
        return lista;

    }

    public Trabajos buscar(String cod) {

        String busca = "select * from \"Trabajos\" where \"trabajosId\" =" + cod;
        try {
            conectar();
            PreparedStatement se = con.prepareCall(busca);
            seter = se.executeQuery();
            while (seter.next()) {

                int codigo = seter.getInt(1);
                String nombre = seter.getString(2);
                double precio = seter.getDouble(3);

                Trabajos t = new Trabajos(codigo, nombre, precio);
                return t;
            }
        } catch (SQLException ex) {
            System.out.println("Error de lectura :" + ex.getMessage());
        }
        return null;
    }

    public void eliminar(int codigo) {
        String eli = "DELETE FROM public.\"Trabajos\" where \"trabajosId\"=" + codigo;
        try {
            conectar();
            Statement se = (Statement) con.createStatement();
            se.executeUpdate(eli);
            desconectar();
        } catch (SQLException ex) {
            System.out.println("Error de lectura :" + ex.getMessage());
        }

    }

    public void actualizar(Trabajos c) {
        try {
            String ac = "UPDATE public.\"Trabajos\" SET"
                    + " \"traPrecio\"=" + c.getPrecio() + ","
                    + " \"traNombre\"='" + c.getNombre() + "'"
                    + "where \"trabajosId\"=" + c.getCodigo();
            conectar();
            Statement se = (Statement) con.createStatement();
            se.executeUpdate(ac);
            desconectar();
        } catch (SQLException ex) {
            System.out.println("Error de lectura : " + ex.getMessage());
        }
    }

    public String mayusculas(JTextField txt) {
        String text = txt.getText();
        return text.toUpperCase();
    }

    public boolean IdentificadorCedula(String ced) {
        boolean verdadera = false;
        int num = 0;
        int ope = 0;
        int suma = 0;
        for (int cont = 0; cont < ced.length(); cont++) {
            num = Integer.valueOf(ced.substring(cont, cont + 1));
            if (cont == ced.length() - 1) {
                break;
            }
            if (cont % 2 != 0 && cont > 0) {
                suma = suma + num;
            } else {
                ope = num * 2;
                if (ope > 9) {
                    ope = ope - 9;
                }
                suma = suma + ope;
            }

        }
        suma = suma % 10;
        if (suma == 0) {
            if (suma == num) {
                verdadera = true;
            }
        } else {
            ope = 10 - suma;
            if (ope == num) {
                verdadera = true;
            }
        }
        return verdadera;
    }

    public void keyPressCedula(JTextField t) {

    }

    public void keyReleesCedula(JTextField t, KeyEvent evt) {
        if (t.getText().length() == 10) {
            if (IdentificadorCedula(t.getText()) == true) {
                t.setBackground(Color.GREEN);
            } else {
                t.setBackground(Color.red);
            }
            evt.consume();
        }
    }

    public void keyTypedCedula(JTextField t, KeyEvent evt) {
        if (evt.getKeyChar() == KeyEvent.VK_ENTER) {

        } else if (evt.getKeyChar() < '0' || evt.getKeyChar() > '9') {
            evt.consume();
            t.setBackground(Color.RED);
        } else {
            t.setBackground(Color.CYAN);
            if (t.getText().length() == 13) {
                evt.consume();
            }
        }
    }

    public void keyTypedMayusculas(JTextField t, KeyEvent evt) {
        char c = evt.getKeyChar();
        if (Character.isDigit(c)) {
            t.getToolkit().beep();
            t.setBackground(Color.red);
            evt.consume();
        } else {
            t.setBackground(Color.green);
            if (t.getText().length() == 20) {
                t.setBackground(Color.green);
                evt.consume();
            }
        }
    }

    public void keyRealseTelefono(JTextField t) {
        if (t.getText().length() == 10) {
            t.setBackground(Color.green);
        }
    }

    public void keyTypedTelefono(JTextField t, KeyEvent evt) {
        if (evt.getKeyChar() < '0' || evt.getKeyChar() > '9') {
            evt.consume();
            t.setBackground(Color.red);
        } else {
            t.setBackground(Color.CYAN);
            if (t.getText().length() == 10) {
                t.setBackground(Color.green);
                evt.consume();
            }
        }
    }
    
    public ArrayList<String> comboTrabajos(){
        ArrayList<String> li = new ArrayList<>();
        ArrayList<Trabajos> lista = listar();
        li.add("Selecione");
        for (int i = 0; i < lista.size(); i++) {
            li.add(lista.get(i).getCodigo() +": "+lista.get(i).getNombre() + " >> " + lista.get(i).getPrecio());
        }
        return li;
    }

}
