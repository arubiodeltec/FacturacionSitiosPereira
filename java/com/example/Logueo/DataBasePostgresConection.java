package com.example.Logueo;

/**
 * Created by DELTEC on 19/08/2015.
 */
public class DataBasePostgresConection {
    private static final String DBNAME = "pereira_energia";
    public String USER = "pda";
    public static final String PASSWORD = "r00td3lt3c8001661991";
    //public String USER = "postgres";
    //public static final String PASSWORD = "r00td3lt3c8001661991";
    public static final String DBDRIVER = "org.postgresql.Driver";
    public static final String SERVER = "pereira.sysdeltec.com";
    public static final String PORT = "5462";
    public static final String conexion = "jdbc:postgresql://"+SERVER+":"+PORT+"/"+DBNAME;

    public DataBasePostgresConection(int tipoUsuario){

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
