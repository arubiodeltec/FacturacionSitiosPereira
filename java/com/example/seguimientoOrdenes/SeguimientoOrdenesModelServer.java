package com.example.seguimientoOrdenes;

import com.example.Logueo.DataBasePostgresConection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELTEC on 19/08/2015.
 */
public class SeguimientoOrdenesModelServer {
    Connection conn;
    DataBasePostgresConection c;

    /**
     * Constructor Manejador de Bd SERVER
     */
    public SeguimientoOrdenesModelServer(int tipoUsuario){

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
     * Consulta Seguimiento al supervisor
     * @param supervisor codigo del supervisor
     * @param ciclo
     * @param orden
     * @param row_consult
     * @return
     */
    public ResultSet get_seguimiento_supervisor(String supervisor, String ciclo, String orden, int row_consult){

        Statement st=null;
        ResultSet rs= null;

        String stsql = "	 SELECT     per.per_codigo, per.per_nombre|| ' ' ||per.per_apellido1 as nombre, " +
                "				count(o.ose_codigo) AS total, " +
                "				coalesce(sum( case when o.ose_est_codigo = 27 then 1 else 0 end),0) AS ejecutada, " +
                "				coalesce(sum( case when o.ose_est_codigo = 30 then 1 else 0 end),0) AS pendiente, " +
                "				coalesce(sum( case when lec.lme_codigo_observacion_no_lectura > 0 then 1 else 0 end),0) AS total_causas, " +
                //"				coalesce(sum( case when lec.lme_codigo_observacion_no_lectura = 1008 then 1 else 0 end),0) AS med_no_existe, " +
                "				coalesce(sum( case when lec.lme_codigo_observacion_no_lectura = 763 AND lec.lme_codigo_observacion_lectura = 765 then 1 else 0 end),0) AS impedimento_tapado, " +	//alto_consumo
                "				coalesce(sum( case when lec.lme_codigo_observacion_no_lectura = 763 AND lec.lme_codigo_observacion_lectura = 778 then 1 else 0 end),0) AS impedimento_reja, " +     //otros
                "				coalesce(sum( case when lec.lme_codigo_observacion_no_lectura = 763 AND lec.lme_codigo_observacion_lectura = 779 then 1 else 0 end),0) AS impedimento_cambio, " +   //sin acceso
                "	case when  coalesce(sum( case when o.ose_est_codigo = 27 then 1 else 0 end),0) > 0 then min(o.ose_fecha_ejecucion) else NULL end as fecha_inicio, " +
                "	case when  coalesce(sum( case when o.ose_est_codigo = 27 then 1 else 0 end),0) > 0 then max(o.ose_fecha_ejecucion) else NULL end as fecha_fin " +
                "	FROM    orden_servicio_temp as o " +
                "	INNER JOIN programacion AS pro on (o.ose_prog_codigo = pro.prog_codigo AND o.ose_ciclo = " + ciclo + ") " +
                "	INNER JOIN persona AS per on pro.prog_recurso_humano = per.per_codigo " +
                "	INNER JOIN supervisor_lector AS sup ON sup.sup_lect_per_codigo = per.per_codigo " +
                "	LEFT join lectura_medidor_temp as lec on o.ose_codigo = lec.lme_ose_codigo" +
                "	WHERE  sup.sup_super_per_codigo = " + supervisor +
                "	AND sup.sup_vigente AND o.ose_ciclo = " + ciclo +
                "	AND o.ose_fecha_creacion >= now() - INTERVAL '27 days'" +
                "	GROUP BY 1,2" +
                "	ORDER BY " + row_consult + " " + orden ;

        try {
            conn = getDBConnection();
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return rs;
    }// Cierre Sincronizacion de Ordenes


    /**
     * Consulta Seguimiento al supervisor
     * @param supervisor codigo del supervisor
     * @param ciclo
     * @param orden
     * @param row_consult
     * @return
     */
    public ResultSet get_seguimiento_lectores(String supervisor, String ciclo, String orden, int row_consult, String rutas){

        Statement st=null;
        ResultSet rs= null;

        String stsql =
                "	 SELECT     per.per_codigo, per.per_nombre|| ' ' ||per.per_apellido1 as nombre, " +
                "				count(o.ose_codigo) AS total, " +
                "				coalesce(sum( case when o.ose_est_codigo = 27 then 1 else 0 end),0) AS ejecutada, " +
                "				coalesce(sum( case when o.ose_est_codigo = 30 then 1 else 0 end),0) AS pendiente, " +
                "				coalesce(sum( case when lec.lme_codigo_observacion_no_lectura > 0 then 1 else 0 end),0) AS total_causas, " +
                "				coalesce(sum( case when lec.lme_codigo_observacion_no_lectura = 763 AND lec.lme_codigo_observacion_lectura = 765 then 1 else 0 end),0) AS impedimento_tapado, " +	//alto_consumo
                "				coalesce(sum( case when lec.lme_codigo_observacion_no_lectura = 763 AND lec.lme_codigo_observacion_lectura = 778 then 1 else 0 end),0) AS impedimento_reja, " +     //otros
                "				coalesce(sum( case when lec.lme_codigo_observacion_no_lectura = 763 AND lec.lme_codigo_observacion_lectura = 779 then 1 else 0 end),0) AS impedimento_cambio, " +   //sin acceso
                "	case when  coalesce(sum( case when o.ose_est_codigo = 27 then 1 else 0 end),0) > 0 then min(o.ose_fecha_ejecucion) else NULL end as fecha_inicio, " +
                "	case when  coalesce(sum( case when o.ose_est_codigo = 27 then 1 else 0 end),0) > 0 then max(o.ose_fecha_ejecucion) else NULL end as fecha_fin " +
                "	FROM    orden_servicio_temp as o " +
                "	INNER JOIN programacion AS pro on (o.ose_prog_codigo = pro.prog_codigo AND o.ose_ciclo = " + ciclo + ") " +
                "	INNER JOIN persona AS per on pro.prog_recurso_humano = per.per_codigo " +
                "	LEFT join lectura_medidor_temp as lec on o.ose_codigo = lec.lme_ose_codigo" +
                "	WHERE  o.ose_ruta IN ("+rutas+") " +
                "   AND  o.ose_ciclo = " + ciclo +
                "	AND o.ose_fecha_creacion >= now() - INTERVAL '27 days'" +
                "	GROUP BY 1,2" +
                "	ORDER BY " + row_consult + " " + orden ;

        try {
            conn = getDBConnection();
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return rs;
    }// Cierre Sincronizacion de Ordenes


    public List<String>  get_rutas_ciclo(String ciclo){

        Statement st=null;
        ResultSet rs= null;
        List<String> labels = new ArrayList<String>();

        String stsql =
                "	SELECT o.ose_ruta FROM orden_servicio_temp o " +
                        "WHERE o.ose_ciclo = "+ ciclo +" GROUP BY o.ose_ruta ORDER BY o.ose_ruta";
        try {
            conn = getDBConnection();
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        try {
            rs.beforeFirst();
            while (rs.next()) {
                labels.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return labels;
    }

}
