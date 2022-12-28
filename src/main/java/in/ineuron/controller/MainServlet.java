package in.ineuron.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import in.ineuron.model.AllQueryGenerator;
import in.ineuron.model.MySqlJdbcUtil;
import in.ineuron.view.DisplayVisuals;
import java.io.IOException;
import javax.servlet.annotation.WebInitParam;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet(urlPatterns = { "/mainServlet" }, initParams = {
		@WebInitParam(name = "jdbcURL", value = "jdbc:mysql://localhost:3306/schooldbo"),
		@WebInitParam(name = "username", value = "root"), @WebInitParam(name = "passwd", value = "Safvan@123") })
public class MainServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private Connection connection;

	private AllQueryGenerator allQueryGenerator = AllQueryGenerator.getAllQueryGenerator();

	PreparedStatement preparedStatementForInsert;

	// Object of Class in View
	private Integer insertRowCount;

	//
	private DisplayVisuals displayVisuals = DisplayVisuals.getDisplayVisualsObj();;

	static
	{
		System.out.println("servlet class loading success");
	}

	public MainServlet()
	{
		System.out.println("servlet  instantiation success");
	}

	@Override
	public void init()
	{
		System.out.println("Servlet initilization succes");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		// finding which operation is requested by user from html file
		String dbOperation = request.getParameter("operation");

		// getting JDBC connection from util class
		connection = MySqlJdbcUtil.getmySqlJdbcConnection();

		if (dbOperation.equals("insert"))
		{
			// getting sql query based on the specified dbOperation
			String sqlQuery = allQueryGenerator.generateSqlQuery(dbOperation);
			
			// calling method Student details insertion to DB
			runStudentInsertOperation(dbOperation, request, response,sqlQuery);
		}

	}

	// for Student INSERTION to database 
	public void runStudentInsertOperation(String dbOperation, HttpServletRequest request, HttpServletResponse response, String sqlQuery)
	{
		if (connection != null)
		{
			// getting preparedStatement for insert operation from Util class
			preparedStatementForInsert = MySqlJdbcUtil.getPreparedStatement(connection, sqlQuery);

			// setting user input values to the insert query
			preparedStatementForInsert = allQueryGenerator.setUserInputValuesToPreparedStatement(request, dbOperation,
					preparedStatementForInsert);
			System.out.println("insert query values set successfull");

			insertRowCount = 0;
			if (preparedStatementForInsert != null)
			{
				try
				{
					insertRowCount = preparedStatementForInsert.executeUpdate();

				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		}
		try
		{
			// to display response in the browser screen -- passing response and row count
			// object
			displayVisuals.showInsertOperationsResult(response, insertRowCount);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
