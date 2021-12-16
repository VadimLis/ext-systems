package edu.javacourse.city.dao;

import edu.javacourse.city.domain.PersonRequest;
import edu.javacourse.city.domain.PersonResponse;
import edu.javacourse.city.exception.PersonCheckException;

import java.sql.*;

public class PersonCheckDao {

    public static final String SQL_REQUEST = "SELECT temporal FROM cr_address_person ap " +
            "INNER JOIN cr_person per ON per.person_id = ap.person_id " +
            "INNER JOIN cr_address adr ON adr.address_id = ap.address_id " +
            "WHERE CURRENT_DATE >= ap.start_date " +
            "and (CURRENT_DATE <= ap.end_date or ap.end_date is null) " +
            "and upper(per.sur_name) = upper(?) " +
            "and upper(per.given_name) = upper(?) " +
            "and upper(per.patronymic) = upper(?) " +
            "and per.date_of_birth = ? " +
            "and adr.street_code = ? " +
            "and upper(adr.building) = upper(?)";

    private ConnectionBuilder connectionBuilder;

    public void setConnectionBuilder(ConnectionBuilder connectionBuilder) {
        this.connectionBuilder = connectionBuilder;
    }

    private Connection getConnection() throws SQLException {
        return connectionBuilder.getConnection();
    }

    public PersonCheckDao() {
        try{
            Class.forName("org.postgresql.Driver");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public PersonResponse checkPerson (PersonRequest request) throws PersonCheckException {
        PersonResponse response = new PersonResponse();

        String sql = SQL_REQUEST;
        if (request.getExtension() != null) {
            sql += "and upper(adr.extension) = upper(?) ";
        } else {
            sql += "and adr.extension is null";
        }
        if (request.getApartment() != null) {
            sql += "and upper(adr.apartment) = upper(?)";
        } else {
            sql += "and adr.apartment is null";
        }

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement (sql)) {

            int counter = 1;
            stmt.setString(counter++, request.getSurName());
            stmt.setString(counter++, request.getGivenName());
            stmt.setString(counter++, request.getPatronymic());
            stmt.setDate(counter++, java.sql.Date.valueOf(request.getDateOfBirth()));
            stmt.setInt(counter++, request.getStreetCode());
            stmt.setString(counter++, request.getBuilding());
            if (request.getExtension() != null) {
                stmt.setString(counter++, request.getExtension());
            }
            if (request.getApartment() != null) {
                stmt.setString(counter++, request.getApartment());
            }

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

}
