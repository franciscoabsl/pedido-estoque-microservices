package com.franciscoabsl.pedidoestoque.pedidos.controller;

import com.franciscoabsl.messaging.ItemDTO;
import com.franciscoabsl.messaging.ReservaEstoqueDTO;

import com.franciscoabsl.pedidoestoque.pedidos.messaging.PedidoProducer;
import com.franciscoabsl.pedidoestoque.pedidos.model.Pedido;
import com.franciscoabsl.pedidoestoque.pedidos.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final PedidoProducer pedidoProducer;

    public PedidoController(PedidoRepository pedidoRepository, PedidoProducer pedidoProducer) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoProducer = pedidoProducer;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Pedido> criarPedido(@RequestBody List<ItemDTO> itensDto) {

        Pedido novoPedido = new Pedido();

        novoPedido.setValorTotal(0.0);

        List<String> itensStr = itensDto.stream()
                .map(i -> "ID:" + i.getIdProduto() + " Qtd:" + i.getQuantidadeComprada())
                .collect(Collectors.toList());
        novoPedido.setItens(itensStr);

        Pedido pedidoSalvo = pedidoRepository.save(novoPedido);

        System.out.println("-> Pedidos: Pedido ID " + pedidoSalvo.getId() + " criado com sucesso e status PENDENTE.");

        ReservaEstoqueDTO reservaDto = new ReservaEstoqueDTO(pedidoSalvo.getId(), itensDto);

        pedidoProducer.publishReserva(reservaDto);

        return new ResponseEntity<>(pedidoSalvo, HttpStatus.CREATED);
    }
}