package sistema.mockito;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistema.mockito.repository.ProdutoRepository;
import sistema.mockito.service.ProdutoService;

@ExtendWith(MockitoExtension.class)
public class ProdutoTest {

    @Mock
    private ProdutoRepository repository;

    @Mock
    private ProdutoService service;
}
