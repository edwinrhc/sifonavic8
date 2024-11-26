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

}
