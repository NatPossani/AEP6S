package br.com.aep.service;

import br.com.aep.model.Doacao;
import br.com.aep.repository.DoacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DoacaoServiceTest {

    private DoacaoRepository repository;
    private DoacaoService service;

    @BeforeEach
    void configurar() {
        repository = mock(DoacaoRepository.class);
        service = new DoacaoService(repository);
    }

    @Test
    void deveCadastrarDoacao() {
        Doacao doacao = service.cadastrar(
                "Mercado Central",
                "Arroz",
                20,
                "kg",
                "30/12/2026",
                "Instituição Esperança"
        );

        assertNotNull(doacao.getCodigo());
        assertEquals("Arroz", doacao.getAlimento());
        assertEquals(20, doacao.getQuantidade());

        verify(repository).salvar(doacao);
    }

    @Test
    void deveListarDoacoes() {
        Doacao doacao = new Doacao(
                "ABC12345", "Doador", "Arroz",
                10, "kg", "01/01/2027", "Instituição"
        );

        when(repository.listar()).thenReturn(List.of(doacao));

        List<Doacao> resultado = service.listar();

        assertEquals(1, resultado.size());
        assertEquals("ABC12345", resultado.get(0).getCodigo());
    }

    @Test
    void deveBuscarDoacao() {
        Doacao doacao = new Doacao(
                "ABC12345", "Doador", "Arroz",
                10, "kg", "01/01/2027", "Instituição"
        );

        when(repository.buscarPorCodigo("ABC12345")).thenReturn(doacao);

        Doacao resultado = service.buscar(" abc12345 ");

        assertNotNull(resultado);
        assertEquals("ABC12345", resultado.getCodigo());
    }

    @Test
    void deveRetornarNuloAoBuscarCodigoVazio() {
        assertNull(service.buscar(" "));
        verifyNoInteractions(repository);
    }

    @Test
    void deveAtualizarDoacao() {
        when(repository.atualizar(eq("ABC12345"), any(Doacao.class)))
                .thenReturn(true);

        boolean resultado = service.atualizar(
                "abc12345",
                "Novo Doador",
                "Feijão",
                15,
                "kg",
                "10/01/2027",
                "Nova Instituição"
        );

        assertTrue(resultado);
        verify(repository).atualizar(eq("ABC12345"), any(Doacao.class));
    }

    @Test
    void deveExcluirDoacao() {
        when(repository.excluir("ABC12345")).thenReturn(true);

        assertTrue(service.excluir("abc12345"));

        verify(repository).excluir("ABC12345");
    }

    @Test
    void deveRetornarFalsoAoExcluirCodigoVazio() {
        assertFalse(service.excluir(""));
        verifyNoInteractions(repository);
    }

    @Test
    void naoDeveCadastrarComQuantidadeInvalida() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(
                        "Doador", "Arroz", 0,
                        "kg", "01/01/2027", "Instituição"
                )
        );

        assertEquals("A quantidade deve ser maior que zero.",
                exception.getMessage());

        verifyNoInteractions(repository);
    }

    @Test
    void naoDeveCadastrarSemNomeDoDoador() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(
                        "", "Arroz", 10,
                        "kg", "01/01/2027", "Instituição"
                )
        );

        verifyNoInteractions(repository);
    }

    @Test
    void naoDeveCadastrarSemAlimento() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(
                        "Doador", " ", 10,
                        "kg", "01/01/2027", "Instituição"
                )
        );
    }

    @Test
    void naoDeveCadastrarSemUnidade() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(
                        "Doador", "Arroz", 10,
                        "", "01/01/2027", "Instituição"
                )
        );
    }

    @Test
    void naoDeveCadastrarSemValidade() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(
                        "Doador", "Arroz", 10,
                        "kg", "", "Instituição"
                )
        );
    }

    @Test
    void naoDeveCadastrarComValidadeInvalida(){
        assertThrows(IllegalArgumentException.class, 
                () -> service.cadastrar(
                        "Doador", "Arroz", 20,
                        "kg", 
                        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        "Instituição"
                )
        );
    }

    @Test
    void naoDeveCadastrarSemDestino() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.cadastrar(
                        "Doador", "Arroz", 10,
                        "kg", "01/01/2027", " "
                )
        );
    }
}
