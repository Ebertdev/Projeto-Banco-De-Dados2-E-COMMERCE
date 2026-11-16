package br.edu.bd2.loja.api;

import br.edu.bd2.loja.api.dto.ClienteDTO;
import br.edu.bd2.loja.dao.ClienteDAO;
import br.edu.bd2.loja.modelo.Cliente;
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

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Clientes", description = "CRUD de clientes (entidade JPA Cliente, herdeira de Pessoa)")
public class ClienteResource {

    private final ClienteDAO dao = new ClienteDAO();

    @GET
    @Operation(summary = "Lista clientes com paginação")
    public List<ClienteDTO> listar(@QueryParam("pagina") @DefaultValue("1") int pagina,
                                   @QueryParam("tamanho") @DefaultValue("20") int tamanho) {
        return dao.listarPaginado(pagina, Math.min(Math.max(tamanho, 1), 100))
                .stream().map(ClienteDTO::de).collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca cliente por id", responses = {@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "404", description = "Não encontrado")})
    public ClienteDTO buscar(@PathParam("id") Long id) {
        return ClienteDTO.de(existente(id));
    }

    @POST
    @Operation(summary = "Cadastra cliente", responses = {@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "400"), @ApiResponse(responseCode = "409", description = "E-mail ou CPF já cadastrado")})
    public Response criar(ClienteDTO dto, @Context UriInfo uri) {
        validar(dto);
        Cliente c = new Cliente();
        dto.aplicarEm(c);
        dao.salvar(c);
        return Response.created(uri.getAbsolutePathBuilder().path(String.valueOf(c.getId())).build())
                .entity(ClienteDTO.de(c)).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualiza cliente")
    public ClienteDTO atualizar(@PathParam("id") Long id, ClienteDTO dto) {
        validar(dto);
        Cliente c = existente(id);
        dto.aplicarEm(c);
        return ClienteDTO.de(dao.atualizar(c));
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Remove cliente", responses = {@ApiResponse(responseCode = "204"), @ApiResponse(responseCode = "404"), @ApiResponse(responseCode = "409", description = "Cliente possui pedidos")})
    public Response remover(@PathParam("id") Long id) {
        if (!dao.remover(id)) {
            throw new NotFoundException("Cliente " + id + " não encontrado");
        }
        return Response.noContent().build();
    }

    private Cliente existente(Long id) {
        Cliente c = dao.buscarPorId(id);
        if (c == null) {
            throw new NotFoundException("Cliente " + id + " não encontrado");
        }
        return c;
    }

    private void validar(ClienteDTO d) {
        if (d == null || vazio(d.nome) || vazio(d.email) || vazio(d.cpf)) {
            throw new BadRequestException("nome, email e cpf são obrigatórios");
        }
    }

    private boolean vazio(String s) {
        return s == null || s.trim().isEmpty();
    }
}
