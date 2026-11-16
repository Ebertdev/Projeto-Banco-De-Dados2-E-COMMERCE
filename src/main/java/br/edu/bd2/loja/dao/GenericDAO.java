package br.edu.bd2.loja.dao;

import java.util.List;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class GenericDAO<T, ID> {

    protected final Class<T> classe;

    public GenericDAO(Class<T> classe) {
        this.classe = classe;
    }

    public T salvar(T entidade) {
        return executarEmTransacao(em -> {
            em.persist(entidade);
            return entidade;
        });
    }

    public T atualizar(T entidade) {
        return executarEmTransacao(em -> em.merge(entidade));
    }

    public boolean remover(ID id) {
        return executarEmTransacao(em -> {
            T entidade = em.find(classe, id);
            if (entidade == null) {
                return false;
            }
            em.remove(entidade);
            return true;
        });
    }

    public T buscarPorId(ID id) {
        return executarSemTransacao(em -> em.find(classe, id));
    }

    public List<T> listarTodos() {
        return executarSemTransacao(em ->
                em.createQuery("SELECT e FROM " + classe.getSimpleName() + " e", classe).getResultList());
    }

    public List<T> listarPaginado(int pagina, int tamanho) {
        return executarSemTransacao(em ->
                em.createQuery("SELECT e FROM " + classe.getSimpleName() + " e ORDER BY e.id", classe)
                        .setFirstResult((Math.max(pagina, 1) - 1) * tamanho)
                        .setMaxResults(tamanho)
                        .getResultList());
    }

    public long contar() {
        return executarSemTransacao(em ->
                em.createQuery("SELECT COUNT(e) FROM " + classe.getSimpleName() + " e", Long.class).getSingleResult());
    }

    protected <R> R executarEmTransacao(Function<EntityManager, R> acao) {
        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R resultado = acao.apply(em);
            tx.commit();
            return resultado;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    protected <R> R executarSemTransacao(Function<EntityManager, R> acao) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return acao.apply(em);
        } finally {
            em.close();
        }
    }
}
