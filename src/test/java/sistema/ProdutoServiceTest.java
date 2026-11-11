package sistema;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistema.mockito.Produto;
import sistema.mockito.exception.ErroAoSalvarProdutoException;
import sistema.mockito.exception.ProdutoNaoEncontradoException;
import sistema.mockito.repository.ProdutoRepository;
import sistema.mockito.service.ProdutoService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //[cite: 3, 6]
public class ProdutoServiceTest {

    @Mock //[cite: 3, 6]
    private ProdutoRepository repository;

    @InjectMocks //[cite: 3, 6]
    private ProdutoService service;

    @Test
    @DisplayName("CT01 - Produto válido é salvo e retornado")
    void deveSalvarProdutoValido() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(repository.salvar(produto)).thenReturn(produto); //[cite: 5]

        Produto resultado = service.salvar(produto);

        Assertions.assertEquals("Mouse", resultado.getNome());
        verify(repository).salvar(produto); //[cite: 5]
    }

    @Test
    @DisplayName("CT02 - Produto com preço negativo lança exceção")
    void naoDeveSalvarProdutoComPrecoNegativo() {
        Produto produto = new Produto("Teclado", -10.0, 5);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produto)); //[cite: 5]

        verify(repository, never()).salvar(any()); //[cite: 5]
    }

    @Test
    @DisplayName("CT03 - Produto com preço zero lança exceção")
    void naoDeveSalvarProdutoComPrecoZero() {
        Produto produto = new Produto("Monitor", 0.0, 5);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produto));

        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("CT04 - Produto com nome vazio lança exceção")
    void naoDeveSalvarProdutoComNomeVazio() {
        Produto produto = new Produto("", 100.0, 5);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produto));

        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("CT05 - Produto com nome nulo lança exceção")
    void naoDeveSalvarProdutoComNomeNulo() {
        Produto produto = new Produto(null, 100.0, 5);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produto));

        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("CT06 - Produto com quantidade negativa lança exceção")
    void naoDeveSalvarProdutoComQuantidadeNegativa() {
        Produto produto = new Produto("Mousepad", 20.0, -1);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.salvar(produto));

        verify(repository, never()).salvar(any());
    }

    @Test
    @DisplayName("CT07 - Produto com quantidade zero é salvo com sucesso")
    void deveSalvarProdutoComQuantidadeZero() {
        Produto produto = new Produto("Cabo", 15.0, 0);
        when(repository.salvar(produto)).thenReturn(produto);

        Produto resultado = service.salvar(produto);

        Assertions.assertEquals(0, resultado.getQuantidade());
        verify(repository).salvar(produto);
    }

    @Test
    @DisplayName("CT08 - Buscar produto existente retorna o produto")
    void deveRetornarProdutoExistente() {
        Produto produto = new Produto("Mouse", 49.90, 10);
        when(repository.buscarPorId(1L)).thenReturn(produto); //[cite: 5]

        Produto resultado = service.buscarPorId(1L);

        Assertions.assertEquals("Mouse", resultado.getNome());
        verify(repository).buscarPorId(1L);
    }

    @Test
    @DisplayName("CT09 - Buscar produto inexistente lança exceção")
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        when(repository.buscarPorId(99L)).thenReturn(null); //[cite: 7]

        Assertions.assertThrows(ProdutoNaoEncontradoException.class,
                () -> service.buscarPorId(99L)); //[cite: 7]

        verify(repository).buscarPorId(99L); //[cite: 7]
    }

    @Test
    @DisplayName("CT10 - Erro no banco de dados lança ErroAoSalvarProdutoException")
    void deveLancarErroQuandoRepositoryFalharAoSalvar() {
        Produto produto = new Produto("Teclado", 129.90, 5);
        doThrow(new RuntimeException("Falha na conexão"))
                .when(repository).salvar(any(Produto.class)); //[cite: 5, 7]

        Assertions.assertThrows(ErroAoSalvarProdutoException.class,
                () -> service.salvar(produto));

        verify(repository).salvar(any(Produto.class));
    }
}