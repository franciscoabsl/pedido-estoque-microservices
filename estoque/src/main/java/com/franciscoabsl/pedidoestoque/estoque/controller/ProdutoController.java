package com.franciscoabsl.pedidoestoque.estoque.controller;

import com.franciscoabsl.pedidoestoque.estoque.model.Produto;
import com.franciscoabsl.pedidoestoque.estoque.repository.ProdutoRepository;
import com.franciscoabsl.pedidoestoque.estoque.service.ProdutoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/produtos")
public class ProdutoController implements CommandLineRunner {

    private final ProdutoService produtoService;
    private final ProdutoRepository produtoRepository;


    public ProdutoController(ProdutoService produtoService, ProdutoRepository produtoRepository) {
        this.produtoService = produtoService;
        this.produtoRepository = produtoRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Produto produto1 = new Produto(1L, "Monitor Ultrawide", 1800.00, 10);
        Produto produto2 = new Produto(2L, "Mouse Sem Fio", 150.00, 50);
        Produto produto3 = new Produto(3L, "Teclado Mecânico", 550.00, 15);
        Produto produto4 = new Produto(4L, "Cadeira Gamer", 1200.00, 3);

        List<Produto> produtosParaCadastrar = Arrays.asList(
                produto1, produto2, produto3, produto4
        );

        produtoRepository.saveAll(produtosParaCadastrar);
        System.out.println("--- " + produtosParaCadastrar.size() + " Produtos de Teste Inicializados no Estoque ---");
    }

    @PostMapping
    public ResponseEntity<Produto> adicionarProduto(@RequestBody Produto produto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.salvar(produto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> getProduto(@PathVariable Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        return produto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}