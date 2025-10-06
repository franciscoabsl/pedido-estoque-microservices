package com.franciscoabsl.pedidoestoque.estoque.messaging;

import com.franciscoabsl.messaging.ReservaEstoqueDTO;
import com.franciscoabsl.messaging.ReservaStatusDTO;
import com.franciscoabsl.pedidoestoque.estoque.service.ProdutoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EstoqueConsumer {

    private final ProdutoService produtoService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${broker.queue.pedido.status.resposta}")
    private String respostaQueue;

    public EstoqueConsumer(ProdutoService produtoService, RabbitTemplate rabbitTemplate) {
        this.produtoService = produtoService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${broker.queue.estoque.diminuir}")
    public void consumirReserva(ReservaEstoqueDTO reservaDto) {
        Long idPedido = reservaDto.getIdPedido();
        String statusFinal;
        Double valorTotalCalculado = 0.0;

        try {
            System.out.println("<- Estoque: Recebido pedido " + idPedido + " para reserva.");

            valorTotalCalculado = produtoService.reservarEstoque(reservaDto.getItens());

            statusFinal = "CONFIRMADO";
            System.out.println("-> Estoque: Pedido " + idPedido + " confirmado. Valor: " + valorTotalCalculado);

        } catch (RuntimeException e) {
            statusFinal = "CANCELADO";
            System.err.println("-> Estoque: Pedido " + idPedido + " CANCELADO. Motivo: " + e.getMessage());
        }

        ReservaStatusDTO statusDto = new ReservaStatusDTO(idPedido, statusFinal, valorTotalCalculado);

        rabbitTemplate.convertAndSend("", respostaQueue, statusDto);
        System.out.println("-> Estoque: Status '" + statusFinal + "' enviado para o Pedido " + idPedido);
    }
}