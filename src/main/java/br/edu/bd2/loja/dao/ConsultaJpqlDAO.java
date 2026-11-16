package br.edu.bd2.loja.dao;

import br.edu.bd2.loja.modelo.Cliente;
import br.edu.bd2.loja.modelo.Pedido;
import br.edu.bd2.loja.modelo.Produto;
import br.edu.bd2.loja.modelo.StatusPedido;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.NoResultException;

public class ConsultaJpqlDAO extends GenericDAO<Object, Long> {

    private static final String VALOR_ITEM = "(i.quantidade * i.precoUnitario - i.desconto)";

    public ConsultaJpqlDAO() {
        super(Object.class);
    }

    public List<Object[]> pedidosPorEstado(String uf, int limite) {
        return executarSemTransacao(em -> em.createQuery(
                "SELECT c.nome, p.id, p.dataHora, pg.forma, pg.valor "
              + "FROM Pedido p JOIN p.cliente c JOIN p.pagamento pg "
              + "WHERE c.endereco.estado = :uf ORDER BY p.dataHora DESC", Object[].class)
                .setParameter("uf", uf.toUpperCase())
                .setMaxResults(limite)
                .getResultList());
    }

    public Pedido pedidoComItens(Long id) {
        return executarSemTransacao(em -> {
            try {
                return em.createQuery(
                        "SELECT DISTINCT p FROM Pedido p JOIN FETCH p.itens i JOIN FETCH i.produto "
                      + "JOIN FETCH p.cliente WHERE p.id = :id", Pedido.class)
                        .setParameter("id", id)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        });
    }

    public List<Object[]> totalPorCliente(BigDecimal minimo, int limite) {
        return executarSemTransacao(em -> em.createNamedQuery("Pedido.totalPorCliente", Object[].class)
                .setParameter("minimo", minimo)
                .setMaxResults(limite)
                .getResultList());
    }

    public List<Produto> produtosAcimaDaMedia(int limite) {
        return executarSemTransacao(em -> em.createNamedQuery("Produto.acimaDaMedia", Produto.class)
                .setMaxResults(limite)
                .getResultList());
    }

    public List<Produto> produtosPorCategoria(String categoria, int pagina, int tamanho) {
        return executarSemTransacao(em -> em.createQuery(
                "SELECT p FROM Produto p JOIN p.categoria c WHERE c.nome = :categoria ORDER BY p.preco DESC", Produto.class)
                .setParameter("categoria", categoria)
                .setFirstResult((Math.max(pagina, 1) - 1) * tamanho)
                .setMaxResults(tamanho)
                .getResultList());
    }

    public List<Object[]> faturamentoMensal(int ano) {
        return executarSemTransacao(em -> em.createQuery(
                "SELECT month(p.dataHora), COUNT(DISTINCT p.id), SUM" + VALOR_ITEM + " "
              + "FROM Pedido p JOIN p.itens i "
              + "WHERE year(p.dataHora) = :ano AND p.status <> :cancelado "
              + "GROUP BY month(p.dataHora) ORDER BY month(p.dataHora)", Object[].class)
                .setParameter("ano", ano)
                .setParameter("cancelado", StatusPedido.CANCELADO)
                .getResultList());
    }

    public List<Object[]> topProdutosVendidos(int n) {
        return executarSemTransacao(em -> em.createQuery(
                "SELECT pr.nome, SUM(i.quantidade) FROM ItemPedido i JOIN i.produto pr JOIN i.pedido p "
              + "WHERE p.status <> :cancelado GROUP BY pr.id, pr.nome ORDER BY SUM(i.quantidade) DESC", Object[].class)
                .setParameter("cancelado", StatusPedido.CANCELADO)
                .setMaxResults(n)
                .getResultList());
    }

    public List<Cliente> clientesSemPedidos() {
        return executarSemTransacao(em -> em.createQuery(
                "SELECT c FROM Cliente c WHERE NOT EXISTS (SELECT p.id FROM Pedido p WHERE p.cliente = c) ORDER BY c.nome",
                Cliente.class).getResultList());
    }

    public List<Object[]> faturamentoPorLoja(long minimoPedidos) {
        return executarSemTransacao(em -> em.createQuery(
                "SELECT l.nome, l.endereco.cidade, COUNT(DISTINCT p.id), SUM" + VALOR_ITEM + " "
              + "FROM Pedido p JOIN p.loja l JOIN p.itens i "
              + "WHERE p.status <> :cancelado "
              + "GROUP BY l.nome, l.endereco.cidade "
              + "HAVING COUNT(DISTINCT p.id) > :minimo "
              + "ORDER BY SUM" + VALOR_ITEM + " DESC", Object[].class)
                .setParameter("cancelado", StatusPedido.CANCELADO)
                .setParameter("minimo", minimoPedidos)
                .getResultList());
    }

    public List<Object[]> resumoPorFormaPagamento() {
        return executarSemTransacao(em -> em.createQuery(
                "SELECT pg.forma, COUNT(p), AVG(pg.valor) FROM Pedido p JOIN p.pagamento pg "
              + "GROUP BY pg.forma ORDER BY COUNT(p) DESC", Object[].class)
                .getResultList());
    }
}
