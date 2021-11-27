package com.example.facturacion;

public class parameterize {
    public parameterize(){

    }
    public String tipo_lectura(){
        return "INSERT INTO scm_tipos_lectura(" +
                        " tipo_lectura, d_tipo_lectura) " +
                        " VALUES "+
                        "('52','REACT.MAXIMA'),"+
                        "('50','REACT.MINIMA'),"+
                        "('41','ACT.MAXIMA'),"+
                        "('44','ACT.MEDIA'),"+
                        "('20','ACTIVA'),"+
                        "('30','ACT.MINIMA'),"+
                        "('42','REACTIVA'),"+
                        "('10','ACTIVA'),"+
                        "('51','REACT.MEDI');";

    }

//    public String scm_obsconsumo(){
//        return "INSERT INTO scm_obsconsumo(" +
//                "codobservacion, descripcion, solconsumo)" +
//                "VALUES "
//                +"('+','Cambio de medidor(Obs automati','CP1'),"
//                +"('-','SIN LECTURA (AUT',				'CLT'),"
//                +"('2','De otra ruta',					'CP1'),"
//                +"('3','Cambiar de sitio o posici?n de','CP1'),"
//                +"('4','medidor en mal estado',			'CP1'),"
//                +"('5','Tapa y/o medidor opaco con lec','CLT'),"
//                +"('6','Tapa y/o medidor  opaca sin le','CP1'),"
//                +"('8','Sin energia reactiva',			'CLT'),"
//                +"('9','tapa empa?ada',					'CP1'),"
//                +"('A','Cerrado - Medidor Interno',		'CP1'),"
//                +"('B','Predio Desocupado sin lectura',	'CMI'),"
//                +"('C','Predio Desocupado con Lectura',	'CLT'),"
//                +"('D','Directo desocupado',			'CMI'),"
//                +"('E','Directo Ocupado',				'CP1'),"
//                +"('H','Cambiar de sitio o posici?n el','CLT'),"
//                +"('J','Medidor en mal estado_con lect','CLT'),"
//                +"('K','Analisis Especial',				'CP1'),"
//                +"('L','Medidor diferente',				'CLT'),"
//                +"('M','Predio demolido',				'CMI'),"
//                +"('N','No se permiti? tomar lectura',	'CP1'),"
//                +"('O','Medidor retirado sin consumo',	'CMI'),"
//                +"('Q','Medidor frenado con energ?a',	'CP1'),"
//                +"('R','Con medidor y figura directo',	'CLT'),"
//                +"('S','Predio en construcci?n_con lec','CLT'),"
//                +"('T','Display apagado con energ?a',	'CLT'),"
//                +"('U','Display apagado sin energ?a',	'CLT'),"
//                +"('V','Autorreconectado',				'CLT'),"
//                +"('W','Contador Gira hacia atras',		'CP1'),"
//                +"('X','De otra ruta_con lectura',		'CLT'),"
//                +"('Y','Predio Suspendido',				'CLT'),"
//                +"('Z','No se ubica el predio',			'CP1');";
//    }
//
//    public String scm_causanolectura(){
//        return "INSERT INTO scm_causanolectura(" +
//                "codcausa, descripcion, solconsumo)" +
//                "VALUES "
//                +   "('+',  'Cambio de medidor(Obs automati'	,'CP1'),"
//                +   "('5',  'Tapa y/o medidor opaco con lec'	,'CLT'),"
//                +   "('C',  'Predio Desocupado con Lectura'		,'CLT'),"
//                +   "('H',  'Cambiar de sitio o posici?n el'	,'CLT'),"
//                +   "('K',  'Analisis Especial'					,'CP1'),"
//                +   "('O',  'Medidor retirado sin consumo'		,'CMI'),"
//                +   "('S',  'Predio en construcci?n_con lec'	,'CLT'),"
//                +   "('V',  'Autorreconectado'					,'CP1'),"
//                +   "('X',  'De otra ruta_con lectura'			,'CLT'),"
//                +   "('Y',  'Predio Suspendido'					,'CLT');";
//
//    }

    public String fac_grupos_lectura(){
        return "INSERT INTO fac_grupos_lectura(" +
                "idgrupolectura, idtipolectura, idtipolecturarelac)" +
                "VALUES"
                +"('A',	   	'10',  ''  ), "
                +"('B',	   	'52',  '41'), "
                +"('C',	   	'51',  '44'), "
                +"('GA',	'10',  ''  ), "
                +"('GA',	'30',  ''  ), "
                +"('GA',	'41',  ''  ), "
                +"('GA',	'44',  ''  ), "
                +"('GR',	'42',  '10'), "
                +"('GR',	'50',  '30'), "
                +"('GR',	'51',  '44'), "
                +"('GR',	'52',  '41'), "
                +"('H',	   	'44',  ''  ), "
                +"('M',	   	'30',  ''  ), "
                +"('O',	   	'50',  '30'), "
                +"('P',	   	'41',  ''  ), "
                +"('R',	   	'42',  '10'), "
                +"('Y',	   	'20',  ''  ); ";
    }

}
