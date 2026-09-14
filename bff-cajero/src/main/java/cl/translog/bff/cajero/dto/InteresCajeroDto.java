package cl.translog.bff.cajero.dto;

public record InteresCajeroDto(

    Long id,
    Integer cuentaId,
    String nombre,
    Double saldoOriginal

){} 