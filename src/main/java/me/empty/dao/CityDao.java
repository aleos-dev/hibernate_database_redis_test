package me.empty.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import me.empty.entity.City;

import java.util.ArrayList;
import java.util.List;

public class CityDao {

    private final EntityManager em;

    public CityDao(EntityManager em) {
        this.em = em;
    }


    public List<City> getItems(int offset, int limit) {

        TypedQuery<City> query = em.createQuery("select c from City c join fetch c.country country join fetch country.languages", City.class);

        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    public List<City> findCitiesByCriteria(Long countryCode, String district) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<City> cq = cb.createQuery(City.class);
        Root<City> city = cq.from(City.class);

        List<Predicate> predicates = new ArrayList<>();

        if (countryCode != null) {
            predicates.add(cb.equal(city.get("country").get("id"), countryCode));
        }
        if (district != null) {
            predicates.add(cb.equal(city.get("district"), district));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<City> query = em.createQuery(cq);
        return query.getResultList();
    }

    public int getTotalCount() {

        TypedQuery<Long> query = em.createQuery("select count(c) from City c", Long.class);

        return query.getSingleResult().intValue();
    }
}
