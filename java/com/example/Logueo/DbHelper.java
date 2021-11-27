package com.example.Logueo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.facturacion.parameterize;

public class DbHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME= "pereira_energia_fac_lec.sqlite";
	private static final int DB_SCHEME_VERSION = 1;
	Context contextofile;
	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_SCHEME_VERSION);
		contextofile = context;
		// TODO Auto-generated constructor stub

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL(DataBaseManager.CREATE_TABLE_TERMINAL);
		db.execSQL(DataBaseManager.CREATE_TABLE_ORDEN);		
		db.execSQL(DataBaseManager.CREATE_TABLE_USUARIO);		
		db.execSQL(DataBaseManager.CREATE_TABLE_STOCK); 		
		db.execSQL(DataBaseManager.CREATE_TABLE_MATERIAL_INSTALADO); 		
		db.execSQL(DataBaseManager.CREATE_TABLE_OBSERVACION);		
		db.execSQL(DataBaseManager.CREATE_TABLE_EJECUCION);		
		db.execSQL(DataBaseManager.CREATE_TABLE_FOTO);		
		db.execSQL(DataBaseManager.CREATE_TABLE_GPS);
		db.execSQL(DataBaseManager.CREATE_TABLE_ASISTENCIA);
		db.execSQL(DataBaseManager.CREATE_TABLE_SUPERVISOR_LECTOR);

		db.execSQL(DataBaseManager.CREATE_TABLE_INSPECCIONES_TIPOFORMATO);
		db.execSQL(DataBaseManager.CREATE_TABLE_INSPECCIONES_MODULOFORMATO);
		db.execSQL(DataBaseManager.CREATE_TABLE_INSPECCIONES_TIPOCAMPO);
		db.execSQL(DataBaseManager.CREATE_TABLE_INSPECCIONES_CAMPOFORMATO);

		db.execSQL(DataBaseManager.CREATE_TABLE_INSPECCIONES_EJECUCIONFORMATO);
		db.execSQL(DataBaseManager.CREATE_TABLE_INSPECCIONES_REGISTROEJECUCION);
		db.execSQL(DataBaseManager.CREATE_TABLE_INSPECCIONES_RESPUESTAABIERTA);
		db.execSQL(DataBaseManager.CREATE_TABLE_INSPECCIONES_RESPUESTAFECHA);
		db.execSQL(DataBaseManager.CREATE_TABLE_INSPECCIONES_RESPUESTANUMERICA);

		db.execSQL(DataBaseManager.CREATE_TABLE_MEDIDOR_ENCONTRADO);

		// FACTURACION EN SITIO
//		System.out.print(DataBaseManager.CREATE_TABLE_SCM_CAUSANOLECTURA);
		db.execSQL(DataBaseManager.CREATE_TABLE_SCM_CAUSANOLECTURA);
//		System.out.print(DataBaseManager.CREATE_TABLE_SCM_OBSERVACIONADICIONAL);
		db.execSQL(DataBaseManager.CREATE_TABLE_SCM_OBSERVACIONADICIONAL);
//		System.out.print(DataBaseManager.CREATE_TABLE_SCM_OBSCONSUMO);
		db.execSQL(DataBaseManager.CREATE_TABLE_SCM_OBSCONSUMO);
//		System.out.print(DataBaseManager.CREATE_TABLE_SCM_TIPOS_LECTURA);
		db.execSQL(DataBaseManager.CREATE_TABLE_SCM_TIPOS_LECTURA);
//		System.out.print(DataBaseManager.CREATE_TABLE_SCM_ACCION_ANEXOS);
		db.execSQL(DataBaseManager.CREATE_TABLE_SCM_ACCION_ANEXOS);
//		System.out.print(DataBaseManager.CREATE_TABLE_SCM_ANEXOS);
		db.execSQL(DataBaseManager.CREATE_TABLE_SCM_ANEXOS);
//		System.out.print(DataBaseManager.CREATE_TABLE_SCM_ELEMENTOS_LECTURAS_DES);
		db.execSQL(DataBaseManager.CREATE_TABLE_SCM_ELEMENTOS_LECTURAS_DES);
