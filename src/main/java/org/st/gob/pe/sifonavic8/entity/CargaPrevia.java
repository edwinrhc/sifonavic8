package org.st.gob.pe.sifonavic8.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CargaPrevia {

    private String tipoDocumento;
    private String numDocumento;
    private String apePaterno;
    private String apeMaterno;
    private String nombres;

    private Date fechaNacimiento;
    private String sexo;
    private Date fechaFallecido;
    private String estadoFallecido;
}
