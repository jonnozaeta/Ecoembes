package es.deusto.sd.ecoembes.external;

import es.deusto.sd.ecoembes.entity.ContainerType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simula la integración con el servicio externo PlasSB Ltd.
 * Se encarga del procesamiento de la capacidad y la asignación de las plantas de reciclaje.
 */
@Service
public class PlasSBLtdGateway {

    // Simulación del estado externo: PlantId -> capacidad diaria utilizada en kg
    private final Map<String, Double> plantAllocatedWeight = new ConcurrentHashMap<>();

    public PlasSBLtdGateway() {
        // Reservas iniciales simuladas de capacidad
        plantAllocatedWeight.put("PLANT-01", 32000.0);
        plantAllocatedWeight.put("PLANT-02", 15000.0);
    }

    public double getUsedCapacity(String plantId, LocalDate date) {
        return plantAllocatedWeight.getOrDefault(plantId, 10000.0);
    }

    public boolean reserveCapacity(String plantId, double weightKg, ContainerType wasteType, LocalDate date) {
        double current = plantAllocatedWeight.getOrDefault(plantId, 0.0);
        // Simula la comprobación del límite de capacidad (por ejemplo, máximo 50000 kg)
        if (current + weightKg > 50000.0) {
            return false;
        }
        plantAllocatedWeight.put(plantId, current + weightKg);
        return true;
    }
}
