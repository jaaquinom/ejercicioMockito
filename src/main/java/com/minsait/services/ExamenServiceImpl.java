package com.minsait.services;

import com.minsait.models.Examen;
import com.minsait.repositories.ExamenRepository;
import com.minsait.repositories.PreguntasRepository;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements  ExamenService{

    private ExamenRepository examenRepository;

    private PreguntasRepository preguntasRepository;
    @Override
    public Optional<Examen> findExamenPorNombre(String nombre){
        return examenRepository.findAll().stream()
                .filter(n ->n.getNombre().equals(nombre)).findFirst();
    }
    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre){
        Optional<Examen> examenOptional = findExamenPorNombre(nombre);
        Examen examen = null;
        if(examenOptional.isPresent()){
            examen = examenOptional.get();
            List<String> preguntas = preguntasRepository.findPreguntasByExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen save(Examen examen){

        if(!examen.getPreguntas().isEmpty()){
            preguntasRepository.savePreguntas(examen.getPreguntas());
        }
        return examenRepository.save(examen);
    }
}
