package br.edu.bd2.loja.api.dto;

import br.edu.bd2.loja.modelo.Cliente;
import br.edu.bd2.loja.modelo.Endereco;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(name = "Cliente")
public class ClienteDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY) public Long id;
    @Schema(example = "Ana Souza Lima", required = true) public String nome;
    @Schema(example = "ana.souza@email.com", required = true) public String email;
    @Schema(example = "529.982.247-25", required = true) public String cpf;
    @Schema(example = "1995-04-23") public LocalDate dataNascimento;
    @Schema(example = "Rua das Flores") public String logradouro;
    @Schema(example = "120") public String numero;
    @Schema(example = "Centro") public String bairro;
    @Schema(example = "Curitiba") public String cidade;
    @Schema(example = "PR") public String estado;
    @Schema(example = "80010-000") public String cep;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) public LocalDate dataCadastro;

    public static ClienteDTO de(Cliente c) {
        ClienteDTO d = new ClienteDTO();
        d.id = c.getId();
        d.nome = c.getNome();
        d.email = c.getEmail();
        d.cpf = c.getCpf();
        d.dataNascimento = c.getDataNascimento();
        d.dataCadastro = c.getDataCadastro();
        Endereco e = c.getEndereco();
        if (e != null) {
            d.logradouro = e.getLogradouro();
            d.numero = e.getNumero();
            d.bairro = e.getBairro();
            d.cidade = e.getCidade();
            d.estado = e.getEstado();
            d.cep = e.getCep();
        }
        return d;
    }

    public void aplicarEm(Cliente c) {
        c.setNome(nome);
        c.setEmail(email);
        c.setCpf(cpf);
        c.setDataNascimento(dataNascimento);
        c.setEndereco(new Endereco(logradouro, numero, bairro, cidade, estado, cep));
    }
}
