import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";

export function createDoctorCard(doctor) {
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name || "Doctor";

  const specialization = document.createElement("p");
  specialization.textContent = `Specialization: ${doctor.specialty || doctor.specialization || "Not specified"}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email || "Not available"}`;

  const availability = document.createElement("p");
  const availableTimes = Array.isArray(doctor.availableTimes)
    ? doctor.availableTimes
    : Array.isArray(doctor.availability)
      ? doctor.availability
      : [doctor.availableTime || doctor.availability || "Not specified"];
  availability.textContent = `Availability: ${availableTimes.join(", ")}`;

  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  const role = localStorage.getItem("userRole");

  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("delete-btn");

    removeBtn.addEventListener("click", async () => {
      const confirmed = window.confirm("Are you sure you want to delete this doctor?");
      if (!confirmed) return;

      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired. Please log in again.");
        window.location.href = "/";
        return;
      }

      try {
        const result = await deleteDoctor(doctor.id, token);
        if (result.success) {
          card.remove();
          alert(result.message || "Doctor deleted successfully.");
        } else {
          alert(result.message || "Unable to delete doctor.");
        }
      } catch (error) {
        console.error("Error deleting doctor:", error);
        alert("Failed to delete doctor.");
      }
    });

    actionsDiv.appendChild(removeBtn);
  } else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");
    bookNow.addEventListener("click", () => {
      alert("Patient needs to login first.");
    });
    actionsDiv.appendChild(bookNow);
  } else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");

    bookNow.addEventListener("click", async (event) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired. Please log in again.");
        window.location.href = "/";
        return;
      }

      try {
        const patientData = await getPatientData(token);
        if (patientData && typeof window.showBookingOverlay === "function") {
          window.showBookingOverlay(event, doctor, patientData);
          return;
        }

        if (patientData && typeof window.openModal === "function") {
          window.openModal("patientBooking");
        }

        if (!patientData) {
          alert("Unable to load your patient information.");
        }
      } catch (error) {
        console.error("Booking error:", error);
        alert("Unable to proceed with booking.");
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);
  return card;
}

