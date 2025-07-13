package com.aluracursos.ScreenMatch.principal;

import com.aluracursos.ScreenMatch.model.DatosEpisodio;
import com.aluracursos.ScreenMatch.model.DatosSerie;
import com.aluracursos.ScreenMatch.model.DatosTemporadas;
import com.aluracursos.ScreenMatch.model.Episodio;
import com.aluracursos.ScreenMatch.service.ConsumoAPI;
import com.aluracursos.ScreenMatch.service.ConvierteDatos;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=f0365c95";
    private ConvierteDatos conversor = new ConvierteDatos();

    public void  muestraElMenu(){
        System.out.println("Por favor escribe el nombre de la serie que deseas buscar");

        //Busca los datos generales de las series
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE+ nombreSerie.replace(" ","+")+API_KEY);
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);
        //Busca los datos de todas las temporadas
        List<DatosTemporadas> temporadas = new ArrayList<>();
        for (int i = 1; i <= datos.totalDeTemporadas(); i++) {
            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ","+") + "&Season=" +i+API_KEY);
            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
            temporadas.add(datosTemporadas);
        }
        temporadas.forEach(System.out::println);

        //Mostrar solo el titulo de los episodios para las tempoaradas
  //      for (int i = 0; i < datos.totalDeTemporadas() ; i++) {
  //          List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
   //         for (int j = 0; j <episodiosTemporada.size(); j++) {
     //           System.out.println(episodiosTemporada.get(j).titulo());
      //      }
       // }
//        temporadas.forEach(t -> t.episodios().forEach(e-> System.out.println(e.titulo())) );

    // Convertir todas las informaciones a una lista del tipo DatosEpisodio
    List <DatosEpisodio> datosEpisodios = temporadas.stream()
            .flatMap(t->t.episodios().stream())
            .collect(Collectors.toList());

    //Top 5 episodios
        System.out.println("Top 5 episodios");
        datosEpisodios.stream()
                .filter(e->!e.evaluacion().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
                .limit(5)
                .forEach(System.out::println);

        //Convirtiendo los datos a una lista del tipo Episodio
        List <Episodio> episodios = temporadas.stream()
                .flatMap(t->t.episodios().stream()
                        .map(d-> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);
    }

}
