package pe.edu.upeu.jdrefrigeracion.notificaciones.service;

public interface NotificadorCorreo {
    void enviarCorreo(String destinatario, String asunto, String cuerpo);
    
    default void enviarCorreoConAdjunto(String destinatario, String asunto, String cuerpo, java.io.File adjunto) {
        enviarCorreo(destinatario, asunto, cuerpo);
    }
}
