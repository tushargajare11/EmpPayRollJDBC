package com.bridgelabz;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDatabaseService {
	private PreparedStatement employeePayrollPreparedStatement;

	private static EmployeePayrollDatabaseService employeePayrollDatabaseService;

	public static EmployeePayrollDatabaseService getInstance() {
		if (employeePayrollDatabaseService == null)
			employeePayrollDatabaseService = new EmployeePayrollDatabaseService();
		return employeePayrollDatabaseService;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String username = "root";
		String password = "******";
		Connection connection = null;
		System.out.println("Connecting to database: " + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, username, password);
		System.out.println("Connection is successful: " + connection);
		return connection;
	}

	public List<EmployeePayrollData> readData(LocalDate start, LocalDate end) throws EmployeePayrollException {
		String sql = null;
		if (start != null)
			sql = String.format("SELECT * FROM employee_payroll WHERE Start BETWEEN '%s' AND '%s';",
					Date.valueOf(start), Date.valueOf(end));
		if (start == null)
			sql = "SELECT * FROM employee_payroll";
		List<EmployeePayrollData> employeePayrollData = new ArrayList();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollData = this.getEmployeePayrollData(resultSet);
		} catch (SQLException sqlException) {
			throw new EmployeePayrollException("Cannot connect to database",
					EmployeePayrollException.ExceptionType.CONNECTION_FAIL);
		}
		return employeePayrollData;
	}

	public int updateEmployeeData(String name, double salary) throws EmployeePayrollException {
		try (Connection connection = this.getConnection()) {
			String sql = String.format("UPDATE employee_payroll SET Salary = %.2f WHERE Name = '%s';", salary, name);
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			return preparedStatement.executeUpdate(sql);
		} catch (SQLException sqlException) {
			throw new EmployeePayrollException("Cannot connect to database",
					EmployeePayrollException.ExceptionType.CONNECTION_FAIL);
		}
	}

	public List<EmployeePayrollData> getEmployeePayrollData(String name) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollData = null;
		if (this.employeePayrollPreparedStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollPreparedStatement.setString(1, name);
			ResultSet resultSet = employeePayrollPreparedStatement.executeQuery();
			employeePayrollData = this.getEmployeePayrollData(resultSet);
		} catch (SQLException sqlException) {
			throw new EmployeePayrollException("Cannot connect to database",
					EmployeePayrollException.ExceptionType.CONNECTION_FAIL);
		}
		return employeePayrollData;
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) throws EmployeePayrollException {
		List<EmployeePayrollData> employeePayrollData = new ArrayList();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollData.add(new EmployeePayrollData(id, name, salary, startDate));
			}
		} catch (SQLException sqlException) {
			throw new EmployeePayrollException("Cannot execute query",
					EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY);
		}
		return employeePayrollData;
	}

	public int readData(String calculate, String gender) throws EmployeePayrollException {
		int sum = 0;
		String sql = String.format("SELECT %s(Salary) FROM employee_payroll WHERE GENDER = '%s' GROUP BY Gender;",
				calculate, gender);
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			resultSet.next();
			sum = resultSet.getInt(1);
		} catch (SQLException sqlException) {
			throw new EmployeePayrollException("Cannot connect to database",
					EmployeePayrollException.ExceptionType.CONNECTION_FAIL);
		}
		return sum;
	}

	private void prepareStatementForEmployeeData() throws EmployeePayrollException {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE Name = ?";
			employeePayrollPreparedStatement = connection.prepareStatement(sql);
		} catch (SQLException sqlException) {
			throw new EmployeePayrollException("Cannot execute query",
					EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY);
		}
	}
}
