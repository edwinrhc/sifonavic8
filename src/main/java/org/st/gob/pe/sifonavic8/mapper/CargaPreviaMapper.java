package org.st.gob.pe.sifonavic8.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.st.gob.pe.sifonavic8.entity.CargaPrevia;

import java.util.List;

@Mapper
public interface CargaPreviaMapper {

    List<CargaPrevia> cargaPreviaConsultaPersona(
            @Param("tipoDocumento") String stipoDoc,
            @Param("numDocumento") String snroDoc);

    void callSpUpdateFechaEstadoFallecidos(
            @Param("numDocumento") String numDocumento,
            @Param("fechaFallecido") String fechaFallecido,
            @Param("usuarioSesion") String usuarioSesion);

    boolean existsByNumeroDocumento(
            @Param("tipoDocumento") String tipodocumento,
            @Param("numDocumento") String numDocumento);

    void callSpTmpNuevosInsertarHerederosMasivo(@Param("new_herederosStr") String new_herederosStr);

    void callSpInsertNuevosHerederos();

}
