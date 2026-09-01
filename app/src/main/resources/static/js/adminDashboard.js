import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  if (!contentDiv) return;

  contentDiv.innerHTML = "";

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
    return;
  }

  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Failed to load doctors:", error);
    const contentDiv = document.getElementById("content");
    if (contentDiv) {
      contentDiv.innerHTML = "<p>Unable to load doctors right now.</p>";
    }
  }
}

function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar");
  const filterTime = document.getElementById("filterTime");
  const filterSpecialty = document.getElementById("filterSpecialty");

  if (!searchBar || !filterTime || !filterSpecialty) return;

  const name = searchBar.value.trim() || null;
  const time = filterTime.value || null;
  const specialty = filterSpecialty.value || null;

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

async function adminAddDoctor() {
  const name = document.getElementById("doctorName")?.value.trim();
  const specialty = document.getElementById("specialization")?.value;
  const email = document.getElementById("doctorEmail")?.value.trim();
  const password = document.getElementById("doctorPassword")?.value;
  const phone = document.getElementById("doctorPhone")?.value.trim();
  const checkboxes = document.querySelectorAll('input[name="availability"]:checked');
  const availability = Array.from(checkboxes).map((checkbox) => checkbox.value);

  if (!name || !specialty || !email || !password || !phone) {
    alert("Please complete all doctor fields.");
    return;
  }

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Please log in again to continue.");
    window.location.href = "/";
    return;
  }

  const doctor = {
    name,
    specialty,
    email,
    password,
    phone,
    availableTimes: availability,
  };

  try {
    const result = await saveDoctor(doctor, token);
    if (result.success) {
      alert(result.message || "Doctor added successfully.");
      const modal = document.getElementById("modal");
      if (modal) modal.style.display = "none";
      await loadDoctorCards();
    } else {
      alert(result.message || "Unable to add doctor.");
    }
  } catch (error) {
    console.error("Admin add doctor failed:", error);
    alert("❌ Failed to add doctor.");
  }
}

window.adminAddDoctor = adminAddDoctor;

window.addEventListener("DOMContentLoaded", () => {
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => {
      openModal("addDoctor");
    });
  }

  const searchBar = document.getElementById("searchBar");
  if (searchBar) {
    searchBar.addEventListener("input", filterDoctorsOnChange);
  }

  const filterTime = document.getElementById("filterTime");
  if (filterTime) {
    filterTime.addEventListener("change", filterDoctorsOnChange);
  }

  const filterSpecialty = document.getElementById("filterSpecialty");
  if (filterSpecialty) {
    filterSpecialty.addEventListener("change", filterDoctorsOnChange);
  }

  loadDoctorCards();
});

