package edu.javacourse.city.dao;

import edu.javacourse.city.domain.PersonRequest;
import edu.javacourse.city.domain.PersonResponse;
import edu.javacourse.city.exception.PersonCheckException;

import java.sql.*;

public class PersonCheckDao {

    public static final String SQL_REQUEST = "SELECT temporal FROM cr_address_person ap " +
            "INNER JOIN cr_person per ON per.person_id = ap.person_id " +
            "INNER JOIN cr_address adr ON adr.address_id = ap.address_id " +
            "WHERE upper(per.sur_name) = upper(?) " +
            "and upper(per.given_name) = upper(?) " +
            "and upper(per.patronymic) = upper(?) " +
            "and per.date_of_birth = ? " +
            "and adr.street_code = ? " +
            "and upper(adr.building) = upper(?) " +
            "and upper(adr.extension) = upper(?) " +
            "and upper(adr.apartment) = upper(?)";

    public PersonResponse checkPerson (PersonRequest request) throws PersonCheckException {
        PersonResponse response = new PersonResponse();

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement (SQL_REQUEST)) {

            stmt.setString(1, request.getSurName());
            stmt.setString(2, request.getGivenName());
            stmt.setString(3, request.getPatronymic());
            stmt.setDate(4, java.sql.Date.valueOf(request.getDateOfBirth()));
            stmt.setInt(5, request.getStreetCode());
            stmt.setString(6, request.getBuilding());
            stmt.setString(7, request.getExtension());
            stmt.setString(8, request.getApartment());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                response.setRegistered(true);
                response.setTemporal(rs.getBoolean("temporal"));

            }

        } catch (SQLException ex) {
            throw new PersonCheckException(ex);
        }

        return response;
    }

    private Connection getConnection() throws SQLException {

        return DriverManager.getConnection("jdbc:postgresql://localhost/city_register", "postgres",
                "vadomlis1996");
    }
}
