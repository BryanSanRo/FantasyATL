package com.bryan_raul_proyecto.atletismo_api.service;

import com.bryan_raul_proyecto.atletismo_api.dao.CompeticionDao;
import com.bryan_raul_proyecto.atletismo_api.dto.CompeticionDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CompeticionService {

    private final CompeticionDao competicionDao;
    private final LugarService lugarService;

    public CompeticionService(CompeticionDao competicionDao, LugarService lugarService) {
        this.competicionDao = competicionDao;
        this.lugarService = lugarService;
    }

    /**
     * Crea una nueva competicion en el sistema tras validar sus campos.
     * @param competicion datos de la competicion a crear
     * @return identificador generado de la competicion creada
     */
    public UUID crear(CompeticionDto competicion) {
        validar(competicion);
        return competicionDao.insertar(competicion);
    }

    /**
     * Comprueba si existe una competicion con el identificador dado.
     * @param id identificador de la competicion a comprobar
     * @return true si existe, false en caso contrario
     */
    public boolean existe(UUID id) {
        return competicionDao.existePorId(id);
    }

    /**
     * Valida las reglas de negocio de la competicion:
     * tipo de pista admitido, jornada con numero positivo y un lugar que exista.
     * @param competicion competicion a validar
     */
    private void validar(CompeticionDto competicion) {
        if (!CompeticionConstantes.TIPOS_PISTA_VALIDOS.contains(competicion.getTipoPista())) {
            throw new IllegalArgumentException(
                    "Tipo de pista no valido: '" + competicion.getTipoPista() + "'. " +
                            "Valores admitidos: " + CompeticionConstantes.TIPOS_PISTA_VALIDOS
            );
        }
        if (competicion.getJornada() == null || competicion.getJornada() <= 0) {
            throw new IllegalArgumentException(
                    "La jornada debe ser un numero entero positivo"
            );
        }
        if (!lugarService.existe(competicion.getLugarId())) {
            throw new IllegalArgumentException(
                    "No existe un lugar con id: '" + competicion.getLugarId() + "'"
            );
        }
        if (competicionDao.existeNombreFecha(competicion.getNombre(), competicion.getFecha())) {
            throw new IllegalArgumentException(
                    "Ya existe una competicion con nombre '" + competicion.getNombre() +
                            "' en la fecha " + competicion.getFecha()
            );
        }
    }
}