import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";
import { openModal } from "./components/modals.js";

// Ensure token and role are synced if accessed via /adminDashboard/{token}
const pathParts = window.location.pathname.split("/").filter(Boolean);
if (pathParts.length > 1 && pathParts[0] === "adminDashboard") {
  const urlToken = pathParts[1];
  if (urlToken) {
    localStorage.setItem("token", urlToken);
    localStorage.setItem("userRole", "admin");
  }
}

window.openModal = openModal;
window.adminAddDoctor = adminAddDoctor;

function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;
  contentDiv.innerHTML = "";

  if (doctors && doctors.length > 0) {
    doctors.forEach((doctor) => {
      const card = createDoctorCard(doctor);
      contentDiv.appendChild(card);
    });
  } else {
    contentDiv.innerHTML = "<p>No doctors found.</p>";
  }
}

async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Failed to load doctors:", error);
    renderDoctorCards([]);
  }
}

async function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  const name = searchBar && searchBar.value.trim().length > 0 ? searchBar.value.trim() : null;
  const time = filterTime && filterTime.value.length > 0 ? filterTime.value : null;
  const specialty = filterSpecialty && filterSpecialty.value.length > 0 ? filterSpecialty.value : null;

  if (!name && !time && !specialty) {
    loadDoctorCards();
    return;
  }

  try {
    const response = await filterDoctors(name, time, specialty);
    const doctors = response?.doctors || [];
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Failed to filter doctors:", error);
    renderDoctorCards([]);
  }
}

async function adminAddDoctor(e) {
  if (e && e.preventDefault) e.preventDefault();

  const name = document.getElementById("doctorName")?.value.trim();
  const specialty = document.getElementById("specialization")?.value.trim();
  const email = document.getElementById("doctorEmail")?.value.trim();
  const password = document.getElementById("doctorPassword")?.value;
  const phone = document.getElementById("doctorPhone")?.value.trim();

  const selectedSlots = [];
  document.querySelectorAll('input[name="availability"]:checked').forEach((cb) => {
    selectedSlots.push(cb.value);
  });

  if (!name || !specialty || !email || !password || !phone) {
    alert("Please fill in all fields.");
    return;
  }

  if (phone.length !== 10 || !/^\d{10}$/.test(phone)) {
    alert("Phone number must be 10 digits.");
    return;
  }

  if (password.length < 6) {
    alert("Password must be at least 6 characters.");
    return;
  }

  if (selectedSlots.length === 0) {
    alert("Please select at least one available time slot.");
    return;
  }

  const token = localStorage.getItem("token") || window.location.pathname.split("/").pop();
  if (!token) {
    alert("Session expired. Please log in again.");
    window.location.href = "/";
    return;
  }

  const doctorData = {
    name,
    specialty,
    email,
    password,
    phone,
    availableTimes: selectedSlots,
  };

  try {
    const result = await saveDoctor(doctorData, token);
    if (result.success) {
      alert(result.message || "Doctor added successfully.");
      const modal = document.getElementById("modal");
      if (modal) {
        modal.classList.remove("active");
        modal.style.display = "none";
      }
      loadDoctorCards();
    } else {
      alert(result.message || "Failed to add doctor.");
    }
  } catch (error) {
    console.error("Error saving doctor:", error);
    alert("Failed to add doctor.");
  }
}

document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);

  if (typeof window.attachHeaderButtonListeners === "function") {
    window.attachHeaderButtonListeners();
  }
});