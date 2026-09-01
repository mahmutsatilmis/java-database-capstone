import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

// Ensure token and role are synced if accessed via /doctorDashboard/{token}
const pathParts = window.location.pathname.split("/").filter(Boolean);
if (pathParts.length > 1 && pathParts[0] === "doctorDashboard") {
  const urlToken = pathParts[1];
  if (urlToken) {
    localStorage.setItem("token", urlToken);
    localStorage.setItem("userRole", "doctor");
  }
}

let selectedDate = "null";
let patientName = "null";

function renderEmptyState(message) {
  const tableBody = document.getElementById("patientTableBody");
  if (!tableBody) return;
  tableBody.innerHTML = `<tr><td colspan="5" style="text-align: center;">${message}</td></tr>`;
}

async function loadAppointments() {
  const tableBody = document.getElementById("patientTableBody");
  if (!tableBody) return;

  const currentToken = localStorage.getItem("token") || window.location.pathname.split("/").filter(Boolean).pop();
  if (!currentToken) {
    renderEmptyState("Session expired. Please log in again.");
    return;
  }

  try {
    const dateParam = selectedDate || "null";
    const nameParam = (patientName && patientName.trim().length > 0 && patientName !== "null") ? patientName.trim() : "null";

    const response = await getAllAppointments(dateParam, nameParam, currentToken);
    const appointments = response?.appointments || response?.data || (Array.isArray(response) ? response : []);

    tableBody.innerHTML = "";

    if (!appointments || appointments.length === 0) {
      renderEmptyState("No Appointments found.");
      return;
    }

    appointments.forEach((appointment) => {
      const patient = {
        id: appointment.patientId || appointment.patient?.id || "N/A",
        name: appointment.patientName || appointment.patient?.name || "Unknown patient",
        phone: appointment.patientPhone || appointment.phone || appointment.patient?.phone || "N/A",
        email: appointment.patientEmail || appointment.email || appointment.patient?.email || "N/A",
      };

      const doctorId = appointment.doctorId || appointment.doctor?.id || "";
      const appointmentId = appointment.id || appointment.appointmentId || "";
      const row = createPatientRow(patient, appointmentId, doctorId);
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error loading appointments:", error);
    renderEmptyState("Error loading appointments. Try again later.");
  }
}

window.addEventListener("DOMContentLoaded", () => {
  const searchBar = document.getElementById("searchBar");
  const todayButton = document.getElementById("todayButton");
  const datePicker = document.getElementById("datePicker");

  if (searchBar) {
    searchBar.addEventListener("input", () => {
      const val = searchBar.value.trim();
      patientName = val.length > 0 ? val : "null";
      loadAppointments();
    });
  }

  if (todayButton) {
    todayButton.addEventListener("click", () => {
      const today = new Date().toISOString().split("T")[0];
      selectedDate = today;
      if (datePicker) {
        datePicker.value = today;
      }
      loadAppointments();
    });
  }

  if (datePicker) {
    datePicker.addEventListener("change", () => {
      selectedDate = datePicker.value ? datePicker.value : "null";
      loadAppointments();
    });
  }

  if (typeof renderContent === "function") {
    renderContent();
  }

  loadAppointments();
});

window.loadAppointments = loadAppointments;