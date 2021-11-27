package com.example.gestionOrdenes;

import com.example.Logueo.DataBasePostgresConection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * Created by DELTEC on 19/08/2015.
 */
public class EjecucionOrdenesModelServer {

    Connection conn;
    DataBasePostgresConection c;

    /**
     * Constructor Manejador de Bd SERVER
     */
    public EjecucionOrdenesModelServer(int tipoUsuario){

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
            //dbConnection = DriverManager.getConnection(c.getConection(),c.getUserName(),c.getPassword());

            Properties props = new Properties();
            props.setProperty("user",c.getUserName());
            props.setProperty("password",c.getPassword());
            props.setProperty("allowEncodingChanges","false");

            dbConnection= DriverManager.getConnection(c.getConection(), props);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return dbConnection;
    }

    /**
     * Ejecuta insert
     * @param arg_lecturas
     * @param cantidad_lecturas
     * @param tipoUsuario
     * @return
     */
    public int ejecutar_consulta(String arg_lecturas, int cantidad_lecturas, int tipoUsuario){
        int aff = 0;
        String stsql = "SELECT InsertUpdateLectura4('" + arg_lecturas + "'," + cantidad_lecturas + ")";

        if(tipoUsuario == 2)
            stsql = "SELECT insertupdatelecturasupervisor('" + arg_lecturas + "'," + cantidad_lecturas + ")";
        else if(tipoUsuario == 3)
            stsql = "SELECT insertupdatelecturarevisor_pereira('" + arg_lecturas + "'," + cantidad_lecturas + ")";

        Statement st=null;
        ResultSet rs= null;

        try {
            conn = getDBConnection();
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMA?O
            rs = st.executeQuery(stsql);
            conn.close();

            while(rs.next()) {
                aff = rs.getInt(1);
            }
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return aff;
    }

    public boolean insertarFoto(int ose_codigo, String fecha, String foto_url ){
        String stsqlInsert = "";
        //Statement st=null;
        int aff = 0;
        boolean ejecuto = false;
		/*DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = (Date)formatter.parse(fecha);
		System.out.println("Today is " +date.getTime());*/

        Timestamp fechaFoto = Timestamp.valueOf(fecha);

        stsqlInsert = "INSERT INTO fotos_lme_temp (ftl_ose_codigo, ftl_foto, ftl_fecha," +
                "ftl_usuario, ftl_fecha_actualizacion, ftl_usuario_actualizacion)" +
                "VALUES (?,?,?,?,now(),'pda')";

        File file = new File(foto_url);
        FileInputStream fis = null;

        try {
            conn = getDBConnection();
            conn.setAutoCommit(false);

            fis = new FileInputStream(file);

            PreparedStatement ps = conn.prepareStatement(stsqlInsert);
            ps.setInt(1, ose_codigo);
            ps.setBinaryStream(2, fis, (int)file.length());
            ps.setTimestamp(3, fechaFoto);
            ps.setString(4, "pda");
            //ps.setTimestamp(5, fechaFoto);
            //ps.setString(6, "pda");
            aff = ps.executeUpdate();
            ps.close();
            fis.close();

            conn.commit();
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(aff == 1)
            ejecuto = true;

        return ejecuto;
    }

    /**
     * Consulta el trabajo a revisar por el lector
     * @param  code Codigo del usuario
     */
    public ResultSet getReadCheckJob(String code){

        Statement st=null;
        ResultSet rs= null;
        String stsql = "SELECT DISTINCT	o.ose_codigo, o.ose_precarga, o.ose_tip_codigo, c.cli_contrato, c.cli_nombre, ci.cic_descripcion AS ciu_nombre, o.ose_direccion AS direccion1, " +
                "		c.cli_direccion As direccion2, t.tip_nombre as producto, o.ose_ciclo as ciclo, c.cli_direccion_descriptiva AS barrio, " +
                "		t1.tip_nombre as consumo, (m.ome_prefijo || ' ' || o.ose_elemento)::text as elemento, o.ose_lectura_anterior as lectura_anterior,	" +
                "		o.ose_consumo_promedio as consumo_promedio, o.ose_lectura_actual as lectura_actual, o.ose_tope_medicion as cantidad_digitos, " +
                "		o.ose_tipo_producto,o.ose_ruta, o.ose_ruta_consecutivo " +
                "	FROM orden_servicio_temp o  " +
                "		INNER JOIN lectura_medidor_historico p ON  o.ose_codigo = p.lmh_ose_codigo" +
                "		INNER JOIN  cliente_orden_temp c ON c.ose_codigo = o.ose_codigo" +
                "       INNER JOIN orden_medidor_temp m ON o.ose_codigo = m.ose_codigo"  +
                "		INNER JOIN tipo t ON t.tip_codigo=o.ose_tipo_producto  " +
                "		INNER JOIN tipo t1 ON t1.tip_codigo=o.ose_tipo_consumo " +
                "		INNER JOIN ciclo ci ON ci.cic_codigo=o.ose_ciclo  " +
                "	WHERE   o.ose_est_codigo = 27 AND p.lmh_est_lectura = 30 AND p.lmh_recurso_humano = " + code;

        try {
            conn = getDBConnection();
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMA?O
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return rs;
    }// Cierre Sincronizacion de Ordenes

    public String getOrdenesAnuladas(int per_codigo, String ordenes){
        String ordenesAnuladas = "";
        boolean primeraVez = true;
        String stsql = "SELECT o.ose_codigo FROM orden_servicio_temp o, programacion p  " +
                "WHERE  o.ose_prog_codigo = p.prog_codigo  " +
                "AND o.ose_est_codigo=32 " +
                "AND  p.prog_recurso_humano=" + per_codigo + " AND o.ose_codigo IN ("+ ordenes +")";

        Statement st=null;
        ResultSet rs= null;

        try {
            conn = getDBConnection();
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMA?O
            rs = st.executeQuery(stsql);
            conn.close();

            if(rs.first()){
                do{
                    if(primeraVez){
                        primeraVez = false;
                    }else ordenesAnuladas += ",";

                    ordenesAnuladas += "" + rs.getInt(1);

                }while(rs.next());
            }
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return ordenesAnuladas;
    }

    /**
     * Retorna las ordenes agregadas al supervisor para Leer
     * @param per_codigo
     * @param ordenes
     * @return
     */
    public ResultSet getOrdenesAgregadas(String per_codigo, String ordenes){
        String stsql = "";
        Statement st=null;
        ResultSet rs= null;

        stsql = "	SELECT DISTINCT o.ose_codigo, o.ose_precarga, o.ose_tip_codigo, c.cli_contrato, ENCODE(CONVERT_TO(c.cli_nombre, 'SQL_ASCII'), 'ESCAPE') AS cli_nombre, " +
                "		ci.cic_descripcion AS ciu_nombre, o.ose_direccion AS direccion1, c.cli_direccion As direccion2, " +
                "		t.tip_nombre as producto, o.ose_ciclo as ciclo, c.cli_direccion_descriptiva AS barrio, " +
                "		t1.tip_nombre as consumo, (m.ome_prefijo || ' ' || o.ose_elemento)::text as elemento, o.ose_lectura_anterior as lectura_anterior, " +
                "		o.ose_consumo_promedio as consumo_promedio, o.ose_lectura_actual as lectura_actual, o.ose_tope_medicion as cantidad_digitos, " +
                "       o.ose_tipo_producto,o.ose_ruta, o.ose_ruta_consecutivo " +
                "	FROM orden_servicio_temp o  " +
                "		INNER JOIN programacion_supervisor p ON  o.ose_codigo = p.prs_ose_codigo" +
                "		INNER JOIN  cliente_orden_temp c ON c.ose_codigo = o.ose_codigo" +
                "       INNER JOIN orden_medidor_temp m ON o.ose_codigo = m.ose_codigo" +
                "		INNER JOIN tipo t ON t.tip_codigo=o.ose_tipo_producto  " +
                "		INNER JOIN tipo t1 ON t1.tip_codigo=o.ose_tipo_consumo " +
                "		INNER JOIN ciclo ci ON ci.cic_codigo=o.ose_ciclo  " +
                "	WHERE  o.ose_est_codigo=27 AND p.prs_est_codigo = 30 AND p.prs_registro_vigente = TRUE AND p.prs_recurso_humano= " + per_codigo ;

        //if (ordenes.length() > 0)
        //    stsql += " AND o.ose_codigo NOT IN (" + ordenes + ")";

        try {
            conn = getDBConnection();
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); //CONSULTAR TAMANO
            rs = st.executeQuery(stsql);
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        return rs;
    }// Cierre getOrdenesAgregadas


    public boolean insertar_medidor(int med_ciclo,int med_ruta,int med_cons_ruta_anterior, int med_cons_ruta_posterior,
                                    String med_direccion,String med_medidor, int med_indicador_med,String med_lectura,
                                    int med_servicio,String med_observacion, int med_persona,String med_latitud,
                                    String med_longitud,String med_fecha_in ) {
        String stsqlInsert = "";
        //Statement st=null;
        int aff = 0;
        boolean ejecuto = false;

        Timestamp med_fecha = Timestamp.valueOf(med_fecha_in);

        stsqlInsert = "INSERT INTO medidor_encontrado_lectura (med_ciclo, med_ruta, " +
                "med_cons_ruta_anterior, med_cons_ruta_posterior, med_direccion, " +
                "med_indicador_med,med_lectura,med_servicio,med_observacion," +
                "med_persona,med_latitud,med_longitud,med_fecha,med_medidor)" +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            conn = getDBConnection();
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(stsqlInsert);
            ps.setInt(1, med_ciclo);
            ps.setInt(2, med_ruta);
            ps.setInt(3, med_cons_ruta_anterior);
            ps.setInt(4, med_cons_ruta_posterior);
            ps.setString(5, med_direccion);
            ps.setInt(6, med_indicador_med);
            ps.setString(7, med_lectura);
            ps.setInt(8, med_servicio);
            ps.setString(9, med_observacion);
            ps.setInt(10, med_persona);
            ps.setString(11, med_latitud);
            ps.setString(12, med_longitud);
            ps.setTimestamp(13, med_fecha);
            ps.setString(14, med_medidor);


            aff = ps.executeUpdate();
            ps.close();


            conn.commit();
            conn.close();
        } catch (SQLException se) {
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        }

        if(aff == 1)
            ejecuto = true;

        return ejecuto;
    }
}
