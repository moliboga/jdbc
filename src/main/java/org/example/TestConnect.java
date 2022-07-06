package org.example;

import java.sql.*;

public class TestConnect {
    public static void main(String[] args) throws SQLException {
        JDBCMain.connect();
        JDBCMain.createStudentTable();

        JDBCMain.addStudentRow("artem", "molyboha");
        JDBCMain.addStudentRow("kosbi", "korzh");
        JDBCMain.batchAddingNewStudents();
        System.out.println("-------------");

        String name = "kosbi";
        System.out.println(name + ":");
        JDBCMain.selectAllStudentsWhereName(name);
        System.out.println("Total:");
        JDBCMain.createProcedureSumOfIndexesByName();
        JDBCMain.getSumOfIndexesByNameCallable("kosbi");
        System.out.println("-------------");

        JDBCMain.selectAllStudents();
        JDBCMain.closeConnection();
    }
}