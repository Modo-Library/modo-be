package modo.repository;

import modo.domain.entity.Books;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BooksRepository extends JpaRepository<Books, Long> {

    List<Books> findBooksByNameContaining(String queryString);

    @Query(value = "SELECT b.books_id, b.name, b.price, b.status, b.deadline, b.description, b.img_url, b.created_at, b.modified_at, b.location, b.users_id, " +
            "ST_Distance_Sphere(point(:x,:y),b.location) AS diff_Distance " +
            "FROM books AS b " +
            "WHERE ST_Distance_Sphere(point(:x,:y),b.location) >= :distance " +
            "ORDER BY diff_Distance", nativeQuery = true)
    List<Books> findBooksWithDistance(double x, double y, double distance);

    @Query(value = "SELECT b.books_id, b.name, b.price, b.status, b.deadline, b.description, b.img_url, b.created_at, b.modified_at, b.location, b.users_id, " +
            "ST_Distance_Sphere(point(:x,:y),b.location) AS diff_Distance " +
            "FROM books AS b " +
            "WHERE ST_Distance_Sphere(point(:x,:y),b.location) >= :distance " +
            "AND b.name LIKE CONCAT('%', :queryString, '%') " +
            "ORDER BY diff_Distance", nativeQuery = true)
    List<Books> findBooksByNameContainingWithDistance(double x, double y, double distance, String queryString);


    @Query(value = "SELECT b.books_id, b.name, b.price, b.status, b.deadline, b.description, b.img_url, b.created_at, b.modified_at, b.location, b.users_id, " +
            "ST_Distance_Sphere(point(:x,:y),b.location) AS diff_Distance " +
            "FROM books AS b " +
            "WHERE ST_Distance_Sphere(point(:x,:y),b.location) <= :distance " +
            "ORDER BY diff_Distance", nativeQuery = true)
    Page<Books> findBooksWithDistanceWithPaging(double x, double y, double distance, Pageable pageable);

    @Query(value = "SELECT b.books_id, b.name, b.price, b.status, b.deadline, b.description, b.img_url, b.created_at, b.modified_at, b.location, b.users_id, " +
            "ST_Distance_Sphere(point(:x,:y),b.location) AS diff_Distance " +
            "FROM books AS b " +
            "WHERE ST_Distance_Sphere(point(:x,:y),b.location) <= :distance " +
            "AND b.name LIKE CONCAT('%', :queryString, '%') " +
            "ORDER BY diff_Distance", nativeQuery = true)
    Page<Books> findBooksByNameContainingWithDistanceWithPaging(double x, double y, double distance, String queryString, Pageable pageable);
}
