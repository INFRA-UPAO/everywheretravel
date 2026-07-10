package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DetalleLiquidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleLiquidacionRepository extends JpaRepository<DetalleLiquidacion, Integer> {

    @Query("SELECT d FROM DetalleLiquidacion d " +
           "LEFT JOIN FETCH d.liquidacion " +
           "LEFT JOIN FETCH d.viajero " +
           "LEFT JOIN FETCH d.producto " +
           "LEFT JOIN FETCH d.proveedor " +
           "LEFT JOIN FETCH d.operador")
    List<DetalleLiquidacion> findAllWithRelations();

    @Query("SELECT d FROM DetalleLiquidacion d " +
           "LEFT JOIN FETCH d.liquidacion " +
           "LEFT JOIN FETCH d.viajero " +
           "LEFT JOIN FETCH d.producto " +
           "LEFT JOIN FETCH d.proveedor " +
           "LEFT JOIN FETCH d.operador " +
           "WHERE d.id = :id")
    Optional<DetalleLiquidacion> findByIdWithRelations(@Param("id") Integer id);

    @Query("SELECT d FROM DetalleLiquidacion d " +
           "LEFT JOIN FETCH d.liquidacion " +
           "LEFT JOIN FETCH d.viajero " +
           "LEFT JOIN FETCH d.producto " +
           "LEFT JOIN FETCH d.proveedor " +
           "LEFT JOIN FETCH d.operador " +
           "WHERE d.liquidacion.id = :liquidacionId")
    List<DetalleLiquidacion> findByLiquidacionIdWithRelations(@Param("liquidacionId") Integer liquidacionId);

    @Query("SELECT d FROM DetalleLiquidacion d " +
           "LEFT JOIN FETCH d.viajero v " +
           "LEFT JOIN FETCH v.personaNatural " +
           "LEFT JOIN FETCH d.producto " +
           "LEFT JOIN FETCH d.proveedor " +
           "LEFT JOIN FETCH d.operador " +
           "WHERE d.liquidacion.id = :liquidacionId")
    List<DetalleLiquidacion> findByLiquidacionIdSinLiquidacion(@Param("liquidacionId") Integer liquidacionId);

    @Query("SELECT COUNT(dl) FROM DetalleLiquidacion dl WHERE dl.producto.id = :productoId")
    long countByProductoId(@Param("productoId") Integer productoId);

    @Query("SELECT COUNT(dl) FROM DetalleLiquidacion dl WHERE dl.proveedor.id = :proveedorId")
    long countByProveedorId(@Param("proveedorId") Integer proveedorId);

}