import { getPatientAppointments, getPatientData, filterAppointments } from "./services/patientServices.js";

const tableBody = document.getElementById("patientTableBody");
const token = localStorage.getItem("token");

let allAppointments = [];
let patientId = null;

document.addEventListener("DOMContentLoaded", initializePage);

async function initializePage() {
  try {
    if (!token) throw new Error("No token found");

    const patient = await getPatientData(token);
    if (!patient) throw new Error("Failed to fetch patient details");

    patientId = Number(patient.id);

    const appointmentData = (await getPatientAppointments(patientId, token)) || [];
    allAppointments = appointmentData.filter((app) => app.patientId === patientId);

    renderAppointments(allAppointments);
  } catch (error) {
    console.error("Error loading appointments:", error);
    alert("❌ Failed to load your appointments.");
  }
}

function renderAppointments(appointments) {
  if (!tableBody) return;
  tableBody.innerHTML = "";

  if (!appointments || !appointments.length) {
    tableBody.innerHTML = `<tr><td colspan="5" style="text-align:center;">No Appointments Found</td></tr>`;
    return;
  }

  appointments.forEach((appointment) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${appointment.patientName || "You"}</td>
      <td>${appointment.doctorName || "N/A"}</td>
      <td>${appointment.appointmentDate || "N/A"}</td>
      <td>${appointment.appointmentTimeOnly || "N/A"}</td>
      <td>${appointment.status === 0 ? `<img src="/assets/images/edit/edit.png" alt="Edit" class="prescription-btn" data-id="${appointment.patientId}">` : "-"}</td>
    `;

    if (appointment.status === 0) {
      const actionBtn = tr.querySelector(".prescription-btn");
      actionBtn?.addEventListener("click", () => redirectToUpdatePage(appointment));
    }

    tableBody.appendChild(tr);
  });
}

function redirectToUpdatePage(appointment) {
  const queryString = new URLSearchParams({
    appointmentId: appointment.id,
    patientId: appointment.patientId,
    patientName: appointment.patientName || "You",
    doctorName: appointment.doctorName,
    doctorId: appointment.doctorId,
    appointmentDate: appointment.appointmentDate,
    appointmentTime: appointment.appointmentTimeOnly,
  }).toString();

  window.location.href = `/pages/updateAppointment.html?${queryString}`;
}

const searchBar = document.getElementById("searchBar");
const appointmentFilter = document.getElementById("appointmentFilter");

if (searchBar) searchBar.addEventListener("input", handleFilterChange);
if (appointmentFilter) appointmentFilter.addEventListener("change", handleFilterChange);

async function handleFilterChange() {
  const searchBarValue = searchBar?.value.trim() || "";
  const filterValue = appointmentFilter?.value || "";

  const name = searchBarValue || null;
  const condition = filterValue === "allAppointments" ? null : filterValue || null;

  try {
    const response = await filterAppointments(condition, name, token);
    const appointments = response?.appointments || [];
    const filteredAppointments = appointments.filter((app) => app.patientId === patientId);

    renderAppointments(filteredAppointments);
  } catch (error) {
    console.error("Failed to filter appointments:", error);
    alert("❌ An error occurred while filtering appointments.");
  }
}