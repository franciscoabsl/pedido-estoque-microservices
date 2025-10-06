package com.franciscoabsl.pedidoestoque.pedidos.repository;

import com.franciscoabsl.pedidoestoque.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}