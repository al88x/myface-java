package techsmiths.myface.services;

import org.springframework.stereotype.Service;
import techsmiths.myface.models.dbmodels.User;

import java.util.List;

@Service
public class UserService extends DatabaseService {

    public List<User> getAllUsers(int limit, int offset) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM users LIMIT :limit OFFSET :offset")
                        .bind("limit", limit)
                        .bind("offset", offset)
                        .mapToBean(User.class)
                        .list()
        );
    }

    public List<User> getAllUsers() {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM users")
                        .mapToBean(User.class)
                        .list()
        );
    }

    public User getUser(int id) {
        return jdbi.withHandle(handle ->
                handle.createQuery("SELECT * FROM users WHERE id = :id")
                        .bind("id", id)
                        .mapToBean(User.class)
                        .one()
        );
    }

    public void updateUser(int id, User user) {
        jdbi.withHandle(handle ->
                handle.createUpdate(
                            "UPDATE IGNORE users SET " +
                                    "username = :username," +
                                    "email = :email," +
                                    "first_name = :firstName," +
                                    "last_name = :lastName," +
                                    "profile_image = :profileImage," +
                                    "banner_image = :bannerImage " +
                            "WHERE id = :id")
                        .bind("username", user.getUsername())
                        .bind("email", user.getEmail())
                        .bind("firstName", user.getFirstName())
                        .bind("lastName", user.getLastName())
                        .bind("profileImage", user.getProfileImage())
                        .bind("bannerImage", user.getBannerImage())
                        .bind("id", id)
                        .execute()
        );
    }

    public void createUser(User user) {
        jdbi.withHandle(handle ->
                handle.createUpdate(
                        "INSERT INTO users " +
                                "(username, email, first_name, last_name, profile_image, banner_image) " +
                                "VALUES " +
                                "(:username, :email, :firstName, :lastName, :profileImage, :bannerImage)")
                        .bind("username", user.getUsername())
                        .bind("email", user.getEmail())
                        .bind("firstName", user.getFirstName())
                        .bind("lastName", user.getLastName())
                        .bind("profileImage", user.getProfileImage())
                        .bind("bannerImage", user.getBannerImage())
                        .execute()
        );
    }
}
