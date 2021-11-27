package com.example.formato;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by DELTEC on 11/09/2015.
 */
public class EjecucionFormatoModelServer {
    Connection conn;
    DataBasePostgresConectionFormat c;

    /**
     * Constructor Manejador de Bd SERVER
     */
    public EjecucionFormatoModelServer(int tipoUsuario){

        c  = new DataBasePostgresConectionFormat(tipoUsuario);
    } //Cierre Constructor

    public Connection getDBConnection() {

        Connection dbConnection = null;
        try {
            Class.forName(c.getDbDriver());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            dbConnection = DriverManager.getConnection(c.getConection(), c.getUserName(), c.getPassword());
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return dbConnection;
    }

    /**
     * Get structure format full the server
     * @return
     */
    public ResultSet getFormatServer(){

        ResultSet rs= null;
        String stsql = "	SELECT  tf.id AS tf_id, tf.nombre AS tf_nombre," +
                "                   mf.id AS mf_id, mf.nombre AS mf_nombre, mf.codigo AS mf_codigo, mf.numero_orden AS mf_numero_orden," +
                "                   cf.id AS cf_id, cf.nombre AS cf_nombre, cf.codigo AS cf_codigo, cf.numero_orden AS cf_numero_orden," +
                "                   cf.referencia_campo_id AS cf_referencia_campo, cf.tabla_referencia AS cf_tabla_referencia," +
                "                   tc.id AS tc_id, tc.descripcion AS tc_descripcion," +
                "                   cs.id AS cs_id, cs.nombre AS cs_nombre, cs.referencia_seleccion_id  " +
                "           FROM    inspecciones_tipoformato tf" +
                "                       INNER JOIN inspecciones_moduloformato mf ON tf.id = mf.tipo_fotmato_id" +
                "                       INNER JOIN inspecciones_campoformato cf ON cf.modulo_formato_id = mf.id" +
                "                       INNER JOIN inspecciones_tipocampo tc ON cf.tipo_campo_id = tc.id" +
                "                       INNER JOIN inspecciones_camposeleccion cs ON cs.campo_formato_id = cf.id";
        try {
            conn = getDBConnection();
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMAnO
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return rs;
    }// Close getFormatServer

    /**
     * Get Format Type
     * @return
     */
    public ResultSet getInspeccionesTipoFormato(){

        ResultSet rs= null;
        String stsql = "	SELECT  tf.id AS tf_id, tf.nombre AS tf_nombre" +
                "           FROM    inspecciones_tipoformato tf ";//FALTAN LAS CONDICIONES
        try {
            conn = getDBConnection();
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return rs;
    }// getInspeccionesTipoFormato

    public ResultSet getModuloFormato(){

        ResultSet rs= null;
        String stsql = "	SELECT  mf.tipo_formato_id," +
                "                   mf.id AS mf_id, mf.nombre AS mf_nombre, mf.codigo AS mf_codigo, mf.numero_orden AS mf_numero_orden" +
                "           FROM    inspecciones_moduloformato mf ";
                //"           WHERE   mf.tipo_formato_id = " + tipoFormatoId;
        try {
            conn = getDBConnection();
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMAnO
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return rs;
    }//

    /**
     * GET TIPOCAMPO
     * @return
     */
    public ResultSet getTipoCampo(){

        ResultSet rs= null;
        String stsql = "	SELECT  tc.id AS tc_id, tc.descripcion AS tc_descripcion" +
                "           FROM    inspecciones_tipocampo tc" ;
        try {
            conn = getDBConnection();
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMAnO
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return rs;
    }//

    /**
     * Get CampoFormato
     * @return
     */
    public ResultSet getCampoFormato(){

        ResultSet rs= null;
        String stsql = "	SELECT  cf.modulo_formato_id, " +
                "                   cf.id AS cf_id, cf.nombre AS cf_nombre, cf.codigo AS cf_codigo, cf.numero_orden AS cf_numero_orden," +
                "                   cf.tabla_referencia AS cf_tabla_referencia, cf.tipo_campo_id, " +
                "                   cf.descripcion AS cf_descripcion, cf.parent_id AS cf_parent_id, cf.level AS cf_level" +
                "           FROM    inspecciones_tipoformato tf" +
                "                       INNER JOIN inspecciones_moduloformato mf ON tf.id = mf.tipo_formato_id" +
                "                       INNER JOIN inspecciones_campoformato cf ON cf.modulo_formato_id = mf.id";
                //"           WHERE   tf.id = " + tipoFormatoId;
        try {
            conn = getDBConnection();
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMAnO
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return rs;
    }//

}
