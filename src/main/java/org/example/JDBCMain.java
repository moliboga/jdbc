package org.example;

import java.sql.*;
import java.util.Arrays;

public class JDBCMain {

    private static final String url = "jdbc:mysql://localhost:3306/jdbc";
    private static final String user = "root";
    private static final String password = "135246";
    private static Connection conn;

    public static void connect() throws SQLException {
        conn = DriverManager.getConnection(url, user, password);
    }

    public static void createStudentTable(){
        String sql = "CREATE TABLE IF NOT EXISTS " + "student" + " (\n" +
                "    PersonID int AUTO_INCREMENT,\n" +
                "    FirstName varchar(255),\n" +
                "    LastName varchar(255),\n" +
                "    PRIMARY KEY (PersonID)\n" +
                ");";
        try (Statement ps = conn.createStatement()){
            ps.executeUpdate(sql);
            System.out.println("Table created");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection() throws SQLException {
        conn.close();
    }

    public static void addStudentRow(String firstName, String lastName) throws SQLException {
        String sql = "INSERT INTO student (FirstName, LastName) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.executeUpdate();
        }
    }

    public static void selectAllStudents() throws SQLException {
        String sql = "SELECT * FROM student";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("personId");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("lastname");
                System.out.println(id + " " + firstName + " " + lastName);
            }
            resultSet.close();
        }
    }

    public static void selectAllStudentsWhereName(String name) throws SQLException {
        String sql = "SELECT * FROM student WHERE firstName = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, name);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("personId");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("lastname");
                System.out.println(id + " " + firstName + " " + lastName);
            }
            resultSet.close();
        }
    }

    public static void getSumOfIndexesByNameCallable(String name) throws SQLException {
        var sql = "{call get_sum_of_indexes_by_name(?)}";
        try (var cs = conn.prepareCall(sql)) {
            cs.setString("name", name);
            try (var rs = cs.executeQuery()) {
                if (rs.next()) {
                    System.out.println(rs.getInt(1));
                }
            }
        }
    }

    public static void createProcedureSumOfIndexesByName() throws SQLException {
        String sql = "CREATE PROCEDURE IF NOT EXISTS get_sum_of_indexes_by_name(INOUT name varchar(255))\n" +
                "    BEGIN\n" +
                "    select sum(PersonID) from student where FirstName=name;\n" +
                "    END";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            ps.executeUpdate();
        }
    }

    public static void batchAddingNewStudents() throws SQLException {
        String sql = "INSERT INTO student (FirstName, LastName) VALUES(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (int i = 0; i < 10; i++){
                ps.setString(1, String.valueOf(i));
                ps.setString(2, "NumberedStudent");
                ps.addBatch();
            }
            int[] batchRes = ps.executeBatch();
            if (batchRes.length == 10)
                conn.commit();
            else
                throw new RuntimeException("Wrong batch size");
        } catch (RuntimeException e){
            conn.rollback();
        }
    }
}
