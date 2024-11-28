package org.st.gob.pe.sifonavic8.common;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UniqueCodeGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    // Generamos el código númerico único de 8 dígitos
    private String generateNumericoCode(int length){
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i ++){
            code.append(RANDOM.nextInt(10)); // 0-9
        }
        return code.toString();
    }
    // Obtenemos la fecha actual en formato ddMMyy
    private String getCurrentDateFormated(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");
        return dateFormat.format(new Date());
    }

    // Método principal para generar el código con el formato CAS-xxxxx-ddmmyy
    public String generateUniqueCode(){
        String numericCode = generateNumericoCode(8);
        String currentDate = getCurrentDateFormated();
        return "CAS-"+numericCode+"-"+currentDate;
    }

}
