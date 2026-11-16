package br.edu.bd2.loja.dao;

import br.edu.bd2.loja.modelo.Cliente;
import br.edu.bd2.loja.modelo.FormaPagamento;
import br.edu.bd2.loja.modelo.ItemPedido;
import br.edu.bd2.loja.modelo.Loja;
import br.edu.bd2.loja.modelo.Pagamento;
import br.edu.bd2.loja.modelo.Pedido;
import br.edu.bd2.loja.modelo.Produto;
import br.edu.bd2.loja.modelo.StatusPagamento;
import br.edu.bd2.loja.modelo.StatusPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class PedidoDAO extends GenericDAO<Pedido, Long> {

    public PedidoDAO() {
        super(Pedido.class);
    }

    public Pedido registrar(Long clienteId, Long lojaId, Map<Long, Integer> itens, FormaPagamento forma, int parcelas) {
        return executarEmTransacao(em -> {
            Cliente cliente = em.find(Cliente.class, clienteId);
            Loja loja = em.find(Loja.class, lojaId);
            if (cliente == null || loja == null) {
                throw new IllegalArgumentException("Cliente ou loja inexistente");
            }
            if (itens.isEmpty()) {
                throw new IllegalArgumentException("O pedido precisa de ao menos um item");
            }
            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setLoja(loja);
            pedido.setDataHora(LocalDateTime.now());
            pedido.setStatus(StatusPedido.PAGO);

            for (Map.Entry<Long, Integer> e : itens.entrySet()) {
                Produto produto = em.find(Produto.class, e.getKey());
                if (produto == null) {
                    throw new IllegalArgumentException("Produto inexistente: " + e.getKey());
                }
                if (produto.getEstoque() < e.getValue()) {
                    throw new IllegalArgumentException("Estoque insuficiente para " + produto.getNome());
                }
                produto.setEstoque(produto.getEstoque() - e.getValue());
                ItemPedido item = new ItemPedido();
                item.setProduto(produto);
                item.setQuantidade(e.getValue());
                item.setPrecoUnitario(produto.getPreco());
                pedido.adicionarItem(item);
            }
            BigDecimal itensTotal = pedido.getValorItens();
            pedido.setValorFrete(itensTotal.compareTo(new BigDecimal("500")) > 0 ? BigDecimal.ZERO : new BigDecimal("29.90"));

            Pagamento pagamento = new Pagamento();
            pagamento.setForma(forma);
            pagamento.setParcelas(forma == FormaPagamento.CARTAO_CREDITO ? Math.max(parcelas, 1) : 1);
            pagamento.setStatus(StatusPagamento.APROVADO);
            pagamento.setValor(pedido.getValorTotal());
            pagamento.setDataPagamento(pedido.getDataHora());
            pedido.setPagamento(pagamento);

            em.persist(pedido);
            return pedido;
        });
    }
}
