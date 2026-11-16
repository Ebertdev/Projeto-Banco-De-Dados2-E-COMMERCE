package br.edu.bd2.loja.dao;

import br.edu.bd2.loja.modelo.Produto;
import java.util.List;
import javax.persistence.NoResultException;

public class ProdutoDAO extends GenericDAO<Produto, Long> {

    public ProdutoDAO() {
        super(Produto.class);
    }

    public List<Produto> listarComCategoria(int pagina, int tamanho) {
        return executarSemTransacao(em ->
                em.createQuery("SELECT p FROM Produto p JOIN FETCH p.categoria ORDER BY p.id", Produto.class)
                        .setFirstResult((Math.max(pagina, 1) - 1) * tamanho)
                        .setMaxResults(tamanho)
                        .getResultList());
    }

    public Produto buscarComCategoria(Long id) {
        return executarSemTransacao(em -> {
            try {
                return em.createQuery("SELECT p FROM Produto p JOIN FETCH p.categoria WHERE p.id = :id", Produto.class)
                        .setParameter("id", id)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        });
    }
}
