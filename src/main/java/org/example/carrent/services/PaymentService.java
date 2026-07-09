package org.example.carrent.services;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.example.carrent.dto.PaymentResponse;
import org.example.carrent.models.Rental;
import org.example.carrent.models.Vehicle;
import org.example.carrent.repositories.IRentalRepository;
import org.example.carrent.repositories.IVehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PaymentService {

    private final IRentalRepository rentalRepository;
    private final IVehicleRepository vehicleRepository;
    private final VehicleLocationService vehicleLocationService;

    public PaymentService(IRentalRepository rentalRepository,
                          IVehicleRepository vehicleRepository,
                          VehicleLocationService vehicleLocationService) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleLocationService = vehicleLocationService;
    }

    public PaymentResponse calculateActivePayment(String userLogin) {
        Rental rental = rentalRepository.findActiveByUserLogin(userLogin)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Brak aktywnego wypożyczenia do opłacenia."
                ));

        Vehicle vehicle = vehicleRepository.findById(rental.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Nie znaleziono pojazdu: " + rental.getVehicleId()
                ));

        LocalDate start = LocalDate.parse(rental.getStartDate());
        LocalDate today = LocalDate.now();

        long days = ChronoUnit.DAYS.between(start, today) + 1;
        if (days < 1) {
            days = 1;
        }

        double amount = vehicle.getPrice() * days;

        return new PaymentResponse(
                userLogin,
                vehicle.getId(),
                vehicle.getPrice(),
                days,
                amount,
                false,
                "Kwota do zapłaty: " + amount + " PLN"
        );
    }

    public PaymentResponse payActiveRental(String userLogin) {
        PaymentResponse response = calculateActivePayment(userLogin);

        Vehicle vehicle = vehicleRepository.findById(response.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Nie znaleziono pojazdu"
                ));

        if (!vehicleLocationService.isAtAllowedLocation(vehicle)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Pojazd musi znajdować się w siedzibie firmy lub innym dozwolonym miejscu, aby opłacić wypożyczenie."
            );
        }

        try {
            long amountInGrosze = Math.round(response.getAmount() * 100);

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInGrosze)
                    .setCurrency("pln")
                    .setPaymentMethod("pm_card_visa")
                    .setConfirm(true)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                    .build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            response.setPaymentIntentId(intent.getId());
            response.setPaid("succeeded".equals(intent.getStatus()));
            response.setMessage("Stripe status: " + intent.getStatus());

        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Błąd płatności Stripe: " + e.getMessage());
        }

        return response;
    }
}