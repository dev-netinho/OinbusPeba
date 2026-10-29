package br.edu.oinbupebas.identity.application;
public interface RecoveryMailDelivery { boolean available(); void send(String recipient,String rawToken); }
