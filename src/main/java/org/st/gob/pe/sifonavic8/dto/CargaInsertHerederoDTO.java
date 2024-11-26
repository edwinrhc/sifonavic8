package org.st.gob.pe.sifonavic8.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CargaInsertHerederoDTO {

    private String tipDocumento;
    private String docHeredero;

    private String nombre;
    private String apPaterno;
    private String apMaterno;

    private String fechaNacimiento;

}
