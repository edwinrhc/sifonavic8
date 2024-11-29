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

//    Insert Persona
    void callSpTmpNuevosInsertarHerederosMasivo(@Param("new_herederosStr") String new_herederosStr);

    void callSpInsertNuevosHerederos();

//    Fin Persona

//  Actualizar  Fecha Fallecida
    void callSpUpdateFechaEstadoFallecidos(
            @Param("numDocumento") String numDocumento,
            @Param("fechaFallecido") String fechaFallecido,
            @Param("usuarioSesion") String usuarioSesion);

    boolean existsByNumeroDocumento(
            @Param("tipoDocumento") String tipodocumento,
            @Param("numDocumento") String numDocumento);

    void callSpUpdatePersonaValidaMasiva(@Param("personasStr") String personasStr);
    void callSpProcedureVerificaHeredero();

//  Fin Actualizar  Fecha Fallecida

    void callSpTmpInsertarHerederosMasivo(@Param("herederosStr") String herederosStr);

    void callSpActualizaHerederosReniec(@Param("usuarioSesion") String usuarioSesion);

    void callSpActualizaHerederosGPEC_Persona();



}
