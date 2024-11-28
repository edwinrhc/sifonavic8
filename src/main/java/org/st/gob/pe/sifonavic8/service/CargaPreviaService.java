package org.st.gob.pe.sifonavic8.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.st.gob.pe.sifonavic8.dto.CargaInsertHerederoDTO;
import org.st.gob.pe.sifonavic8.entity.CargaPrevia;
import org.st.gob.pe.sifonavic8.mapper.CargaPreviaMapper;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CargaPreviaService {

    @Autowired
   CargaPreviaMapper cargaPreviaMapper;


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
}
