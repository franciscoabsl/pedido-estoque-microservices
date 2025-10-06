package com.franciscoabsl.pedidoestoque.estoque.service;

import com.franciscoabsl.messaging.ItemDTO;
import com.franciscoabsl.pedidoestoque.estoque.model.Produto;
import com.franciscoabsl.pedidoestoque.estoque.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Double reservarEstoque(List<ItemDTO> itens) {
        double valorTotal = 0.0;

        for (ItemDTO item : itens) {
            Produto produto = produtoRepository.findById(item.getIdProduto())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + item.getIdProduto()));

            int quantidadeDesejada = item.getQuantidadeComprada();
            int estoqueAtual = produto.getQuantidadeEmEstoque();

            if (estoqueAtual < quantidadeDesejada) {
                throw new RuntimeException("Estoque insuficiente para o Produto ID " + item.getIdProduto() + "...");
            }

            valorTotal += produto.getPreco() * quantidadeDesejada;

            produto.setQuantidadeEmEstoque(estoqueAtual - quantidadeDesejada);
            produtoRepository.save(produto);
        }

        return valorTotal;
    }


    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public java.util.Optional<Produto> findById(Long id) {
        return produtoRepository.findById(id);
    }
}