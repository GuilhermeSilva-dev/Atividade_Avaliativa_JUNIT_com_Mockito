package sistema.mockito.repository;

import sistema.mockito.Produto;

public interface ProdutoRepository {
    Produto salvar(Produto produto);
    Produto buscarPorId(Long id);
}