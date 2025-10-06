package com.franciscoabsl.messaging;

import java.io.Serializable;

public class ReservaStatusDTO implements Serializable {
    private Long idPedido;
    private String status; // Ex: "CONFIRMADO" ou "CANCELADO"

    public ReservaStatusDTO() {}

    public ReservaStatusDTO(Long idPedido, String status) {
        this.idPedido = idPedido;
        this.status = status;
    }

    public Long getIdPedido() {
        return idPedido;
    }
    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}