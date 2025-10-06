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
    public void reservarEstoque(List<ItemDTO> itens) {
        for (ItemDTO item : itens) {
            Produto produto = produtoRepository.findById(item.getIdProduto())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + item.getIdProduto()));

            int quantidadeDesejada = item.getQuantidadeComprada();
            int estoqueAtual = produto.getQuantidadeEmEstoque();

            if (estoqueAtual < quantidadeDesejada) {
                throw new RuntimeException("Estoque insuficiente para o Produto ID " + item.getIdProduto() + ". Desejado: " + quantidadeDesejada + ", Disponível: " + estoqueAtual);
            }

            produto.setQuantidadeEmEstoque(estoqueAtual - quantidadeDesejada);
            produtoRepository.save(produto);
        }
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public java.util.Optional<Produto> findById(Long id) {
        return produtoRepository.findById(id);
    }
}