package com.franciscoabsl.pedidoestoque.pedidos.messaging;

import com.franciscoabsl.messaging.ReservaStatusDTO;
import com.franciscoabsl.pedidoestoque.pedidos.model.Pedido;
import com.franciscoabsl.pedidoestoque.pedidos.repository.PedidoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class PedidoConsumer {

    private final PedidoRepository pedidoRepository;

    public PedidoConsumer(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @RabbitListener(queues = "${broker.queue.pedido.status.resposta}")
    @Transactional
    public void consumirStatus(ReservaStatusDTO statusDto) {
        System.out.println("<- Pedidos: Recebido status '" + statusDto.getStatus() + "' para o Pedido " + statusDto.getIdPedido());

        Optional<Pedido> optionalPedido = pedidoRepository.findById(statusDto.getIdPedido());

        if (optionalPedido.isPresent()) {
            Pedido pedido = optionalPedido.get();

            pedido.setStatus(statusDto.getStatus());

            if ("CONFIRMADO".equals(statusDto.getStatus())) {
                pedido.setValorTotal(statusDto.getValorTotal());
            } else {
                pedido.setValorTotal(0.0);
            }

            pedidoRepository.save(pedido);

            System.out.println("-> Pedidos: Pedido " + pedido.getId() + " finalizado com status: " + statusDto.getStatus() + ", Valor: " + pedido.getValorTotal());
        }
    }
}