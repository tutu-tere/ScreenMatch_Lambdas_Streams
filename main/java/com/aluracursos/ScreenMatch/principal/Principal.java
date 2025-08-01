package com.aluracursos.ScreenMatch.principal;

import com.aluracursos.ScreenMatch.model.*;
import com.aluracursos.ScreenMatch.repository.SerieRepository;
import com.aluracursos.ScreenMatch.service.ConsumoAPI;
import com.aluracursos.ScreenMatch.service.ConvierteDatos;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=f0365c95";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;

    public Principal(SerieRepository repository) {
            this.repositorio= repository;
    }

    public void  muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1- Buscar series
                    2- Buscar episodios
                    3- Mostrar series buscadas
                    4- Buscar series por titulo
                    
                    0-Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción invalida");
            }
        }
    }


    private DatosSerie getDatosSerie() {
            System.out.println("Escribe el nombre de la serie que deseas buscar");
            var nombreSerie = teclado.nextLine();
            var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
            System.out.println(json);
            DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
            return datos;
        }
        private void buscarEpisodioPorSerie() {
//            DatosSerie datosSerie = getDatosSerie();
            mostrarSeriesBuscadas();
            System.out.println("Escribe la serie de la cual quieres ver un episodio");
            var nombreSerie= teclado.nextLine();

            Optional<Serie> serie = series.stream()
                    .filter(s->s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                    .findFirst();

            if (serie.isPresent()){
                var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d->d.episodios().stream()
                    .map(e-> new Episodio(d.numero(),e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

            }
        }
        private void buscarSerieWeb() {
            DatosSerie datos = getDatosSerie();
            Serie serie = new Serie(datos);
            repositorio.save(serie);
//            datosSeries.add(datos);
            System.out.println(datos);
        }
    private void mostrarSeriesBuscadas() {
         series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo(){
        System.out.println("Escriba el titulo de la serie que desea buscar");
        var nombreSerie = teclado.nextLine();
        Optional<Serie> serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

        if(serieBuscada.isPresent()){
            System.out.println("La serie buscada es: "+ serieBuscada.get());
        } else {
            System.out.println("Serie no encontrada");
        }

    }




        //Busca los datos generales de las series
//        var nombreSerie = teclado.nextLine();
//        var json = consumoApi.obtenerDatos(URL_BASE+ nombreSerie.replace(" ","+")+API_KEY);
//        var datos = conversor.obtenerDatos(json, DatosSerie.class);
//        System.out.println(datos);
////        //Busca los datos de todas las temporadas
//        List<DatosTemporadas> temporadas = new ArrayList<>();
//        for (int i = 1; i <= datos.totalDeTemporadas(); i++) {
//            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ","+") + "&Season=" +i+API_KEY);
//            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
//            temporadas.add(datosTemporadas);
//        }
////        temporadas.forEach(System.out::println);
//
////        Mostrar solo el titulo de los episodios para las tempoaradas
////        for (int i = 0; i < datos.totalDeTemporadas() ; i++) {
////            List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
////            for (int j = 0; j <episodiosTemporada.size(); j++) {
////                System.out.println(episodiosTemporada.get(j).titulo());
////            }
////        }
////        temporadas.forEach(t -> t.episodios().forEach(e-> System.out.println(e.titulo())) );
//
//    // Convertir todas las informaciones a una lista del tipo DatosEpisodio
//    List <DatosEpisodio> datosEpisodios = temporadas.stream()
//            .flatMap(t->t.episodios().stream())
//            .collect(Collectors.toList());
//
//    //Top 5 episodios
////        System.out.println("Top 5 episodios");
////        datosEpisodios.stream()
////                .filter(e->!e.evaluacion().equalsIgnoreCase("N/A"))
////                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
////                .limit(5)
////                .forEach(System.out::println);
////
////        //Convirtiendo los datos a una lista del tipo Episodio
//        List <Episodio> episodios = temporadas.stream()
//                .flatMap(t->t.episodios().stream()
//                        .map(d-> new Episodio(t.numero(), d)))
//                .collect(Collectors.toList());
//
////        episodios.forEach(System.out::println);
//
//    //Busqueda de episodios a partir de x año
//
////        System.out.println("Indica el año del que quieres ver los episodios");
////        var fecha = teclado.nextInt();
////        teclado.nextLine();
////
////        LocalDate fechaBusqueda = LocalDate.of(fecha, 1,1);
////
////        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
////        episodios.stream()
////                .filter(e -> e.getFechaDeLanzamiento()!= null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
////                .forEach(e-> System.out.println(
////                        "Temporada " + e.getTemporada() +
////                                "  Episodio " + e.getTitulo() +
////                                "Fecha de lanzamiento " + e.getFechaDeLanzamiento().format(dtf)));
//
////Busca episodio por pedazo de titulo
//
////        System.out.println("Por favor escriba el titulo del episodio que desea ver");
////        var pedazoTitulo = teclado.nextLine();
////        Optional<Episodio> episodioBuscado = episodios.stream()
////                .filter(e -> e.getTitulo().toUpperCase().contains(pedazoTitulo.toUpperCase()))
////                .findFirst();
////            if(episodioBuscado.isPresent()){
////            System.out.println("Episodio Encontrado");
////            System.out.println("Los datos son:"+ episodioBuscado.get());
////        } else {
////            System.out.println("Episodio no encontrado");
////        }
//
//            //Evaluaciones por Temporadas
//        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
//                .filter(episodio -> episodio.getEvaluacion()>0.0)
//                .collect(Collectors.groupingBy(Episodio::getTemporada,
//                                Collectors.averagingDouble(Episodio::getEvaluacion)));
//                    System.out.println(evaluacionesPorTemporada);
//
//                    DoubleSummaryStatistics est = episodios.stream()
//                            .filter(episodio -> episodio.getEvaluacion()>0.0)
//                            .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
//                        System.out.println("Media de las evaluaciones:"+ est.getAverage());
//                        System.out.println("Episodio Mejor evaluado:"+ est.getMax());
//                        System.out.println("Episodio Peor evaluado:"+ est.getMin());


    }




