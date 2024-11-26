package org.st.gob.pe.sifonavic8.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.st.gob.pe.sifonavic8.entity.CargaPrevia;
import org.st.gob.pe.sifonavic8.mapper.CargaPreviaMapper;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CargaPreviaService {

    @Autowired
   CargaPreviaMapper cargaPreviaMapper;


    public List<CargaPrevia> consultaPersona(String tipoDocumento, String numDocumento) {
        return cargaPreviaMapper.cargaPreviaConsultaPersona(tipoDocumento, numDocumento);
    }
}
