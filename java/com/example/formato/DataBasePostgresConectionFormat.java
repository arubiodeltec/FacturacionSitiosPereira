package com.example.formato;

/**
 * Created by DELTEC on 11/09/2015.
 */
public class DataBasePostgresConectionFormat {

    private static final String DBNAME = "formatos_inspecciones";
    public String USER = "postgres";
    public static final String DBDRIVER = "org.postgresql.Driver";
    //public static final String PASSWORD = "dannypostgres10";
    public static final String PASSWORD = "postgres";
    public static final String SERVER = "192.168.0.157";
    public static final String PORT = "5432";
    public static final String conexion = "jdbc:postgresql://"+SERVER+":"+PORT+"/"+DBNAME;

    public DataBasePostgresConectionFormat(int tipoUsuario){

        if(tipoUsuario == 2)
            this.USER = "pda_supervisor";
    } //Cierre Constructor

    public String getDbName(){
        return this.DBNAME;
    }

    public String getUserName(){
        return this.USER;
    }

    public String getPassword(){
        return this.PASSWORD;
    }

    public String getServer(){
        return this.SERVER;
    }

    public String getPort(){
        return this.PORT;
    }

    public String getConection(){
        return this.conexion;
    }

    public String getDbDriver(){return this.DBDRIVER;}
}
