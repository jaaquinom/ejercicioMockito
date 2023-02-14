package com.minsait.services;

import com.minsait.models.Examen;
import com.minsait.repositories.ExamenRepository;
import com.minsait.repositories.PreguntasRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    @Mock
    ExamenRepository examenRepository;
    @Mock
    PreguntasRepository preguntasRepository;
    @InjectMocks
    ExamenServiceImpl service;
    @Captor
    ArgumentCaptor<Long> captor;

    @Test
    void testArgumentCaptor(){
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(preguntasRepository).findPreguntasByExamenId(captor.capture());

        assertEquals(1L, captor.getValue());
    }

    @Test
    void testFindExamenPorNombre() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        String nombre = "Quimica";

        Optional<Examen> examen = service.findExamenPorNombre(nombre);
        assertTrue(examen.isPresent());
        assertEquals(nombre, examen.get().getNombre());

    }
    @Test
    void testSaveConPreguntas(){

        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);

        when(service.save(examen)).thenReturn(new Examen(2L,"Fisica"));

        Examen examenConPreguntas = service.save(examen);

        assertTrue(examenConPreguntas.getPreguntas().isEmpty());
    }
    @Test
    void testSaveSinPreguntas(){

        Examen examen = Datos.EXAMEN;
        when(service.save(examen)).thenReturn(new Examen(2L,"Fisica"));
        Examen examenSinPreguntas = service.save(examen);

        assertTrue(examenSinPreguntas.getPreguntas().isEmpty());
    }
    @Test
    void testFindExamenPorNombreConPreguntas() {



        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasByExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = service.findExamenPorNombreConPreguntas("Fisica");

        assertTrue(examen.getPreguntas().contains("Aritmetica"));
        verify(examenRepository, times(1)).findAll();
        verify(preguntasRepository, atMostOnce()).findPreguntasByExamenId(anyLong());

    }

    @Test
    void testExceptions(){
        /*
        when(examenRepository.findAll()).thenReturn(Datos.EXAMANES);
        when(preguntasRepository.findPreguntasByExamenId(anyLong())).thenThrow(IllegalArgumentException.class);
        String nombre = "Fisica";

        assertThrows(IllegalArgumentException.class, ()-> service.findExamenPorNombreConPreguntas(nombre));
        assertEquals(IllegalArgumentException.class, assertThrows(IllegalArgumentException.class,
                ()-> service.findExamenPorNombreConPreguntas(nombre).getClass() ));*/
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntasRepository.findPreguntasByExamenId(anyLong())).thenThrow(IllegalArgumentException.class);
        String nombre = "Fisica";

        assertThrows(IllegalArgumentException.class, () -> service.findExamenPorNombreConPreguntas(nombre));
        assertEquals(IllegalArgumentException.class, assertThrows(IllegalArgumentException.class,
                () -> service.findExamenPorNombreConPreguntas(nombre)).getClass());

    }

    @Test
    void testDoThrow(){
        //Given --Dado
        doThrow(RuntimeException.class).when(preguntasRepository).savePreguntas(Datos.PREGUNTAS);
        //when -- entonces
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);

        //Then --probamos
        assertThrows(RuntimeException.class, () -> service.save(examen));
    }
    @Test
    void testDoAnswer(){
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        doAnswer(invocationOnMock -> {
           Long id = invocationOnMock.getArgument(0);
           return id==1 ? Datos.PREGUNTAS : Collections.EMPTY_LIST;
        }).when(preguntasRepository).findPreguntasByExamenId(anyLong());
        /*when(preguntasRepository.findPreguntasByExamenId(1L)).thenReturn(Datos.PREGUNTAS);
        when(preguntasRepository.findPreguntasByExamenId(2L)).thenReturn(Datos.PREGUNTAS);*/

        Examen examen = service.findExamenPorNombreConPreguntas("Matematicas");
        assertAll(
                ()->assertEquals(1L, examen.getId(), ()-> "El examen no es Matematicas"),
                ()-> assertFalse(examen.getPreguntas().isEmpty(), ()-> "El examen no es Matematicas")
                //()-> assertTrue(examen.getPreguntas().isEmpty(), ()-> "El examen es Matematicas")
        );
    }


}