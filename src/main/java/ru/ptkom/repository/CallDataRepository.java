package ru.ptkom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ptkom.model.CallData;
import ru.ptkom.model.Operator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CallDataRepository extends JpaRepository<CallData, Long> {

    Optional<List<CallData>> findByCallStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT * FROM cdr WHERE ((from_operator_id = :id OR to_operator_id = :id) AND (callstartdate >= :startDate AND callstartdate <= :finishDate)) ORDER BY callstartdate DESC", nativeQuery = true)
    Optional<List<CallData>> findOperatorsTraffic(@Param("id") Long id, @Param("startDate") LocalDateTime startDate, @Param("finishDate") LocalDateTime finishDate);

    @Query(value = "SELECT * FROM cdr ORDER BY callstartdate DESC OFFSET :offset ROWS FETCH NEXT :quantity ROWS ONLY", nativeQuery = true)
    Optional<List<CallData>> findLastRows(@Param("offset") Long offset, @Param("quantity") Long quantity);

    @Query(value = "SELECT * FROM cdr WHERE ((numbera = :number OR numberb = :number) AND (callstartdate >= :startDate AND callstartdate <= :finishDate)) ORDER BY callstartdate DESC OFFSET :offset ROWS FETCH NEXT :quantity ROWS ONLY", nativeQuery = true)
    Optional<List<CallData>> findCallHistoryByNumberAndPeriod(@Param("number") String number, @Param("startDate") LocalDateTime startDate, @Param("finishDate") LocalDateTime finishDate, @Param("offset") Long offset, @Param("quantity") Long quantity);

    @Query(value = "SELECT * FROM cdr WHERE ((numbera = :number OR numberb = :number) AND (callstartdate >= :startDate AND callstartdate <= :finishDate)) ORDER BY callstartdate DESC", nativeQuery = true)
    Optional<List<CallData>> findAllCallHistoryByNumberAndPeriod(@Param("number") String number, @Param("startDate") LocalDateTime startDate, @Param("finishDate") LocalDateTime finishDate);
}
