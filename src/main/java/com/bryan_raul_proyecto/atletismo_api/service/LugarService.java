package com.bryan_raul_proyecto.atletismo_api.service;

import com.bryan_raul_proyecto.atletismo_api.dao.LugarDao;
import com.bryan_raul_proyecto.atletismo_api.dto.LugarDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LugarService {

    private final LugarDao lugarDao;

    public LugarService(LugarDao lugarDao) {
        this.lugarDao = lugarDao;
    }

    /**
     * Crea un nuevo lugar en el sistema.
     * @param lugar datos del lugar a crear
     * @return identificador generado del lugar creado
     */
    public UUID crear(LugarDto lugar) {
        return lugarDao.insertar(lugar);
    }

    /**
     * Comprueba si existe un lugar con el identificador dado.
     * @param id identificador del lugar a comprobar
     * @return true si existe, false en caso contrario
     */
    public boolean existe(UUID id) {
        return lugarDao.existePorId(id);
    }
}