package controller;

import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Movimento;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.Produto;

public class ProdutoJpaController implements Serializable {

    public ProdutoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Produto produto) {
        if (produto.getMovimentoCollection() == null) {
            produto.setMovimentoCollection(new ArrayList<>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();

            produto.setMovimentoCollection(attachEntities(em, produto.getMovimentoCollection(), Movimento.class, Movimento::getMovimentoID));
            em.persist(produto);
            updateMovimentoRelationships(em, produto.getMovimentoCollection(), produto);
            
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Produto produto) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produto persistentProduto = em.find(Produto.class, produto.getProdutoID());
            if (persistentProduto == null) {
                throw new NonexistentEntityException("The produto with id " + produto.getProdutoID() + " no longer exists.");
            }

            Collection<Movimento> movimentoCollectionOld = persistentProduto.getMovimentoCollection();
            Collection<Movimento> movimentoCollectionNew = produto.getMovimentoCollection();

            movimentoCollectionNew = attachEntities(em, movimentoCollectionNew, Movimento.class, Movimento::getMovimentoID);
            produto.setMovimentoCollection(movimentoCollectionNew);
            produto = em.merge(produto);

            updateMovimentoRelationships(em, movimentoCollectionOld, movimentoCollectionNew, produto);

            em.getTransaction().commit();
        } catch (NonexistentEntityException ex) {
            if (ex instanceof NonexistentEntityException) {
                throw ex;
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = produto.getProdutoID();
                if (findProduto(id) == null) {
                    throw new NonexistentEntityException("The produto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Produto produto = findProdutoOrThrow(em, id);
            
            dissociateMovimentos(em, produto.getMovimentoCollection());

            em.remove(produto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Produto> findProdutoEntities() {
        return findProdutoEntities(true, -1, -1);
    }

    public List<Produto> findProdutoEntities(int maxResults, int firstResult) {
        return findProdutoEntities(false, maxResults, firstResult);
    }

    private List<Produto> findProdutoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Produto.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Produto findProduto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProdutoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(Produto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    private <T, ID> Collection<T> attachEntities(EntityManager em, Collection<T> entities, Class<T> clazz, Function<T, ID> getIdFunction) {
        Collection<T> attachedEntities = new ArrayList<>();
        for (T entity : entities) {
            T attachedEntity = em.getReference(clazz, getIdFunction.apply(entity));
            attachedEntities.add(attachedEntity);
        }
        return attachedEntities;
    }

    private void updateMovimentoRelationships(EntityManager em, Collection<Movimento> movimentos, Produto newProduto) {
        for (Movimento movimento : movimentos) {
            Produto oldProduto = movimento.getProdutoID();
            movimento.setProdutoID(newProduto);
            movimento = em.merge(movimento);
            if (oldProduto != null && !oldProduto.equals(newProduto)) {
                oldProduto.getMovimentoCollection().remove(movimento);
                em.merge(oldProduto);
            }
        }
    }
    
    private void updateMovimentoRelationships(EntityManager em, Collection<Movimento> oldMovimentos, Collection<Movimento> newMovimentos, Produto newProduto) {
        for (Movimento oldMovimento : oldMovimentos) {
            if (!newMovimentos.contains(oldMovimento)) {
                oldMovimento.setProdutoID(null);
                em.merge(oldMovimento);
            }
        }
        for (Movimento newMovimento : newMovimentos) {
            if (!oldMovimentos.contains(newMovimento)) {
                Produto oldProduto = newMovimento.getProdutoID();
                newMovimento.setProdutoID(newProduto);
                newMovimento = em.merge(newMovimento);
                if (oldProduto != null && !oldProduto.equals(newProduto)) {
                    oldProduto.getMovimentoCollection().remove(newMovimento);
                    em.merge(oldProduto);
                }
            }
        }
    }
    
    private Produto findProdutoOrThrow(EntityManager em, Integer id) throws NonexistentEntityException {
        try {
            Produto produto = em.getReference(Produto.class, id);
            produto.getProdutoID();
            return produto;
        } catch (EntityNotFoundException enfe) {
            throw new NonexistentEntityException("O produto " + id + " não existe.", enfe);
        }
    }

    private void dissociateMovimentos(EntityManager em, Collection<Movimento> movimentoCollection) {
        for (Movimento movimento : movimentoCollection) {
            movimento.setProdutoID(null);
            em.merge(movimento);
        }
    }
    
}
