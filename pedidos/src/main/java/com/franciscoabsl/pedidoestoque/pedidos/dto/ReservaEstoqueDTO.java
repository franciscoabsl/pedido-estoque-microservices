package com.franciscoabsl.pedidoestoque.pedidos.dto;

import java.io.Serializable;
import java.util.List;

public class ReservaEstoqueDTO implements Serializable {
    private Long idPedido;
    private List<ItemDTO> itens;

    public ReservaEstoqueDTO() {}

    public ReservaEstoqueDTO(Long idPedido, List<ItemDTO> itens) {
        this.idPedido = idPedido;
        this.itens = itens;
    }

    public Long getIdPedido() {
        return idPedido;
    }
    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }
    public List<ItemDTO> getItens() {
        return itens;
    }
    public void setItens(List<ItemDTO> itens) {
        this.itens = itens;
    }
}