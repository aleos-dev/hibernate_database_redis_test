package me.empty.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import me.empty.dao.CityDao;
import me.empty.entity.City;
import me.empty.entity.Country;
import me.empty.redis.CityCountry;
import me.empty.redis.Language;
import util.EntityManagerUtil;
import util.RedisUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class AppService {

    private static final int CHUNK_SIZE = 500;

    public void runTest() {


        List<City> cities = fetchCities();
        var preparedData = transformData(cities);

        RedisUtil.pushToRedis(preparedData);

        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        testRedisData(ids).forEach(System.out::println);
        long stopRedis = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));

        long startMysql = System.currentTimeMillis();
        testMysqlData(ids).forEach(System.out::println);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));
    }

    public void testCriteriaQueryGetCitiesFromCountry(Long countryCode) {

        List<City> citiesByCriteria = new CityDao(EntityManagerUtil.getEntityManager())
                .findCitiesByCriteria(countryCode, null);

        citiesByCriteria.forEach(System.out::println);
        System.out.println("Cities by criteria count = " + citiesByCriteria.size());
    }


    public List<City> fetchCities() {
        return EntityManagerUtil.callInTXContext(this::fetchDataInChunks);
    }

    public List<CityCountry> transformData(List<City> cities) {

        return cities.stream().map(city -> {
            CityCountry res = new CityCountry();
            res.setId(city.getId());
            res.setName(city.getName());
            res.setPopulation(city.getPopulation());
            res.setDistrict(city.getDistrict());

            Country country = city.getCountry();
            res.setAlternativeCountryCode(country.getCode2());
            res.setContinent(country.getContinent());
            res.setCountryCode(country.getCode());
            res.setCountryName(country.getName());
            res.setCountryPopulation(country.getPopulation());
            res.setCountryRegion(country.getRegion());
            res.setCountrySurfaceArea(country.getSurfaceArea());

            Set<Language> languages = country.getLanguages().stream()
                    .map(this::remapLang)
                    .collect(Collectors.toSet());

            res.setLanguages(languages);

            return res;

        }).collect(Collectors.toList());
    }

    private Language remapLang(me.empty.entity.Language cl) {
        Language language = new Language();

        language.setLanguage(cl.getName());
        language.setIsOfficial(cl.getInfo().isOfficial());
        language.setPercentage(cl.getInfo().getPercentage());
        return language;
    }

    private List<City> fetchDataInChunks(EntityManager em) {

        List<City> cities = new ArrayList<>();
        CityDao cityDao = new CityDao(em);

        int totalCount = cityDao.getTotalCount();
        for (int i = 0; i < totalCount; i += CHUNK_SIZE) {
            cities.addAll(cityDao.getItems(i, CHUNK_SIZE));
        }

        return cities;
    }

    private List<City> testMysqlData(List<Integer> ids) {

        return EntityManagerUtil.callInTXContext(em -> {

            String sql = """
                    select distinct city
                    from City city
                    join fetch city.country as country
                    left join fetch country.languages as languages
                    where city.id in :cityIds
                    """;
            TypedQuery<City> query = em.createQuery(sql, City.class);
            query.setParameter("cityIds", ids);

            return query.getResultList();
        });
    }

    private List<CityCountry> testRedisData(List<Integer> ids) {

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<CityCountry> cityCountryList = new ArrayList<>();

        try (StatefulRedisConnection<String, String> connection = RedisUtil.getClient().connect()) {

            RedisStringCommands<String, String> sync = connection.sync();

            for (Integer id : ids) {

                String value = sync.get(String.valueOf(id));

                try {
                    cityCountryList.add(mapper.readValue(value, CityCountry.class));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }

        return cityCountryList;
    }
}
