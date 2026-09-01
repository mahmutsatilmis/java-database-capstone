export function getAppointments(appointment) {
  const tr = document.createElement("tr");

  tr.innerHTML = `
    <td class="patient-id">${appointment.patientName || "N/A"}</td>
    <td>${appointment.doctorName || "N/A"}</td>
    <td>${appointment.appointmentDate || appointment.date || "N/A"}</td>
    <td>${appointment.appointmentTimeOnly || appointment.time || "N/A"}</td>
    <td>
      <img src="/assets/images/edit/edit.png" alt="action" class="prescription-btn" data-id="${appointment.id}" />
    </td>
  `;

  const btn = tr.querySelector(".prescription-btn");
  if (btn) {
    btn.addEventListener("click", () => {
      const targetId = appointment.id || appointment.appointmentId;
      window.location.href = `/pages/addPrescription.html?mode=view&appointmentId=${targetId}`;
    });
  }

  return tr;
}