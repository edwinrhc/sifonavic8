package org.st.gob.pe.sifonavic8.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class Constantes {

    public static final String FORMATO_FECHA = "dd/MM/yyy";

    public static final String SOMETHING_WENT_WRONG = "Something Went Wrong.";

    public static final String INVALID_DATA = "Invalid Data.";

    public static final String UNAUTHORIZED_ACCESS="Unauthorized access.";
    public static final String VERSION = "v 1.0.0";


    public static SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat(FORMATO_FECHA);
    }

    public static Date parseDate(String dateString) throws ParseException {
        if (dateString != null && !dateString.isEmpty()) {
            return getSimpleDateFormat().parse(dateString);
        } else {
            // Manejar el caso en el que dateString es null o vacío
            return null; // o lanzar una excepción, dependiendo de tus requisitos
        }
    }

    public static final Logger logger = Logger.getLogger(Constantes.class.getName());


    public static String convertDateFormat(String dateStr) {
        List<SimpleDateFormat> knownFormats = Arrays.asList(
                new SimpleDateFormat("yyyy-MM-dd"), // Formato ISO
                new SimpleDateFormat("dd/MM/yyyy")  // Formato común en algunas regiones
        );

        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (SimpleDateFormat format : knownFormats) {
            try {
                Date date = format.parse(dateStr);
                return targetFormat.format(date); // Devuelve la fecha en el nuevo formato si el parseo es exitoso
            } catch (ParseException e) {

            }
        }

        // Si ningún formato fue correcto, maneja el error
        System.err.println("Fecha no parseable: " + dateStr);
        return null; // O maneja esto de una manera que se ajuste mejor a tu aplicación
    }

}
