import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ManageEmployee {
	private static SessionFactory factory;

	public static void main(String[] args) {
		try{ 
			factory = new Configuration().configure().buildSessionFactory(); 
		}catch (Throwable ex) { 
			System.err.println("Failed to create sessionFactory object." + ex); 
			throw new ExceptionInInitializerError(ex); 
		}
		ManageEmployee ME = new ManageEmployee(); 

		/* Let us have a set of certificates for the first employee */ 
		HashSet<Certificate> certificates = new HashSet<Certificate>();

		certificates.add(new Certificate("MCA")); 
		certificates.add(new Certificate("MBA")); 
		certificates.add(new Certificate("PMP"));

		/* Add employee records in the database */ 
		Integer empID1 = ME.addEmployee("Manoj", "Kumar", 4000, certificates); 

		/* Add another employee record in the database */ 
		Integer empID2 = ME.addEmployee("Dilip", "Kumar", 3000, certificates); 

		/* List down all the employees */ 
		ME.listEmployees();

		/* Update employee's salary records */ 
		ME.updateEmployee(empID1, 5000); 

		/* Delete an employee from the database */ 
		ME.deleteEmployee(empID2); 

		/* List down all the employees */ 
		ME.listEmployees();
		
		factory.close();
	}

	/* Method to add an employee record in the database */ 
	public Integer addEmployee(String fname, String lname, int salary, Set<Certificate> cert){ 
		Session session = factory.openSession(); 
		Transaction tx = null; 
		Integer employeeID = null;
		try{ 
			tx = session.beginTransaction(); 
			Employee employee = new Employee(fname, lname, salary); 
			employee.setCertificates(cert); 
			employeeID = (Integer) session.save(employee); 
			tx.commit(); 
		}catch (HibernateException e) { 
			if (tx!=null) 
				tx.rollback(); 
			e.printStackTrace(); 
		}finally { 
			session.close(); 
		} 
		return employeeID; 
	}

	/* Method to list all the employees detail */ 
	@SuppressWarnings("unchecked")
	public void listEmployees( ){ 
		Session session = factory.openSession();
		Transaction tx = null; 
		try{
			tx = session.beginTransaction();
			List<Employee> employees = session.createQuery("FROM Employee").list();
			for (Iterator<Employee> iterator1 = employees.iterator(); 
					iterator1.hasNext();){ 
				Employee employee = (Employee) iterator1.next();
				System.out.print("First Name: " + employee.getFirstName());
				System.out.print(" Last Name: " + employee.getLastName()); 
				System.out.println(" Salary: " + employee.getSalary()); 
				Set<Certificate> certificates = employee.getCertificates(); 
				for (Iterator<Certificate> iterator2 = certificates.iterator(); iterator2.hasNext();){ 
					Certificate certName = (Certificate) iterator2.next();
					System.out.println("Certificate: " + certName.getName());
				}
			} 
			tx.commit();
		}catch (HibernateException e) {
			if (tx!=null) 
				tx.rollback();
			e.printStackTrace();
		}finally { 
			session.close();
		}
	}

	/* Method to update salary for an employee */ 
	public void updateEmployee(Integer EmployeeID, int salary ){ 
		Session session = factory.openSession(); 
		Transaction tx = null; 
		try{ 
			tx = session.beginTransaction(); 
			Employee employee = (Employee)session.get(Employee.class, EmployeeID); 
			employee.setSalary( salary ); 
			session.update(employee); 
			tx.commit(); 
		}catch (HibernateException e) { 
			if (tx!=null) 
				tx.rollback(); 
			e.printStackTrace(); 
		}finally { 
			session.close(); 
		} 
	}

	/* Method to delete an employee from the records */ 
	public void deleteEmployee(Integer EmployeeID){ 
		Session session = factory.openSession(); 
		Transaction tx = null; 
		try{
			tx = session.beginTransaction(); 
			Employee employee = (Employee)session.get(Employee.class, EmployeeID); 
			session.delete(employee); 
			tx.commit(); 
		}catch (HibernateException e) {
			if (tx!=null) 
				tx.rollback(); 
			e.printStackTrace(); 
		}finally { 
			session.close(); 
		} 
	}
}
