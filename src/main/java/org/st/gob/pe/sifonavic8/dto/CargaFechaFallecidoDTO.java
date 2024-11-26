package org.st.gob.pe.sifonavic8.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CargaFechaFallecidoDTO {

	private String tipDocumento;
	private String docTitular;
    private String fechaFallecido;
	private String usuarioSesion;



	// Mótodo para verificar si la lista está vacía
    public boolean isEmpty() {
        return tipDocumento == null || tipDocumento.isEmpty() || docTitular == null || docTitular.isEmpty() || fechaFallecido == null || fechaFallecido.isEmpty();
    }

	public String getFormattedFechaFallecido() {
		// Suponiendo que fechaFallecido está en formato LocalDate
		LocalDate fecha = LocalDate.parse(fechaFallecido);
		return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}
    
    
    

}
