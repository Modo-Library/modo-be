package modo.repository;

import modo.domain.dto.books.EachBooksResponseVo;
import modo.domain.entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BooksRepository extends JpaRepository<Books, Long> {

    @Query(value =
            "SELECT\n" +
                    "    b.books_id AS booksId, b.name AS name, b.price AS price, b.status AS status, b.description AS description, b.img_url AS imgUrl,\n" +
                    "    ROUND(ST_Distance_Sphere(point(:x, :y), b.location), -2) AS distance\n" +
                    "FROM\n" +
                    "    books AS b\n" +
                    "WHERE\n" +
                    "    ROUND(ST_Distance_Sphere(point(:x, :y), b.location), -2) <= :limitDistance\n" +
                    "    AND (\n" +
                    "        ROUND(ST_Distance_Sphere(point(:x, :y), b.location), -2) > :startDistance\n" +
                    "        OR (ROUND(ST_Distance_Sphere(point(:x, :y), b.location), -2) = :startDistance AND b.books_id > :startId)\n" +
                    "    )\n" +
                    "    AND b.name LIKE CONCAT('%', :queryString, '%')\n" +
                    "ORDER BY\n" +
                    "    distance ASC, books_id ASC\n" +
                    "LIMIT 10;\n",
            nativeQuery = true)
    List<EachBooksResponseVo> findBooksByNameContainingWithDistanceWithNoOffset(double x, double y, double limitDistance, String queryString, int startDistance, Long startId);

    @Query(value =
            "SELECT\n" +
                    "    b.books_id AS booksId, b.name AS name, b.price AS price, b.status AS status, b.description AS description, b.img_url AS imgUrl,\n" +
                    "    ROUND(ST_Distance_Sphere(point(:x, :y), b.location), -2) AS distance\n" +
                    "FROM\n" +
                    "    books AS b\n" +
                    "WHERE\n" +
                    "    ROUND(ST_Distance_Sphere(point(:x, :y), b.location), -2) <= :limitDistance\n" +
                    "    AND (\n" +
                    "        ROUND(ST_Distance_Sphere(point(:x, :y), b.location), -2) > :startDistance\n" +
                    "        OR (ROUND(ST_Distance_Sphere(point(:x, :y), b.location), -2) = :startDistance AND b.books_id > :startId)\n" +
                    "    )\n" +
                    "ORDER BY\n" +
                    "    distance ASC, books_id ASC\n" +
                    "LIMIT 10;\n",
            nativeQuery = true)
    List<EachBooksResponseVo> findBooksWithDistanceWithNoOffset(double x, double y, double limitDistance, int startDistance, Long startId);


    @Query("select b from Books b left join fetch b.picturesList where b.booksId = :booksId")
    Optional<Books> findBooks(@Param("booksId") Long booksId);
}
