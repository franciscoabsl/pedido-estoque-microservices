package com.franciscoabsl.pedidoestoque.pedidos.controller;


import com.franciscoabsl.pedidoestoque.pedidos.dto.ItemDTO;
import com.franciscoabsl.pedidoestoque.pedidos.dto.ReservaEstoqueDTO;
import com.franciscoabsl.pedidoestoque.pedidos.messaging.PedidoProducer;
import com.franciscoabsl.pedidoestoque.pedidos.model.Pedido;
import com.franciscoabsl.pedidoestoque.pedidos.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                .map(item -> String.format("ID:%d Qtd:%d", item.getIdProduto(), item.getQuantidadeComprada()))
                .collect(java.util.stream.Collectors.toList());

        novoPedido.setItens(itensStr);
        Pedido pedidoSalvo = pedidoRepository.save(novoPedido);
        System.out.println("-> Pedidos: Pedido ID " + pedidoSalvo.getId() + " criado com sucesso e status PENDENTE.");
        ReservaEstoqueDTO reservaDto = new ReservaEstoqueDTO(pedidoSalvo.getId(), itensDto);
        pedidoProducer.publishReserva(reservaDto);

        return new ResponseEntity<>(pedidoSalvo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoStatus(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}