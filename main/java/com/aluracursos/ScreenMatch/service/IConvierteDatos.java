package com.aluracursos.ScreenMatch.service;

public interface IConvierteDatos {
    <T> T obtenerDatos (String json, Class <T> clase);
}
