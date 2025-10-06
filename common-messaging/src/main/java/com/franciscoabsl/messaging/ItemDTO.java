package com.franciscoabsl.messaging;

import java.io.Serializable;

public class ItemDTO implements Serializable {
    private Long idProduto;
    private Integer quantidadeComprada;

    public ItemDTO() {}

    public ItemDTO(Long idProduto, Integer quantidadeComprada) {
        this.idProduto = idProduto;
        this.quantidadeComprada = quantidadeComprada;
    }

    public Long getIdProduto() {
        return idProduto;
    }
    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }
    public Integer getQuantidadeComprada() {
        return quantidadeComprada;
    }
    public void setQuantidadeComprada(Integer quantidadeComprada) {
        this.quantidadeComprada = quantidadeComprada;
    }
}