package application;

import java.util.Scanner;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class Program2 {

	public static void main(String[] args) {

		DepartmentDao departmentDao = DaoFactory.createDepartmentDao();
		Scanner sc = new Scanner(System.in);
		int numeroCase;
		char testeLogico = 'S';

		while (testeLogico != 'N') {
			System.out.println("1.  Insert new Department" + "\n2.  Find All Department" + "\n3.  Update by Id Department"
					+ "\n4.  Delete by Id Department" + "\n5.  Find by Id Department");

			System.out.print("\nWhich service do you want? ");
			numeroCase = sc.nextInt();

			switch (numeroCase) {
			case 5:
				System.out.println("\n====Find by Id Department====");
				System.out.print("Type id to search: ");
				Department department = departmentDao.findById(sc.nextInt());
				System.out.println(department);
				System.out.println("");
				break;

			case 1:
				System.out.println("\n====Insert new Department====");
				System.out.print("Enter the name of the department: ");
				String nameDep = sc.next();
				Department newDep = new Department(null, nameDep);
				departmentDao.insert(newDep);
				System.out.println("Inserted! New Id: " + newDep.getId());
				System.out.println("");
				break;

			case 3:
				System.out.println("\n====Update by Id Department====");
				System.out.print("Type id to updated: ");
				Department dep1 = departmentDao.findById(sc.nextInt());
				dep1.setName("Robotica");
				departmentDao.update(dep1);
				System.out.println("Update completed!");
				System.out.println("");
				break;

			case 4:
				System.out.println("\n====Delete by Id Department====");
				System.out.print("Enter id for delete: ");
				int id = sc.nextInt();
				departmentDao.deleteById(id);
				break;
			case 2:
				System.out.println("\n====Find All Department====");
				for(Department dep : departmentDao.findAll()) {
					System.out.println(dep);
				}
				
				break;
			default:
				System.out.println("Invalid number");
			} 
			System.out.print("\nDo you want to continue using the service? (S/N): ");
			testeLogico = sc.next().toUpperCase().charAt(0);
			System.out.println("");
		}

	}
	

}
