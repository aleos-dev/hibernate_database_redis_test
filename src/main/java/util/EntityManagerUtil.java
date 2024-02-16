package util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Objects.nonNull;

public class EntityManagerUtil {

    private static final String PERSISTENCE_UNIT_NAME = "default";
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = initEntityManagerFactory();

    private static EntityManagerFactory initEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return ENTITY_MANAGER_FACTORY;
    }

    public static EntityManager getEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public static <T> T callInTXContext(Function<EntityManager, T> func) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T callback = func.apply(em);
            em.getTransaction().commit();

            return callback;
        } catch (Exception e) {
            em.getTransaction().rollback();
            String errorMessage = "Error performing call in TX context: " + e.getMessage();
            throw new RuntimeException(errorMessage, e);
        } finally {
            em.close();
        }
    }

    public static void runInTxContext(Consumer<EntityManager> consumer) {

        EntityManager em = getEntityManager();
        try {

            em.getTransaction().begin();
            consumer.accept(em);
            em.getTransaction().commit();

        } catch (Exception e) {
            em.getTransaction().rollback();
            String errorMessage = "Error performing run in TX context: " + e.getMessage();
            throw new RuntimeException(errorMessage, e);
        } finally {
            em.close();
        }
    }

    public static void shutdown() {
        if (nonNull(ENTITY_MANAGER_FACTORY)) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }
}
