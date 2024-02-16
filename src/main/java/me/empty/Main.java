package me.empty;

import lombok.SneakyThrows;
import me.empty.service.AppService;
import util.EntityManagerUtil;
import util.RedisUtil;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {

        var service = new AppService();

        service.runTest();
//        service.testCriteriaQueryGetCitiesFromCountry(159L);

        EntityManagerUtil.shutdown();
        RedisUtil.shutdown();
    }

}