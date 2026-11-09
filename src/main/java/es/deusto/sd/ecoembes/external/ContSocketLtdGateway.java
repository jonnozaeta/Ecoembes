package es.deusto.sd.ecoembes.external;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Simula la integración con el servicio externo ContSocket Ltd.
 * Se encarga de la comunicación por socket/IoT con sensores inteligentes de contenedores.
 */
@Service
public class ContSocketLtdGateway {

    /**
     * Comprueba si un sensor del contenedor está operativo y conectado a la red ContSocket.
     */
    public boolean verifySensorConnection(String containerId) {
        return containerId != null && !containerId.isBlank();
    }

    /**
     * Notifica al sistema ContSocket una actualización registrada del sensor del contenedor.
     */
    public void forwardTelemetry(String containerId, double level, LocalDateTime timestamp) {
        // Simulación de registro de telemetría / notificación por socket
        System.out.println("[ContSocket Ltd.] Telemetry received for container " + containerId + ": " + level + "% at " + timestamp);
    }
}
