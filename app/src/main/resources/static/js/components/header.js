function attachHeaderButtonListeners() {
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => {
      if (typeof window.openModal === "function") {
        window.openModal("addDoctor");
      }
    });
  }

  const patientLogin = document.getElementById("patientLogin");
  if (patientLogin) {
    patientLogin.addEventListener("click", () => {
      if (typeof window.openModal === "function") {
        window.openModal("patientLogin");
      }
    });
  }

  const patientSignup = document.getElementById("patientSignup");
  if (patientSignup) {
    patientSignup.addEventListener("click", () => {
      if (typeof window.openModal === "function") {
        window.openModal("patientSignup");
      }
    });
  }

  const homeBtn = document.getElementById("headerHomeBtn");
  if (homeBtn) {
    homeBtn.addEventListener("click", () => {
      window.location.href = "/pages/loggedPatientDashboard.html";
    });
  }

  const appointmentsBtn = document.getElementById("headerAppointmentsBtn");
  if (appointmentsBtn) {
    appointmentsBtn.addEventListener("click", () => {
      window.location.href = "/pages/patientAppointments.html";
    });
  }
}

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

function renderHeader() {
  const headerDiv = document.getElementById("header");
  if (!headerDiv) return;

  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole");
    localStorage.removeItem("token");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <img src="/assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>
    `;
    return;
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="/assets/images/logo/logo.png" alt="Hospital CMS Logo" class="logo-img">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav class="header-actions">
  `;

  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn" type="button">Add Doctor</button>
      <a href="#" onclick="logout(); return false;">Logout</a>
    `;
  } else if (role === "doctor") {
    headerContent += `
      <button id="headerHomeBtn" class="adminBtn" type="button">Home</button>
      <a href="#" onclick="logout(); return false;">Logout</a>
    `;
  } else if (role === "patient") {
    headerContent += `
      <button id="patientLogin" class="adminBtn" type="button">Login</button>
      <button id="patientSignup" class="adminBtn" type="button">Sign Up</button>
    `;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button id="headerHomeBtn" class="adminBtn" type="button">Home</button>
      <button id="headerAppointmentsBtn" class="adminBtn" type="button">Appointments</button>
      <a href="#" onclick="logoutPatient(); return false;">Logout</a>
    `;
  } else {
    headerContent += `
      <button id="patientLogin" class="adminBtn" type="button">Login</button>
      <button id="patientSignup" class="adminBtn" type="button">Sign Up</button>
    `;
  }

  headerContent += `
      </nav>
    </header>
  `;

  headerDiv.innerHTML = headerContent;
  attachHeaderButtonListeners();
}

window.renderHeader = renderHeader;
window.logout = logout;
window.logoutPatient = logoutPatient;
window.attachHeaderButtonListeners = attachHeaderButtonListeners;

renderHeader();

