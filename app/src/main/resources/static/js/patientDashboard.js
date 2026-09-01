// patientDashboard.js
import { getDoctors, filterDoctors } from './services/doctorServices.js';
import { openModal } from './components/modals.js';
import { createDoctorCard } from './components/doctorCard.js';
import { patientSignup, patientLogin } from './services/patientServices.js';

document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("patientLogin"));
  }

  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);
  if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});

function loadDoctorCards() {
  getDoctors()
    .then((doctors) => {
      renderDoctorCards(doctors);
    })
    .catch((error) => {
      console.error("Failed to load doctors:", error);
    });
}

function filterDoctorsOnChange() {
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

  filterDoctors(name, time, specialty)
    .then((response) => {
      const doctors = response?.doctors || [];
      renderDoctorCards(doctors);
    })
    .catch((error) => {
      console.error("Failed to filter doctors:", error);
      alert("❌ An error occurred while filtering doctors.");
    });
}

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
    contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
  }
}

window.signupPatient = async function () {
  try {
    const name = document.getElementById("name")?.value.trim();
    const email = document.getElementById("email")?.value.trim();
    const password = document.getElementById("password")?.value;
    const phone = document.getElementById("phone")?.value.trim();
    const address = document.getElementById("address")?.value.trim();

    if (!name || !email || !password || !phone || !address) {
      alert("Please fill in all fields.");
      return;
    }

    const data = { name, email, password, phone, address };
    const { success, message } = await patientSignup(data);
    if (success) {
      alert(message || "Signup successful!");
      const modal = document.getElementById("modal");
      if (modal) modal.style.display = "none";
      window.location.reload();
    } else {
      alert(message || "Signup failed.");
    }
  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ An error occurred while signing up.");
  }
};

window.loginPatient = async function () {
  try {
    const email = document.getElementById("email")?.value.trim();
    const password = document.getElementById("password")?.value;

    if (!email || !password) {
      alert("Please enter both email and password.");
      return;
    }

    const data = { identifier: email, password };
    const response = await patientLogin(data);
    if (response.ok) {
      const result = await response.json();
      if (typeof setRole === "function") {
        setRole("loggedPatient");
      } else {
        localStorage.setItem("userRole", "loggedPatient");
      }
      localStorage.setItem("token", result.token);
      window.location.href = "/pages/loggedPatientDashboard.html";
    } else {
      const result = await response.json().catch(() => ({}));
      alert("❌ " + (result.message || "Invalid credentials!"));
    }
  } catch (error) {
    console.error("Login error:", error);
    alert(" Failed to login. Please try again.");
  }
};