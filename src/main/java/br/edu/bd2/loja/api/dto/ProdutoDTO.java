package br.edu.bd2.loja.api.dto;

import br.edu.bd2.loja.modelo.Produto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(name = "Produto")
public class ProdutoDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY) public Long id;
    @Schema(example = "JBL Flip 6", required = true) public String nome;
    @Schema(example = "JBL") public String marca;
    @Schema(example = "Caixa de som Bluetooth à prova d'água") public String descricao;
    @Schema(example = "799.00", required = true) public BigDecimal preco;
    @Schema(example = "120") public int estoque;
    @Schema(example = "true") public boolean ativo = true;
    @Schema(description = "Id da categoria (veja /categorias)", example = "9", required = true) public Long categoriaId;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) public String categoriaNome;

    public static ProdutoDTO de(Produto p) {
        ProdutoDTO d = new ProdutoDTO();
        d.id = p.getId();
        d.nome = p.getNome();
        d.marca = p.getMarca();
        d.descricao = p.getDescricao();
        d.preco = p.getPreco();
        d.estoque = p.getEstoque();
        d.ativo = p.isAtivo();
        d.categoriaId = p.getCategoria().getId();
        d.categoriaNome = p.getCategoria().getNome();
        return d;
    }

    public void aplicarEm(Produto p) {
        p.setNome(nome);
        p.setMarca(marca);
        p.setDescricao(descricao);
        p.setPreco(preco);
        p.setEstoque(estoque);
        p.setAtivo(ativo);
    }
}
