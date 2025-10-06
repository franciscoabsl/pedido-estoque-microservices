package com.franciscoabsl.pedidoestoque.estoque.dto;

import java.io.Serializable;

public class ReservaStatusDTO implements Serializable {
    private Long idPedido;
    private String status; // "CONFIRMADO" ou "CANCELADO"
    private Double valorTotal;

    public ReservaStatusDTO() {}

    public ReservaStatusDTO(Long idPedido, String status, Double valorTotal) {
        this.idPedido = idPedido;
        this.status = status;
        this.valorTotal = valorTotal;
    }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }
}