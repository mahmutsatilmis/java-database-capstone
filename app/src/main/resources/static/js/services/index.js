import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = `${API_BASE_URL}/admin/login`;
const DOCTOR_API = `${API_BASE_URL}/doctor/login`;
const PATIENT_API = `${API_BASE_URL}/patient/login`;

window.onload = function () {
  const adminBtn = document.getElementById("adminLogin") || document.getElementById("adminRoleBtn");
  if (adminBtn) {
    adminBtn.addEventListener("click", () => {
      openModal("adminLogin");
    });
  }

  const doctorBtn = document.getElementById("doctorLogin") || document.getElementById("doctorRoleBtn");
  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => {
      openModal("doctorLogin");
    });
  }

  const patientBtn = document.getElementById("patientLogin") || document.getElementById("patientRoleBtn");
  if (patientBtn) {
    patientBtn.addEventListener("click", () => {
      openModal("patientLogin");
    });
  }
};

async function adminLoginHandler() {
  const username = document.getElementById("username")?.value.trim() || "";
  const password = document.getElementById("password")?.value || "";

  if (!username || !password) {
    alert("Please enter your username and password.");
    return;
  }

  const admin = { username, password };

  try {
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(admin),
    });

    const data = await response.json();

    if (!response.ok) {
      alert(data.message || "Invalid credentials!");
      return;
    }

    if (data.token) {
      localStorage.setItem("token", data.token);
      selectRole("admin");
      const modal = document.getElementById("modal");
      if (modal) modal.style.display = "none";
    } else {
      alert("Invalid credentials!");
    }
  } catch (error) {
    console.error("Admin login error:", error);
    alert("Invalid credentials!");
  }
}

async function doctorLoginHandler() {
  const email = document.getElementById("email")?.value.trim() || "";
  const password = document.getElementById("password")?.value || "";

  if (!email || !password) {
    alert("Please enter your email and password.");
    return;
  }

  const doctor = { identifier: email, password };

  try {
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(doctor),
    });

    const data = await response.json();

    if (!response.ok) {
      alert(data.message || "Invalid credentials!");
      return;
    }

    if (data.token) {
      localStorage.setItem("token", data.token);
      selectRole("doctor");
      const modal = document.getElementById("modal");
      if (modal) modal.style.display = "none";
    } else {
      alert("Invalid credentials!");
    }
  } catch (error) {
    console.error("Doctor login error:", error);
    alert("Invalid credentials!");
  }
}

async function patientLoginHandler() {
  const email = document.getElementById("email")?.value.trim() || "";
  const password = document.getElementById("password")?.value || "";

  if (!email || !password) {
    alert("Please enter your email and password.");
    return;
  }

  const patient = { identifier: email, password };

  try {
    const response = await fetch(PATIENT_API, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(patient),
    });

    const data = await response.json();

    if (!response.ok) {
      alert(data.message || "Invalid credentials!");
      return;
    }

    if (data.token) {
      localStorage.setItem("token", data.token);
      if (typeof setRole === "function") {
        setRole("loggedPatient");
      } else {
        localStorage.setItem("userRole", "loggedPatient");
      }
      const modal = document.getElementById("modal");
      if (modal) modal.style.display = "none";
      window.location.href = "/pages/loggedPatientDashboard.html";
    } else {
      alert("Invalid credentials!");
    }
  } catch (error) {
    console.error("Patient login error:", error);
    alert("Invalid credentials!");
  }
}

window.adminLoginHandler = adminLoginHandler;
window.doctorLoginHandler = doctorLoginHandler;
window.patientLoginHandler = patientLoginHandler;
window.openModal = openModal;