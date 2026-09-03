package com.sunrisedental.service.observer;

import com.sunrisedental.dto.AppointmentDetailDTO;

/**
 * Observer Pattern Interface: NotificationObserver
 * Listens for appointment events (booking, confirmation, rescheduling, cancellation).
 */
public interface NotificationObserver {
    
    void onAppointmentScheduled(AppointmentDetailDTO appointment);
    
    void onAppointmentStatusChanged(AppointmentDetailDTO appointment, String oldStatus, String newStatus);
}
