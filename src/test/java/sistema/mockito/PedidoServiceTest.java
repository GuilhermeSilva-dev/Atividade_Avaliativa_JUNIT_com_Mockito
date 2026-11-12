package sistema.mockito;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistema.mockito.exception.ErroAoSalvarPedidoException;
import sistema.mockito.exception.EstoqueInsuficienteException;
import sistema.mockito.exception.ProdutoNaoEncontradoException;
import sistema.mockito.repository.PedidoRepository;
import sistema.mockito.repository.ProdutoRepository;
import sistema.mockito.service.PedidoService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService service;

    @Test
    @DisplayName("PED01 e PED02 - Pedido válido calcula o total e é salvo")
    void deveCalcularValorTotalESalvarPedido() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        Pedido pedido = service.criarPedido(1L, 2);

        Assertions.assertEquals(99.80, pedido.getValorTotal(), 0.001);
        verify(produtoRepository, times(1)).buscarPorId(1L);
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    @Test
    @DisplayName("PED03 - Quantidade zero lança exceção e não salva")
    void naoDeveCriarPedidoComQuantidadeZero() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.criarPedido(1L, 0));

        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("PED04 - Quantidade negativa lança exceção e não salva")
    void naoDeveCriarPedidoComQuantidadeNegativa() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.criarPedido(1L, -3));

        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("PED05 - Produto inexistente lança exceção e não salva")
    void naoDeveCriarPedidoSeProdutoNaoExistir() {
        when(produtoRepository.buscarPorId(99L)).thenReturn(null);

        Assertions.assertThrows(ProdutoNaoEncontradoException.class,
                () -> service.criarPedido(99L, 2));

        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("PED06 - Quantidade maior que estoque lança exceção")
    void naoDeveCriarPedidoComEstoqueInsuficiente() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        Assertions.assertThrows(EstoqueInsuficienteException.class,
                () -> service.criarPedido(1L, 11));

        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("PED07 - Quantidade igual ao estoque cria pedido (fronteira)")
    void deveCriarPedidoComQuantidadeIgualAoEstoque() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        Pedido pedido = service.criarPedido(1L, 10);

        Assertions.assertEquals(499.00, pedido.getValorTotal(), 0.001);
        verify(pedidoRepository).salvar(any(Pedido.class));
    }

    @Test
    @DisplayName("Cenário A - Banco fora do ar no salvar lança RuntimeException")
    void deveLancarExcecaoQuandoBancoFalharAoSalvarPedido() {
        Produto produto = new Produto("Monitor", 800.00, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        doThrow(new RuntimeException("Falha na conexão"))
                .when(pedidoRepository).salvar(any(Pedido.class));

        Assertions.assertThrows(RuntimeException.class,
                () -> service.criarPedido(1L, 2));
    }

    @Test
    @DisplayName("Cenário C - Falha no ProdutoRepository não chama salvar()")
    void naoDeveChamarSalvarQuandoProdutoRepositoryFalhar() {
        when(produtoRepository.buscarPorId(1L)).thenThrow(new RuntimeException("Falha no banco"));

        Assertions.assertThrows(RuntimeException.class,
                () -> service.criarPedido(1L, 2));

        verify(pedidoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Cenário D - Lança ErroAoSalvarPedidoException ao falhar salvamento")
    void deveLancarErroAoSalvarPedidoExceptionQuandoFalhar() {
        Produto produto = new Produto("Monitor", 800.00, 10);
        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        doThrow(new RuntimeException("Falha na conexão"))
                .when(pedidoRepository).salvar(any(Pedido.class));

        // Aqui testamos a sua nova exception do Desafio!
        Assertions.assertThrows(ErroAoSalvarPedidoException.class,
                () -> service.criarPedido(1L, 2));
    }
}