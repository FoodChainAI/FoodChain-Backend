package com.example.foodchain.geo.repository;

import com.example.foodchain.geo.entity.Locality;
import com.example.foodchain.geo.entity.LocalityLevel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocalityRepository extends JpaRepository<Locality, UUID> {

    List<Locality> findByParentIdOrderByName(UUID parentId);

    List<Locality> findByParentIdIsNullOrderByName();

    List<Locality> findByLevelOrderByName(LocalityLevel level);

    List<Locality> findByParentIdAndLevelOrderByName(UUID parentId, LocalityLevel level);

    /** Accent- and case-insensitive name search (needs the unaccent extension). */
    @Query(value = """
            SELECT * FROM localities
            WHERE unaccent(lower(name)) LIKE unaccent(lower(concat('%', :q, '%')))
            ORDER BY level, name
            LIMIT 30
            """, nativeQuery = true)
    List<Locality> searchByName(@Param("q") String q);

    /** Great-circle distance in meters between two localities' centroids (geography). */
    @Query(value = """
            SELECT ST_Distance(a.centroid, b.centroid)
            FROM localities a, localities b
            WHERE a.id = :from AND b.id = :to
            """, nativeQuery = true)
    Double distanceMeters(@Param("from") UUID from, @Param("to") UUID to);

    /** Nearest locality to a point — powers the optional "use my position" feature. */
    @Query(value = """
            SELECT * FROM localities
            ORDER BY centroid <-> ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
            LIMIT 1
            """, nativeQuery = true)
    Optional<Locality> findNearest(@Param("lat") double lat, @Param("lng") double lng);
}
