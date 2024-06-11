package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Pessoa;
import model.PessoaFisica;

public class PessoaFisicaJpaController implements Serializable {

    public PessoaFisicaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(PessoaFisica pessoaFisica) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            Pessoa pessoaPessoaID = pessoaFisica.getPessoaPessoaID();
            if (pessoaPessoaID != null) {
                pessoaPessoaID = em.getReference(pessoaPessoaID.getClass(), pessoaPessoaID.getPessoaID());
                pessoaFisica.setPessoaPessoaID(pessoaPessoaID);
            }
            
            em.persist(pessoaFisica);
            
            if (pessoaPessoaID != null) {
                pessoaPessoaID.getPessoaFisicaCollection().add(pessoaFisica);
                pessoaPessoaID = em.merge(pessoaPessoaID);
            }
            
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPessoaFisica(pessoaFisica.getPessoaID()) != null) {
                throw new PreexistingEntityException("PessoaFisica " + pessoaFisica + " já existte.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(PessoaFisica pessoaFisica) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            
            PessoaFisica persistentPessoaFisica = em.find(PessoaFisica.class, pessoaFisica.getPessoaID());
            Pessoa pessoaPessoaIDOld = persistentPessoaFisica.getPessoaPessoaID();
            Pessoa pessoaPessoaIDNew = pessoaFisica.getPessoaPessoaID();
            
            if (pessoaPessoaIDNew != null) {
                pessoaPessoaIDNew = em.getReference(pessoaPessoaIDNew.getClass(), pessoaPessoaIDNew.getPessoaID());
                pessoaFisica.setPessoaPessoaID(pessoaPessoaIDNew);
            }
            
            pessoaFisica = em.merge(pessoaFisica);
            
            if (pessoaPessoaIDOld != null && !pessoaPessoaIDOld.equals(pessoaPessoaIDNew)) {
                pessoaPessoaIDOld.getPessoaFisicaCollection().remove(pessoaFisica);
                pessoaPessoaIDOld = em.merge(pessoaPessoaIDOld);
            }
            
            if (pessoaPessoaIDNew != null && !pessoaPessoaIDNew.equals(pessoaPessoaIDOld)) {
                pessoaPessoaIDNew.getPessoaFisicaCollection().add(pessoaFisica);
                pessoaPessoaIDNew = em.merge(pessoaPessoaIDNew);
            }
            
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pessoaFisica.getPessoaID();
                if (findPessoaFisica(id) == null) {
                    throw new NonexistentEntityException("PessoasFisica não existe: " + id);
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
            
            PessoaFisica pessoaFisica;
            try {
                pessoaFisica = em.getReference(PessoaFisica.class, id);
                pessoaFisica.getPessoaID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("PessoasFisica não existe: " + id, enfe);
            }
            
            Pessoa pessoaPessoaID = pessoaFisica.getPessoaPessoaID();
            if (pessoaPessoaID != null) {
                pessoaPessoaID.getPessoaFisicaCollection().remove(pessoaFisica);
                pessoaPessoaID = em.merge(pessoaPessoaID);
            }
            
            em.remove(pessoaFisica);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<PessoaFisica> findPessoaFisicaEntities() {
        return findPessoaFisicaEntities(true, -1, -1);
    }

    public List<PessoaFisica> findPessoaFisicaEntities(int maxResults, int firstResult) {
        return findPessoaFisicaEntities(false, maxResults, firstResult);
    }

    private List<PessoaFisica> findPessoaFisicaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<PessoaFisica> cq = cb.createQuery(PessoaFisica.class);
            cq.select(cq.from(PessoaFisica.class));
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

    public PessoaFisica findPessoaFisica(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(PessoaFisica.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoaFisicaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<PessoaFisica> rt = cq.from(PessoaFisica.class);
            cq.select(cb.count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
