package com.wisetaskuser.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import com.wisetaskuser.entities.Task;
import com.wisetaskuser.entities.Tasks;
import com.wisetaskuser.entities.User;
import com.wisetaskuser.repositories.UsersRepository;
import lombok.NoArgsConstructor;

/**
 * A service class that is being used by the users rest controller and performs
 * all the respective interactions with the database.
 * @author Theofanis Gkoufas
 *
 */
@Service
@NoArgsConstructor
public class UsersServices {
	
	@Autowired
	ApplicationContext context;

	@Autowired
	private UsersRepository usersRepository;
	
	public boolean areCredentialsValid(User loginCredentials) {
		boolean areValid = false;
		for(User user : usersRepository.findAll()) {
			if(user.getUsername().equals(loginCredentials.getUsername())
					&& user.getPassword().equals(loginCredentials.getPassword())) {
				areValid = true;
				break;
			}
		}
		return areValid;
	}
	
	public User getUser(User loginCredentials) {
		for(User user : usersRepository.findAll()) {
			if(user.getUsername().equals(loginCredentials.getUsername())
					&& user.getPassword().equals(loginCredentials.getPassword())) {
				return user;
			}
		}
		return null;
	}
	
	public User getUser(int id) throws NoSuchElementException{
		return usersRepository.findById(id).get();
	}
	
	public void addAccount(User user) {
		usersRepository.save(user);
	}
	
	public boolean isUsernameUnique(String username) {
		boolean isUnique = true;
		for(User user : usersRepository.findAll()) {
			if(user.getUsername().equals(username)) {
				isUnique = false;
				break;
			}
		}
		return isUnique;
	}
	
	@SuppressWarnings("unchecked")
	public Tasks getTasksThatNeedSending() {
		DataSource dataSource = context.getBean(DataSource.class);
		Connection conn;
		PreparedStatement preparedStatement;
		Tasks tasks = context.getBean(Tasks.class);
		/* We create a temporary list just for the sake of storing the primary lecturers
		   retrieved from the sql statement below. 
		   This happens because in a separate sql query, we retrieve the email addresses
		   of these lecturers (no I could not manage to get them on the same query as
		   much as I tried). I am also storing the task id so later on I will be able to
		   know on which task to pass the email value (emailAddressToSend field).
		*/
		List<String> primaryLecturers = context.getBean("stringList", List.class);
		try {
			conn = dataSource.getConnection();
			preparedStatement = conn.prepareStatement("SELECT T.task_id, T.task_description, "
					+ "M.primary_lecturer, S.time_to_send_notif "
					+ "FROM tasks T, users U, assessments A, modules M, entries E, settings S "
					+ "WHERE T.task_belongs_to_assessment = A.assessment_id "
					+ "AND A.assessment_belongsTo_module = M.module_id "
					+ "AND M.module_belongsTo_entry = E.entry_id "
					+ "AND S.entry_FK = E.entry_id "
					+ "AND E.user_id = U.user_id "
					+ "AND T.date_to_send = ?");
			preparedStatement.setObject(1, LocalDate.now());
			ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				tasks.getTasks().add(Task.builder()
						.taskId(resultSet.getInt(1))
						.taskDescription(resultSet.getString(2))
						.timeToSendEmail(resultSet.getString(4))
						.build());
				primaryLecturers.add(resultSet.getInt(1) + "-" + resultSet.getString(3));
			}
			List<String> emailAccountsOfUsers = getEmailAddressesBasedOnNames(primaryLecturers, conn);
			for(Task task : tasks.getTasks()) {
				for(String temp : emailAccountsOfUsers) {
					String[] userInfo = temp.split("-");
					String userEmailAccount = userInfo[userInfo.length - 1];
					int taskID = Integer.parseInt(userInfo[0]);
					if(task.getTaskId() == taskID) {
						task.setEmailAddressToSend(userEmailAccount);
					}
				}
			}
			preparedStatement.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tasks;
	}
	
	/**
	 * Retrieves a list of all the email addresses that belong to lecturers that are given
	 * as the "primaryLecturers" list argument.
	 * @param primaryLecturers The list of lecturers whose emails we want to retrieve.
	 * @param conn An open database connection established by a dataSource.
	 * @return A list of email addresses.
	 */
	private List<String> getEmailAddressesBasedOnNames(List<String> primaryLecturers, Connection conn) {
		PreparedStatement preparedStatement;
		String sql;
		for(int i=0; i<primaryLecturers.size(); i++) {
			String[] elementSplitted = primaryLecturers.get(i).split("-");
			String[] lecturerName = elementSplitted[1].split(" ");
			sql = "SELECT U.email FROM users U WHERE U.username LIKE '%" 
					+ lecturerName[lecturerName.length - 1] + "%'"; 
			try {
				preparedStatement = conn.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery();
				while(resultSet.next()) {
					String temp = primaryLecturers.get(i);
					temp += "-" + resultSet.getString(1);
					primaryLecturers.remove(i);
					primaryLecturers.add(i, temp);
				}
				if(i == primaryLecturers.size()) {
					preparedStatement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return primaryLecturers;
	}
	
}
