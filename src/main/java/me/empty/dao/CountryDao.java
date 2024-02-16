package me.empty.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import me.empty.entity.Country;

import java.util.List;

public class CountryDao {

    private final EntityManager em;

    public CountryDao(EntityManager em) {
        this.em = em;
    }

    public List<Country> getAll() {

        TypedQuery<Country> query = em.createQuery("select c from Country c join fetch c.capital join fetch c.languages", Country.class);

        return query.getResultList();
    }
}
