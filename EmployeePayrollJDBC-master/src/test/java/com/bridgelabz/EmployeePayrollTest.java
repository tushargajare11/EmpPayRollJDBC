package com.bridgelabz;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bridgelabz.EmployeePayrollData;
import com.bridgelabz.EmployeePayrollException;
import com.bridgelabz.EmployeePayrollService;
import static com.bridgelabz.EmployeePayrollService.IOService.DATABASE_IO;

public class EmployeePayrollTest {
	@Test
	public void givenEmployeePayrollData_WhenRetrieved_ShouldMatchNumberOfEmployees() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DATABASE_IO);
		Assert.assertEquals(3, employeePayrollData.size());
	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDatabase() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DATABASE_IO);
		employeePayrollService.updateEmployeeSalary("Charlie", 30000);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDatabase("Charlie");
		Assert.assertTrue(result);
	}

	@Test
	public void givenEmployeePayrollData_ShouldNumberOfEmployeesWithinDateRange() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(DATABASE_IO,
				"2018-01-01", "2022-01-26");
		Assert.assertEquals(3, employeePayrollData.size());
	}

	@Test
	public void givenEmployeePayrollData_ShouldReturnNumberOfFemaleEmployeeSalaries() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		Assert.assertEquals(1, employeePayrollService.readEmployeePayrollData("COUNT", "F"));
	}

	@Test
	public void givenEmployeePayrollData_ShouldReturnNumberOfMaleEmployeeSalaries() throws EmployeePayrollException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		Assert.assertEquals(2, employeePayrollService.readEmployeePayrollData("COUNT", "M"));
	}
}
