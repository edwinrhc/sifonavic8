package org.st.gob.pe.sifonavic8.service;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.st.gob.pe.sifonavic8.dto.CargaFechaFallecidoDTO;
import org.st.gob.pe.sifonavic8.dto.CargaHerederosPersonaDTO;
import org.st.gob.pe.sifonavic8.dto.CargaInsertHerederoDTO;
import org.st.gob.pe.sifonavic8.entity.CargaPrevia;
import org.st.gob.pe.sifonavic8.mapper.CargaPreviaMapper;

import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CargaPreviaService {

    @Autowired
   CargaPreviaMapper cargaPreviaMapper;

    private static final Logger logger = LoggerFactory.getLogger(CargaPreviaService.class);


    public List<CargaPrevia> consultaPersona(String tipoDocumento, String numDocumento) {
        return cargaPreviaMapper.cargaPreviaConsultaPersona(tipoDocumento, numDocumento);
    }

    @Transactional
    public void procesarCargaInsertarHerederosMasiva(List<CargaInsertHerederoDTO> datos) {
        try {
            String datosStr = datos.stream()
                    .map(dto -> String.join(",", dto.getTipDocumento(), dto.getDocHeredero(), dto.getNombre(), dto.getApPaterno(), dto.getApMaterno(),dto.getFechaNacimiento()))
                    .collect(Collectors.joining("|"));
            cargaPreviaMapper.callSpTmpNuevosInsertarHerederosMasivo(datosStr);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la carga y insertar Heredero", e);
        }
    }

    @Transactional
    public void procesarInsertarHerederos(){
        try{
            cargaPreviaMapper.callSpInsertNuevosHerederos();
        }catch (Exception e){
            throw  new RuntimeException("Error al procesar la carga y insertar Heredero",e);
        }
    }

    @Transactional
    public void procesarFechaFallecidoCargarActualizar(List<CargaFechaFallecidoDTO> documentos){
        try{
            for (CargaFechaFallecidoDTO documento : documentos) {
                cargaPreviaMapper.callSpUpdateFechaEstadoFallecidos(documento.getDocTitular(), documento.getFechaFallecido(),documento.getUsuarioSesion());
            }
        }catch (Exception e){
            throw new RuntimeException("Error al procesar la carga y actualizar",e);
        }
    }

    @Transactional
    public void procesarCargaActualizaPersonaValidaMasiva(List<CargaFechaFallecidoDTO> datos) {
        try {
            String datosStr = datos.stream()
                    .map(dto -> String.join(",", dto.getTipDocumento(), dto.getDocTitular()))
                    .collect(Collectors.joining("|"));
            cargaPreviaMapper.callSpUpdatePersonaValidaMasiva(datosStr);
        } catch (PersistenceException e) {
            // Obtener la causa original de la excepción
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                SQLException sqlEx = (SQLException) cause;
                // Manejar errores SQL específicos
                logger.error("Error en la ejecución del SP: Código SQL: {}, Mensaje: {}", sqlEx.getErrorCode(), sqlEx.getMessage());
            } else {
                logger.error("Error en MyBatis: {}", e.getMessage());
            }
            throw new RuntimeException("Error al actualizar personas válidas masivamente.", e);
        }
    }

    @Transactional
    public void procesarProcedureVerificaHeredero(){
        try{
            cargaPreviaMapper.callSpProcedureVerificaHeredero();
        }catch (Exception e){
            throw  new RuntimeException("Error al procesar la carga y actualizar",e);
        }
    }

    @Transactional
    public void procesarCargaActualizaHerederosMasiva(List<CargaHerederosPersonaDTO> datos) {
        try {
            String datosStr = datos.stream()
                    .map(dto -> String.join(",", dto.getTipDocumento(), dto.getDocHeredero(), dto.getNombre(), dto.getApPaterno(), dto.getApMaterno()))
                    .collect(Collectors.joining("|"));
            cargaPreviaMapper.callSpTmpInsertarHerederosMasivo(datosStr);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la carga y actualizar", e);
        }
    }

    @Transactional
    public void procesarActualizaHerederosReniec(String usuarioSesion){
        try{
            cargaPreviaMapper.callSpActualizaHerederosReniec(usuarioSesion);
        }catch (Exception e){
            throw  new RuntimeException("Error al procesar la carga y actualizar Reniec",e);
        }
    }


    @Transactional
    public void procesarActualizaHerederosGPEC_Persona(){
        try{
            cargaPreviaMapper.callSpActualizaHerederosGPEC_Persona();
        }catch (Exception e){
            throw  new RuntimeException("Error al procesar la carga y actualizar GPEC_Persona",e);
        }
    }
}
