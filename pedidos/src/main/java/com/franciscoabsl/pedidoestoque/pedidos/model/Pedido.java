package com.franciscoabsl.pedidoestoque.pedidos.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<String> itens;

    private Double valorTotal;
    private String status; // PENDENTE_ESTOQUE, CONFIRMADO, CANCELADO

    public Pedido() {
        this.status = "PENDENTE";
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public List<String> getItens() { return itens; }
    public void setItens(List<String> itens) { this.itens = itens; }
    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}