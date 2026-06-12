package com.panaderia.ecommerce.shared;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class HorarioStepDefinitions {

    private LocalTime horaActual;
    private ZoneId zona;
    private LocalDate fechaMinima;

    @Given("la hora actual es {string} en zona {string}")
    public void laHoraActualEsEnZona(String hora, String zonaId) {
        this.horaActual = LocalTime.parse(hora);
        this.zona = ZoneId.of(zonaId);
    }

    @When("el cliente intenta programar una entrega")
    public void elClienteIntentaProgramarUnaEntrega() {
        LocalTime corte = LocalTime.of(20, 0); // 8 PM
        LocalDate hoy = LocalDate.now(zona);
        if (horaActual.isAfter(corte)) {
            fechaMinima = hoy.plusDays(2); // Pasado mañana
        } else {
            fechaMinima = hoy.plusDays(1); // Mañana
        }
    }

    @Then("la fecha mínima de entrega debe ser mañana")
    public void laFechaMinimaDeEntregaDebeSerManana() {
        LocalDate manana = LocalDate.now(zona).plusDays(1);
        assertEquals(manana, fechaMinima);
    }

    @Then("la fecha mínima de entrega debe ser pasado mañana")
    public void laFechaMinimaDeEntregaDebeSerPasadoManana() {
        LocalDate pasadoManana = LocalDate.now(zona).plusDays(2);
        assertEquals(pasadoManana, fechaMinima);
    }
}