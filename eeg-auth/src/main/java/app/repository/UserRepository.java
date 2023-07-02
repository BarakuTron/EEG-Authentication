package app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	 User findByUsername(String username); //returns null if no user is found
}
