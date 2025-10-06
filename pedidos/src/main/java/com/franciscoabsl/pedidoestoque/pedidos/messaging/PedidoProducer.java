package com.franciscoabsl.pedidoestoque.pedidos.messaging;

import com.franciscoabsl.messaging.ReservaEstoqueDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PedidoProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${broker.queue.estoque.diminuir}")
    private String routingKey;

    public PedidoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishReserva(ReservaEstoqueDTO reservaDto) {
        System.out.println("-> Pedido " + reservaDto.getIdPedido() + ": Enviando solicitação de reserva para o estoque.");

        rabbitTemplate.convertAndSend("", routingKey, reservaDto);
    }
}