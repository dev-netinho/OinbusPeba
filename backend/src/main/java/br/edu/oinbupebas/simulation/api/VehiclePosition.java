package br.edu.oinbupebas.simulation.api;
import java.time.Instant;
public record VehiclePosition(String vehicleId,String line,String direction,double latitude,double longitude,String nextStop,int etaMinutes,Instant calculatedAt,boolean simulated){}
