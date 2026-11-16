package br.edu.bd2.loja.dao;

import br.edu.bd2.loja.modelo.Cliente;
import java.util.List;
import javax.persistence.NoResultException;

public class ClienteDAO extends GenericDAO<Cliente, Long> {

    public ClienteDAO() {
        super(Cliente.class);
    }

    public Cliente buscarPorEmail(String email) {
        return executarSemTransacao(em -> {
            try {
                return em.createNamedQuery("Cliente.porEmail", Cliente.class)
                        .setParameter("email", email)
                        .getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        });
    }

    public List<Cliente> buscarPorNome(String trecho, int pagina, int tamanho) {
        return executarSemTransacao(em ->
                em.createQuery("SELECT c FROM Cliente c WHERE LOWER(c.nome) LIKE :nome ORDER BY c.nome", Cliente.class)
                        .setParameter("nome", "%" + trecho.toLowerCase() + "%")
                        .setFirstResult((Math.max(pagina, 1) - 1) * tamanho)
                        .setMaxResults(tamanho)
                        .getResultList());
    }
}
