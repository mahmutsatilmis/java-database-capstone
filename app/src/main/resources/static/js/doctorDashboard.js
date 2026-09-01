import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const tableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0];
const token = localStorage.getItem("token");
let patientName = null;

function renderEmptyState(message) {
  if (!tableBody) return;

  tableBody.innerHTML = `
    <tr>
      <td colspan="5">${message}</td>
    </tr>
  `;
}

async function loadAppointments() {
  if (!tableBody) return;

  try {
    const response = await getAllAppointments(selectedDate, patientName || "null", token);
    const appointments = response?.appointments || response?.data || response || [];

    tableBody.innerHTML = "";

    if (!appointments || appointments.length === 0) {
      renderEmptyState("No Appointments found for today.");
      return;
    }

    appointments.forEach((appointment) => {
      const patient = appointment.patient || {
        id: appointment.patientId || "N/A",
        name: appointment.patientName || "Unknown patient",
        phone: appointment.phone || "N/A",
        email: appointment.email || "N/A",
      };

      const doctorId = appointment.doctor?.id || appointment.doctorId || "";
      const appointmentId = appointment.id || "";
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
      const value = searchBar.value.trim();
      patientName = value || "null";
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
    datePicker.value = selectedDate;
    datePicker.addEventListener("change", () => {
      selectedDate = datePicker.value || new Date().toISOString().split("T")[0];
      loadAppointments();
    });
  }

  if (typeof renderContent === "function") {
    renderContent();
  }

  loadAppointments();
});

window.loadAppointments = loadAppointments;
