package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.PessoaJuridica;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import model.PessoaFisica;
import model.Movimento;
import model.Pessoa;

public class PessoaJpaController implements Serializable {

    public PessoaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pessoa pessoa) {
        if (pessoa.getPessoaJuridicaCollection() == null) {
            pessoa.setPessoaJuridicaCollection(new ArrayList<>());
        }
        if (pessoa.getPessoaFisicaCollection() == null) {
            pessoa.setPessoaFisicaCollection(new ArrayList<>());
        }
        if (pessoa.getMovimentoCollection() == null) {
            pessoa.setMovimentoCollection(new ArrayList<>());
        }

        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();

            pessoa.setPessoaJuridicaCollection(attachEntities(em, pessoa.getPessoaJuridicaCollection(), PessoaJuridica.class, PessoaJuridica::getPessoaID));

            pessoa.setPessoaFisicaCollection(attachEntities(em, pessoa.getPessoaFisicaCollection(), PessoaFisica.class, PessoaFisica::getPessoaID));

            pessoa.setMovimentoCollection(attachEntities(em, pessoa.getMovimentoCollection(), Movimento.class, Movimento::getMovimentoID));

            em.persist(pessoa);

            updateRelationships(em, pessoa.getPessoaJuridicaCollection(), PessoaJuridica::getPessoaPessoaID, PessoaJuridica::setPessoaPessoaID, pessoa);
            updateRelationships(em, pessoa.getPessoaFisicaCollection(), PessoaFisica::getPessoaPessoaID, PessoaFisica::setPessoaPessoaID, pessoa);
            updateRelationships(em, pessoa.getMovimentoCollection(), Movimento::getPessoaID, Movimento::setPessoaID, pessoa);

            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pessoa pessoa) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();

            Pessoa persistentPessoa = em.find(Pessoa.class, pessoa.getPessoaID());
            if (persistentPessoa == null) {
                throw new NonexistentEntityException("The pessoa with id " + pessoa.getPessoaID() + " no longer exists.");
            }

            Collection<PessoaJuridica> pessoaJuridicaCollectionOld = persistentPessoa.getPessoaJuridicaCollection();
            Collection<PessoaJuridica> pessoaJuridicaCollectionNew = pessoa.getPessoaJuridicaCollection();
            Collection<PessoaFisica> pessoaFisicaCollectionOld = persistentPessoa.getPessoaFisicaCollection();
            Collection<PessoaFisica> pessoaFisicaCollectionNew = pessoa.getPessoaFisicaCollection();
            Collection<Movimento> movimentoCollectionOld = persistentPessoa.getMovimentoCollection();
            Collection<Movimento> movimentoCollectionNew = pessoa.getMovimentoCollection();

            List<String> illegalOrphanMessages = validateOrphans(pessoaJuridicaCollectionOld, pessoaJuridicaCollectionNew, "PessoaJuridica");
            illegalOrphanMessages.addAll(validateOrphans(pessoaFisicaCollectionOld, pessoaFisicaCollectionNew, "PessoaFisica"));

            if (illegalOrphanMessages != null && !illegalOrphanMessages.isEmpty()) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }

            pessoa.setPessoaJuridicaCollection(attachEntities(em, pessoaJuridicaCollectionNew, PessoaJuridica.class, PessoaJuridica::getPessoaID));
            pessoa.setPessoaFisicaCollection(attachEntities(em, pessoaFisicaCollectionNew, PessoaFisica.class, PessoaFisica::getPessoaID));
            pessoa.setMovimentoCollection(attachEntities(em, movimentoCollectionNew, Movimento.class, Movimento::getMovimentoID));

            pessoa = em.merge(pessoa);

            updateRelationships(em, pessoa.getPessoaJuridicaCollection(), PessoaJuridica::getPessoaPessoaID, PessoaJuridica::setPessoaPessoaID, pessoa);
            updateRelationships(em, pessoa.getPessoaFisicaCollection(), PessoaFisica::getPessoaPessoaID, PessoaFisica::setPessoaPessoaID, pessoa);
            updateMovimentoRelationships(em, movimentoCollectionOld, movimentoCollectionNew, pessoa);

            em.getTransaction().commit();
        } catch (Exception ex) {
            handleException(pessoa, ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pessoa pessoa;
            try {
                pessoa = em.getReference(Pessoa.class, id);
                pessoa.getPessoaID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("Pessoa não existe: " + id, enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<PessoaJuridica> pessoaJuridicaCollectionOrphanCheck = pessoa.getPessoaJuridicaCollection();
            for (PessoaJuridica pessoaJuridicaCollectionOrphanCheckPessoaJuridica : pessoaJuridicaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("A pessoa (" + pessoa + ") não pode ser deletada, por que a PessoaJuridica" + pessoaJuridicaCollectionOrphanCheckPessoaJuridica + " possui o campo pessoaJuridicaCollection que não pode ser nulo.");
            }
            Collection<PessoaFisica> pessoaFisicaCollectionOrphanCheck = pessoa.getPessoaFisicaCollection();
            for (PessoaFisica pessoaFisicaCollectionOrphanCheckPessoaFisica : pessoaFisicaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("A pessoa (" + pessoa + ") não pode ser deletada, por que a PessoaFisica " + pessoaFisicaCollectionOrphanCheckPessoaFisica + " possui o campo pessoaFisicaCollection que não pode ser nulo.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Movimento> movimentoCollection = pessoa.getMovimentoCollection();
            for (Movimento movimentoCollectionMovimento : movimentoCollection) {
                movimentoCollectionMovimento.setPessoaID(null);
                movimentoCollectionMovimento = em.merge(movimentoCollectionMovimento);
            }
            em.remove(pessoa);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pessoa> findPessoaEntities() {
        return findPessoaEntities(true, -1, -1);
    }

    public List<Pessoa> findPessoaEntities(int maxResults, int firstResult) {
        return findPessoaEntities(false, maxResults, firstResult);
    }

    private List<Pessoa> findPessoaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pessoa.class));
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

    public Pessoa findPessoa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pessoa.class, id);
        } finally {
            em.close();
        }
    }

    public int getPessoaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pessoa> rt = cq.from(Pessoa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }


    private List<String> validateOrphans(Collection<?> oldCollection, Collection<?> newCollection, String entityName) {
        List<String> orphanMessages = new ArrayList<>();
        for (Object oldEntity : oldCollection) {
            if (!newCollection.contains(oldEntity)) {
                orphanMessages.add("Você deve manter " + entityName + " " + oldEntity + " pois pessoaPessoaID não pode ser nulo.");
            }
        }
        return orphanMessages;
    }

    private <T, ID> Collection<T> attachEntities(EntityManager em, Collection<T> entities, Class<T> clazz, Function<T, ID> getIdFunction) {
        Collection<T> attachedEntities = new ArrayList<>();
        for (T entity : entities) {
            T attachedEntity = em.getReference(clazz, getIdFunction.apply(entity));
            attachedEntities.add(attachedEntity);
        }
        return attachedEntities;
    }

    private <T> void updateRelationships(EntityManager em, Collection<T> entities, Function<T, Pessoa> getOldPessoaFunction, BiConsumer<T, Pessoa> setPessoaFunction, Pessoa newPessoa) {
        for (T entity : entities) {
            Pessoa oldPessoa = getOldPessoaFunction.apply(entity);
            setPessoaFunction.accept(entity, newPessoa);
            entity = em.merge(entity);
            if (oldPessoa != null && !oldPessoa.equals(newPessoa)) {
                oldPessoa.getPessoaJuridicaCollection().remove(entity);
                em.merge(oldPessoa);
            }
        }
    }

    private void updateMovimentoRelationships(EntityManager em, Collection<Movimento> oldMovimentos, Collection<Movimento> newMovimentos, Pessoa newPessoa) {
        for (Movimento oldMovimento : oldMovimentos) {
            if (!newMovimentos.contains(oldMovimento)) {
                oldMovimento.setPessoaID(null);
                em.merge(oldMovimento);
            }
        }
        for (Movimento newMovimento : newMovimentos) {
            if (!oldMovimentos.contains(newMovimento)) {
                Pessoa oldPessoa = newMovimento.getPessoaID();
                newMovimento.setPessoaID(newPessoa);
                newMovimento = em.merge(newMovimento);
                if (oldPessoa != null && !oldPessoa.equals(newPessoa)) {
                    oldPessoa.getMovimentoCollection().remove(newMovimento);
                    em.merge(oldPessoa);
                }
            }
        }
    }
    
    private void handleException(Pessoa pessoa, Exception ex) throws Exception {
        String msg = ex.getLocalizedMessage();
        if (msg == null || msg.length() == 0) {
            Integer id = pessoa.getPessoaID();
            if (findPessoa(id) == null) {
                throw new NonexistentEntityException("A pessoa não existe mais " + id);
            }
        }
        throw ex;
    }
    
}
