package com.example.asistencia;

import com.example.Logueo.DataBasePostgresConection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by DELTEC on 19/08/2015.
 */
public class EjecucionAsistenciaModelServer {

    Connection conn;
    DataBasePostgresConection c;

    /**
     * Constructor Manejador de Bd SERVER
     */
    public EjecucionAsistenciaModelServer(int tipoUsuario){

        c  = new DataBasePostgresConection(tipoUsuario);
    } //Cierre Constructor

    public Connection getDBConnection() {

        Connection dbConnection = null;
        try {
            Class.forName(c.getDbDriver());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            dbConnection = DriverManager.getConnection(c.getConection(),c.getUserName(),c.getPassword());
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return dbConnection;
    }

    /**
     * Consulta Asistentes por Supervisores
     * @param codeSuper
     * @return
     */
    public ResultSet getAssistantSuper(String codeSuper){

        ResultSet rs= null;
        String stsql = "	SELECT sup_super_per_codigo, sup_lect_per_codigo, per_apellido1 || ' ' || per_nombre  AS per_nombre " +
                "	FROM supervisor_lector INNER JOIN persona ON sup_lect_per_codigo = per_codigo  " +
                "	WHERE sup_super_per_codigo = " + codeSuper;
        try {
            conn = getDBConnection();
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMAnO
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return rs;
    }// Cierre Consulta de Asistentes

    /**
     * Return the assitant selected
     * @param codeAssistant
     * @return
     */
    public ResultSet getAssistant(String codeAssistant){

        ResultSet rs= null;
        String stsql = "	SELECT per_apellido1 || ' ' || per_nombre  AS per_nombre \n" +
                "	FROM persona " +
                "	WHERE gru_codigo = 3 AND per_codigo = " + codeAssistant;
        try {
            conn = getDBConnection();
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMAnO
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return rs;
    }// Cierre Consulta de Asistentes

    public boolean actualizarAsistencia(String asi_hora, int asi_per_codigo, int asi_supervisor, int asi_estado, int asi_tipo,
                                           String asi_fecha_creacion,String asi_fecha_actulizacion, String asi_observacion ){
        String stsqlUpdate = "",stsqlInsert = "";
        Statement st=null;
        int aff = 0;
        boolean ejecuto = false;

        stsqlUpdate = " UPDATE asistencia SET " +
                "	asi_estado = "+ asi_estado +"," +
                "	asi_tipo = "+ asi_tipo + "," +
                "	asi_hora = '"+ asi_hora +"'," +
                "	asi_fecha_actualizacion = '"+ asi_fecha_actulizacion +"'," +
                "	asi_observacion = '" + asi_observacion + "'" +
                "	WHERE asi_supervisor = "+ asi_supervisor + " AND asi_per_codigo=" + asi_per_codigo + " AND asi_fecha_creacion = '"+ asi_fecha_creacion +"'";
        try {
            conn = getDBConnection();
            conn.setAutoCommit(false);
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMA?O
            aff = st.executeUpdate(stsqlUpdate);

            if(aff == 0){
                stsqlInsert = "INSERT INTO asistencia (asi_hora, asi_per_codigo, asi_supervisor," +
                        "asi_estado, asi_tipo, asi_fecha_creacion, asi_fecha_actualizacion, asi_observacion)" +
                        "VALUES('"+ asi_hora + "'," + asi_per_codigo +", "+ asi_supervisor +", "+ asi_estado +"," +
                        ""+ asi_tipo +", '"+ asi_fecha_creacion +"','"+ asi_fecha_actulizacion +"','"+ asi_observacion + "')";
                aff = st.executeUpdate(stsqlInsert);
            }
            st.close();
            conn.commit();
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
            System.out.println(" SQL_" + stsqlInsert);
        }

        if(aff == 1)
            ejecuto = true;

        return ejecuto;
    }

}
