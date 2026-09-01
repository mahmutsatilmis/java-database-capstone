import { getPatientAppointments } from "./services/patientServices.js";
import { createPatientRecordRow } from "./components/patientRecordRow.js";

const tableBody = document.getElementById("patientTableBody");
const token = localStorage.getItem("token");

const urlParams = new URLSearchParams(window.location.search);
const patientId = urlParams.get("id");
const doctorId = urlParams.get("doctorId");

document.addEventListener("DOMContentLoaded", initializePage);

async function initializePage() {
  try {
    if (!token) throw new Error("No token found");

    const appointmentData = (await getPatientAppointments(patientId, token)) || [];

    const filteredAppointments = appointmentData.filter(
      (app) => String(app.doctorId) === String(doctorId)
    );

    renderAppointments(filteredAppointments);
  } catch (error) {
    console.error("Error loading appointments:", error);
    alert("❌ Failed to load appointments.");
  }
}

function renderAppointments(appointments) {
  if (!tableBody) return;
  tableBody.innerHTML = "";

  if (!appointments || !appointments.length) {
    tableBody.innerHTML = `<tr><td colspan="4" style="text-align:center;">No Appointments Found</td></tr>`;
    return;
  }

  appointments.forEach((appointment) => {
    const row = createPatientRecordRow(appointment);
    tableBody.appendChild(row);
  });
}