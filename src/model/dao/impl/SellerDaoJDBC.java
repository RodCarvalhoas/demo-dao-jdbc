package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) { //Método para inserir dados no bd
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId)"
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)", 
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime())); //Instanciando uma data no SQL
			st.setDouble(4,obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
			
			
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			
		}

	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?");
	
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime())); //Instanciando uma data no SQL
			st.setDouble(4,obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			
		}

	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"DELETE FROM seller "
					+ "WHERE Id = ?");
			
			st.setInt(1, id);
			
			st.executeUpdate();
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null; //Objeto do comando SQL com ?
		ResultSet rs = null; //Objeto que captura em forma de tabela
		try {
			st = conn.prepareStatement( //Comando SQL
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE seller.Id = ?");

			st.setInt(1, id); //Setando o department que foi deixado em aberto com ?
			rs = st.executeQuery(); //Executando o comando SQL
			if (rs.next()) { //Se o resultSet existir 1 linha >
				Department dep = instantiateDepartment(rs); //O department será instanciado a partir do método que captura o DepartmentId e Name do Departamento a partir do filtro feito pelo comando SQL.
				Seller obj = instantiateSeller(rs, dep); //O Seller será instanciado a partir do método
				return obj; //retorna o Seller a partir da instanciação
			}
			return null; //Se não existir vendedor, retorna nulo
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}

	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj  = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null; //Instanciando a variável PreparedStatement
		ResultSet rs = null; //Instanciando o ResultSet
		try {
			st = conn.prepareStatement( //Comando SQL com a váriavel PreparedStatemnt
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name");
			
			rs = st.executeQuery(); //Executando o Comando SQL
			
			List<Seller> list = new ArrayList<>(); //Instanciando um List de Seller
			Map<Integer, Department> map  = new HashMap<Integer, Department>(); //Instanciando um Map 
			
			
			while(rs.next()) { //Enquanto tiver próximo no resultSet
				
				Department dep = map.get(rs.getInt("DepartmentId")); //Instanciando um Department com a função map com a coluna DepartmentId
				
				if(dep == null) { //Se não existir DepartmentId*Função de cima* faça:
					dep = instantiateDepartment(rs);//Instanciando o Department
					map.put(rs.getInt("DepartmentId"), dep);//Inserindo o Department no conjunto Map
				}
				
				Seller obj = instantiateSeller(rs, dep);//Instanciando o Seller*Department*
				list.add(obj);//Adicionando o Seller na lista
				
			}
			return list;//Retorna List
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	@Override
	public List<Seller> findByDeparment(Department department) {//Método para procurar por Department
		PreparedStatement st = null; //Instanciando a variável PreparedStatement
		ResultSet rs = null; //Instanciando o ResultSet
		try {
			st = conn.prepareStatement( //Comando SQL com a váriavel PreparedStatemnt
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name");
			st.setInt(1, department.getId()); //Capturando o DepartmentId
			rs = st.executeQuery(); //Executando o Comando SQL
			
			List<Seller> list = new ArrayList<>(); //Instanciando um List de Seller
			Map<Integer, Department> map  = new HashMap<Integer, Department>(); //Instanciando um Map 
			
			
			while(rs.next()) { //Enquanto tiver próximo no resultSet
				
				Department dep = map.get(rs.getInt("DepartmentId")); //Instanciando um Department com a função map com a coluna DepartmentId
				
				if(dep == null) { //Se não existir DepartmentId*Função de cima* faça:
					dep = instantiateDepartment(rs);//Instanciando o Department
					map.put(rs.getInt("DepartmentId"), dep);//Inserindo o Department no conjunto Map
				}
				
				Seller obj = instantiateSeller(rs, dep);//Instanciando o Seller*Department*
				list.add(obj);//Adicionando o Seller na lista
				
			}
			return list;//Retorna List
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

}
