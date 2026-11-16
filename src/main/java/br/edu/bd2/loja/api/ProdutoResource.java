package br.edu.bd2.loja.api;

import br.edu.bd2.loja.api.dto.ProdutoDTO;
import br.edu.bd2.loja.dao.GenericDAO;
import br.edu.bd2.loja.dao.ProdutoDAO;
import br.edu.bd2.loja.modelo.Categoria;
import br.edu.bd2.loja.modelo.Produto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Produtos", description = "CRUD de produtos do catálogo")
public class ProdutoResource {

    private final ProdutoDAO dao = new ProdutoDAO();
    private final GenericDAO<Categoria, Long> categoriaDAO = new GenericDAO<>(Categoria.class);

    @GET
    @Operation(summary = "Lista produtos com paginação (JOIN FETCH da categoria)")
    public List<ProdutoDTO> listar(@QueryParam("pagina") @DefaultValue("1") int pagina,
                                   @QueryParam("tamanho") @DefaultValue("20") int tamanho) {
        return dao.listarComCategoria(pagina, Math.min(Math.max(tamanho, 1), 100))
                .stream().map(ProdutoDTO::de).collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca produto por id", responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404")})
    public ProdutoDTO buscar(@PathParam("id") Long id) {
        return ProdutoDTO.de(existente(id));
    }

    @POST
    @Operation(summary = "Cadastra produto", responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "400")})
    public Response criar(ProdutoDTO dto, @Context UriInfo uri) {
        validar(dto);
        Produto p = new Produto();
        dto.aplicarEm(p);
        p.setCategoria(categoria(dto.categoriaId));
        dao.salvar(p);
        return Response.created(uri.getAbsolutePathBuilder().path(String.valueOf(p.getId())).build())
                .entity(ProdutoDTO.de(p)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza produto")
    public ProdutoDTO atualizar(@PathParam("id") Long id, ProdutoDTO dto) {
        validar(dto);
        Produto p = existente(id);
        dto.aplicarEm(p);
        p.setCategoria(categoria(dto.categoriaId));
        return ProdutoDTO.de(dao.atualizar(p));
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Remove produto", responses = {@ApiResponse(responseCode = "204"), @ApiResponse(responseCode = "404"), @ApiResponse(responseCode = "409", description = "Produto já foi vendido")})
    public Response remover(@PathParam("id") Long id) {
        if (!dao.remover(id)) {
            throw new NotFoundException("Produto " + id + " não encontrado");
        }
        return Response.noContent().build();
    }

    private Produto existente(Long id) {
        Produto p = dao.buscarComCategoria(id);
        if (p == null) {
            throw new NotFoundException("Produto " + id + " não encontrado");
        }
        return p;
    }

    private Categoria categoria(Long id) {
        Categoria c = id == null ? null : categoriaDAO.buscarPorId(id);
        if (c == null) {
            throw new BadRequestException("categoriaId inexistente");
        }
        return c;
    }

    private void validar(ProdutoDTO d) {
        if (d == null || d.nome == null || d.nome.trim().isEmpty() || d.preco == null || d.preco.signum() < 0) {
            throw new BadRequestException("nome e preco (>= 0) são obrigatórios");
        }
    }
}
