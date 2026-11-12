package sistema.mockito.service;

import sistema.mockito.Pedido;
import sistema.mockito.Produto;
import sistema.mockito.exception.EstoqueInsuficienteException;
import sistema.mockito.exception.ProdutoNaoEncontradoException;
import sistema.mockito.repository.PedidoRepository;
import sistema.mockito.repository.ProdutoRepository;

public class PedidoService {
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public PedidoService(ProdutoRepository produtoRepository, PedidoRepository pedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido criarPedido(Long produtoId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        Produto produto = produtoRepository.buscarPorId(produtoId);
        if (produto == null) {
            throw new ProdutoNaoEncontradoException("Produto não encontrado: " + produtoId);
        }

        if (quantidade > produto.getQuantidade()) {
            throw new EstoqueInsuficienteException("Estoque insuficiente");
        }

        double total = produto.getPreco() * quantidade;
        Pedido pedido = new Pedido(produto, quantidade, total);

        pedidoRepository.salvar(pedido);

        return pedido;
    }
}