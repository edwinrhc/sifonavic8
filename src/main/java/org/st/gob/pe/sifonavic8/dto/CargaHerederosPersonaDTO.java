package org.st.gob.pe.sifonavic8.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CargaHerederosPersonaDTO {

    private String tipDocumento;
    private String docHeredero;

    private String nombre;
    private String apPaterno;
    private String apMaterno;


}
