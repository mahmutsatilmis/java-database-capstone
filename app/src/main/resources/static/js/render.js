// render.js

function selectRole(role) {
  setRole(role);
  const token = localStorage.getItem('token');
  
  if (role === "admin") {
    if (token) {
      window.location.href = `/adminDashboard/${token}`;
    }
  } else if (role === "patient") {
    window.location.href = "/pages/patientDashboard.html";
  } else if (role === "doctor") {
    if (token) {
      window.location.href = `/doctorDashboard/${token}`;
    }
  } else if (role === "loggedPatient") {
    if (token) {
      window.location.href = "/pages/loggedPatientDashboard.html";
    } else {
      window.location.href = "/pages/patientDashboard.html";
    }
  }
}

function renderContent() {
  const isPatientPublicPage = window.location.pathname.includes("patientDashboard.html");
  if (isPatientPublicPage) {
    if (!getRole()) {
      setRole("patient");
    }
    return;
  }

  const role = getRole();
  if (!role) {
    window.location.href = "/";
    return;
  }
}