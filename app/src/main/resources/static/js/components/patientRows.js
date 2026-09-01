// patientRows.js
export function createPatientRow(patient, appointmentId, doctorId) {
  const tr = document.createElement("tr");

  tr.innerHTML = `
    <td>${patient.id || "N/A"}</td>
    <td>${patient.name || "N/A"}</td>
    <td>${patient.phone || "N/A"}</td>
    <td>${patient.email || "N/A"}</td>
    <td>
      <img src="/assets/images/edit/edit.png" alt="Prescription" class="prescription-btn" style="cursor: pointer; width: 20px;" />
    </td>
  `;

  const btn = tr.querySelector(".prescription-btn");
  if (btn) {
    btn.addEventListener("click", () => {
      window.location.href = `/pages/addPrescription.html?appointmentId=${appointmentId}&doctorId=${doctorId}&patientName=${encodeURIComponent(patient.name || '')}`;
    });
  }

  return tr;
}// patientRows.js
