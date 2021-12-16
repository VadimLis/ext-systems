package edu.javacourse.city.dao;

import edu.javacourse.city.domain.PersonRequest;
import edu.javacourse.city.domain.PersonResponse;
import edu.javacourse.city.exception.PersonCheckException;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class PersonCheckDaoTest {

    @Test
    public void checkPerson() throws PersonCheckException {
        PersonRequest pr = new PersonRequest();

        pr.setSurName("Васильев");
        pr.setGivenName("Павел");
        pr.setPatronymic("Николаевич");
        pr.setDateOfBirth(LocalDate.of(1995, 3, 18));
        pr.setStreetCode(1);
        pr.setBuilding("10");
        pr.setExtension("2");
        pr.setApartment("121");

        PersonCheckDao dao = new PersonCheckDao();
        dao.setConnectionBuilder(new DirectConnectionBuilder());
        PersonResponse pres = dao.checkPerson(pr);
        Assert.assertTrue(pres.isRegistered());
        Assert.assertFalse(pres.isTemporal());

    }
}