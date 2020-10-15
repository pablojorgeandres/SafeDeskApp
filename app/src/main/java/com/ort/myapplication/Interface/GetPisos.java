package com.ort.myapplication.Interface;

import com.ort.myapplication.Model.Piso;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetPisos {

    @GET("/api/turnos/pisos/fecha/{fecha}/edificio/{idEdificio}")
    Call<List<Piso>> getPisos(@Path("fecha") String fecha, @Path("idEdificio") long idEdificio);

}