//		System.out.print(DataBaseManager.CREATE_TABLE_SCM_CLI_CONCEPTOS_DES);
		db.execSQL(DataBaseManager.CREATE_TABLE_SCM_CLI_CONCEPTOS_DES);
//		System.out.print(DataBaseManager.CREATE_TABLE_SCM_MULTITABLA);
		db.execSQL(DataBaseManager.CREATE_TABLE_SCM_MULTITABLA);
//		System.out.print(DataBaseManager.CREATE_TABLE_DETALLE_ORDEN_FACTURACION);
		db.execSQL(DataBaseManager.CREATE_TABLE_DETALLE_ORDEN_FACTURACION);
//		System.out.print(DataBaseManager.CREATE_TABLE_FAC_IMPRESION);
		db.execSQL(DataBaseManager.CREATE_TABLE_FAC_IMPRESION);
//		System.out.print(DataBaseManager.CREATE_TABLE_SCM_ELEMENTOS_LECTURA);
		db.execSQL(DataBaseManager.CREATE_TABLE_SCM_ELEMENTOS_LECTURA);
//		System.out.print(DataBaseManager.CREATE_TABLE_FAC_LABORCONCEPTOS);
		db.execSQL(DataBaseManager.CREATE_TABLE_FAC_LABORCONCEPTOS);
//		System.out.print(DataBaseManager.CREATE_TABLE_FAC_RANGOS);
		db.execSQL(DataBaseManager.CREATE_TABLE_FAC_RANGOS);
//		System.out.print(DataBaseManager.CREATE_TABLE_SCM_ORDENES_TRABAJO);
		db.execSQL(DataBaseManager.CREATE_TABLE_SCM_ORDENES_TRABAJO);
//		System.out.print(DataBaseManager.CREATE_TABLE_SFAC_GRUPOS_LECTURA);
		db.execSQL(DataBaseManager.CREATE_TABLE_SFAC_GRUPOS_LECTURA);


		db.execSQL("CREATE INDEX idx_respuesta_abierta ON inspecciones_respuestaabierta(ejecucion_formato_id)");
		db.execSQL("CREATE INDEX idx_respuesta_numerica ON inspecciones_respuestanumerica(ejecucion_formato_id);");
		db.execSQL("CREATE INDEX idx_registro_ejecucion ON inspecciones_registroejecucion(campo_formato_id)");
		db.execSQL("CREATE INDEX idx_campo_formato ON  inspecciones_campoformato(parent_id)");

        parameterize param = new  parameterize();
        db.execSQL(param.fac_grupos_lectura());
        //db.execSQL(param.scm_causanolectura());
        //db.execSQL(param.scm_obsconsumo());
        db.execSQL(param.tipo_lectura());

		//System.out.println(param.scm_causanolectura());
		//System.out.println(param.scm_obsconsumo());
		System.out.println(param.fac_grupos_lectura());
		System.out.println(param.tipo_lectura());

		System.out.println("CREATE_TABLE_INSPECCIONES_EJECUCIONFORMATO " + DataBaseManager.CREATE_TABLE_INSPECCIONES_EJECUCIONFORMATO);
		System.out.println("CREATE_TABLE_INSPECCIONES_REGISTROEJECUCION " + DataBaseManager.CREATE_TABLE_INSPECCIONES_REGISTROEJECUCION);
		System.out.println("CREATE_TABLE_INSPECCIONES_RESPUESTAABIERTA " + DataBaseManager.CREATE_TABLE_INSPECCIONES_RESPUESTAABIERTA);
		System.out.println("CREATE_TABLE_INSPECCIONES_RESPUESTAFECHA " + DataBaseManager.CREATE_TABLE_INSPECCIONES_RESPUESTAFECHA);
		System.out.println("CREATE_TABLE_INSPECCIONES_RESPUESTANUMERICA " + DataBaseManager.CREATE_TABLE_INSPECCIONES_RESPUESTANUMERICA);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}